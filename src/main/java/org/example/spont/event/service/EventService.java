package org.example.spont.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.spont.auth.security.CustomUserDetails;
import org.example.spont.auth.security.JwtService;
import org.example.spont.event.dto.CreateEventRequest;
import org.example.spont.event.dto.RequestJoinEventRequest;
import org.example.spont.event.dto.RequestJoinEventResponse;
import org.example.spont.event.entity.Event;
import org.example.spont.event.entity.EventStatus;
import org.example.spont.event.entity.JoinMode;
import org.example.spont.event.repository.EventRepo;
import org.example.spont.notification.entity.NotificationType;
import org.example.spont.notification.service.NotificationService;
import org.example.spont.participant.dto.ParticipantResponse;
import org.example.spont.participant.entity.Participant;
import org.example.spont.participant.entity.ParticipantRole;
import org.example.spont.participant.service.ParticipantService;
import org.example.spont.user.entity.User;
import org.example.spont.user.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepo  eventRepo;
    private final UserService userService;
    private final ParticipantService  participantService;
    private final JwtService  jwtService;
    private final NotificationService notificationService;

    @Transactional
    public Event createEvent(CreateEventRequest request, String creatorId)    {

        try{
            System.out.println("checkpoint: Event Service");


            UUID uuid = UUID.fromString(creatorId);

            System.out.println("checkpoint: creatorID " + creatorId);


            User creator = userService.findUserById(uuid)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate time
            if (request.endTime() != null && request.endTime().isBefore(request.startTime())) {
                throw new IllegalArgumentException("End time must be after start time");
            }

            Event event = new Event();

            event.setCreator(creator);
            event.setTitle(request.title());
            event.setDescription(request.description());
            event.setLocationName(request.locationName());
            event.setLatitude(request.latitude());
            event.setLongitude(request.longitude());
            event.setStartTime(request.startTime());
            event.setEndTime(request.endTime());
            event.setMaxParticipants(request.maxParticipants());
            event.setVisibility(request.visibility());
            event.setJoinMode(request.joinMode());
            event.setInviteToken(UUID.randomUUID().toString().substring(0, 8));

            // Set status
            if (request.startTime().isAfter(Instant.now())) {
                event.setStatus(EventStatus.SCHEDULED);
            } else {
                event.setStatus(EventStatus.ONGOING);
            }

            Event savedEvent = eventRepo.save(event);

            participantService.addParticipant(savedEvent,creator, ParticipantRole.HOST);

            return savedEvent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public RequestJoinEventResponse requestJoin(String inviteToken, RequestJoinEventRequest request, CustomUserDetails userDetails) {


        Event event = eventRepo.findByInviteToken(inviteToken)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User user;
        String jwtToken = null;

        // CASE 1: Logged in user
        if (userDetails != null) {
            user = userService.findUserById(userDetails.getUser().getUserId())
                    .orElseThrow();
        }

        // CASE 2: Not logged in
        else {

            Optional<User> existingUser = userService.findUserByPhone(request.phone());

            if (existingUser.isPresent())
                user = existingUser.get();
            else
                user = userService.guestRegistration(request.name(),request.phone(),request.gender());

            jwtToken = jwtService.generateToken(user.getUserId().toString());
        }

        // Check event status
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalStateException("Event is not joinable");
        }

        ParticipantRole role;

        if(event.getJoinMode() == JoinMode.OPEN)
            role = ParticipantRole.MEMBER;
        else
            role = ParticipantRole.PENDING;

        try {
            participantService.addParticipant(event, user, role);

        } catch (DataIntegrityViolationException ex) {

            throw new IllegalStateException("Unable to join event (duplicate or full)");
        }

        notificationService.createNotification(
                event.getCreator().getUserId(),
                "join_request",
                user.getName() + " requested to join your event"
        );


        return new RequestJoinEventResponse(jwtToken,role);


    }

    public List<ParticipantResponse> fetchParticipantList(String token, CustomUserDetails user) {

        Event event = eventRepo.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<ParticipantResponse> participants = participantService.fetchParticipantsByEventId(event.getEventId());

        return participants;

    }

    public List<ParticipantResponse> waitingList(String token, CustomUserDetails user) {
        Event event = eventRepo.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return participantService.fetchWaitingList(event.getEventId());

    }

    @Transactional
    public void reviewParticipantRequest(String token, UUID participantId, String decision, CustomUserDetails user) {

        Event event = eventRepo.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User creator = userService.findUserById(user.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(event.getCreator().getUserId() != creator.getUserId()) {
            throw new RuntimeException("Unauthorized");
        }

        participantService.updateParticipantRole(participantId,decision);

        notificationService.createNotification(
                participantId,
                decision,
                "Your request was " + decision.toLowerCase()
        );
    }

    public Page<Event> getUpcomingEvents(int page,int size){
        return eventRepo.getUpcomingEvents(
                PageRequest.of(page, size)
        );
    }

}

