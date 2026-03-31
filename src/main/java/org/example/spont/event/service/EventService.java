package org.example.spont.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.spont.auth.security.CustomUserDetails;
import org.example.spont.auth.security.JwtService;
import org.example.spont.common.response.PaginatedResponse;
import org.example.spont.event.dto.*;
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
import org.example.spont.utils.TextUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            UUID uuid = UUID.fromString(creatorId);

            User creator = userService.findUserById(uuid)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate time
            if (request.endTime() != null && request.endTime().isBefore(request.startTime())) {
                throw new IllegalArgumentException("End time must be after start time");
            }

            Event event = new Event();

            event.setCreator(creator);
            event.setTitle(TextUtil.normalize(request.title()));
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

        // Check event status
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalStateException("Event is not joinable");
        }


        User user;
        String jwtToken = null;

        // CASE 1: Logged in user
        if (userDetails != null) {
            user = userService.findUserById(userDetails.getUser().getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        else{
            throw new RuntimeException("User must sign in to join event");
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
                event.getInviteToken(),
                "join_request",
                user.getName() + " requested to join your event"
        );


        return new RequestJoinEventResponse(jwtToken,role);


    }

    public List<ParticipantResponse> fetchParticipantList(String token) {

        Event event = eventRepo.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<ParticipantResponse> participants = participantService.fetchParticipantsByEventId(event.getEventId());

        return participants;

    }

    public List<ParticipantResponse> waitingList(String token) {
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

        boolean isCoHost = participantService.isCoHost(event.getEventId(),user.getUserId());

        if(!event.getCreator().getUserId().equals(creator.getUserId()) && !isCoHost) {
            throw new RuntimeException("Unauthorized");
        }

        Participant participant = participantService.updateParticipantRole(participantId, decision);

        String message;

        if (decision.equalsIgnoreCase("co_host")) {
            message = "You have been promoted to co-host.";
        } else {
            message = "Your request was " + decision.toLowerCase() + ".";
        }

        notificationService.createNotification(
                participant.getUser().getUserId(),
                event.getInviteToken(),
                decision,
                message
        );
    }

    public Page<EventResponseDTO> getUpcomingEvents(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Event> events = eventRepo.getUpcomingEvents(pageable);

        return events.map(this::mapToDto);
    }

    private EventResponseDTO mapToDto(Event event) {
        return new EventResponseDTO(
                event.getEventId(),
                event.getInviteToken(),
                event.getTitle(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocationName(),
                event.getLatitude(),
                event.getLongitude(),
                event.getStatus().name(),
                event.getJoinMode().name(),
                event.getVisibility().name(),
                event.getMaxParticipants(),
                new UserSummaryDTO(
                        event.getCreator().getUserId(),
                        event.getCreator().getName()
                )
        );
    }

    public Page<EventResponseDTO> getMyHostingEvents(UUID userId, int hostingPage) {

        Pageable hostingPageable = PageRequest.of(hostingPage, 5);

        Page<Event> hosting = eventRepo.getHostingEvents(userId, hostingPageable);

        // 🔥 Convert to DTO
        Page<EventResponseDTO> hostingDto = hosting.map(this::mapToDto);

        return hostingDto;
    }

    public Page<EventResponseDTO> getMyAttendingEvents(UUID userId, int attendingPage) {

        Pageable attendingPageable = PageRequest.of(attendingPage, 10);

        Page<Event> attending = eventRepo.getAttendingEvents(userId, attendingPageable);

        Page<EventResponseDTO> attendingDto = attending.map(this::mapToDto);

        return attendingDto;
    }


    public Page<MyPastEvents> getPastEvents(UUID userId, int page) {

        Pageable pageable = PageRequest.of(page, 15);

        Page<MyPastEvents> past = eventRepo.getPastEvents(userId, pageable);

        return past;
    }

    public EventResponseDTO fetchEventByToken(String token){
        Event event = eventRepo.findByInviteToken(token).orElseThrow(() -> new RuntimeException("Event not found"));
        EventResponseDTO eventResponseDTO = mapToDto(event);
        return eventResponseDTO;

    }

}

