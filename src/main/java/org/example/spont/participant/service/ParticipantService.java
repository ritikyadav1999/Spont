package org.example.spont.participant.service;

import lombok.RequiredArgsConstructor;
import org.example.spont.event.entity.Event;
import org.example.spont.participant.dto.ParticipantResponse;
import org.example.spont.participant.entity.Participant;
import org.example.spont.participant.entity.ParticipantRole;
import org.example.spont.participant.repository.ParticipantRepo;
import org.example.spont.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepo participantRepo;

    public Participant findById(UUID id) {
        return participantRepo.findById(id).orElse(null);
    }


    public void addParticipant(Event event, User user,ParticipantRole role) {
        Participant participant = new Participant();
        participant.setEvent(event);
        participant.setUser(user);

        if(role == ParticipantRole.HOST)
            participant.setJoinedAt(Instant.now());

        participant.setRole(role);
        participantRepo.save(participant);

    }

    public List<ParticipantResponse> fetchParticipantsByEventId(UUID eventId) {
        return participantRepo.findByEventId(eventId);
    }

    public List<ParticipantResponse> fetchWaitingList(UUID eventId) {
        return participantRepo.findWaitingParticipantsByEventId(eventId);
    }

    public Participant updateParticipantRole(UUID participantId, String decision){

        Participant participant = participantRepo.findById(participantId).orElseThrow();

        if(decision.equalsIgnoreCase("approved"))
            participant.setRole(ParticipantRole.MEMBER);
        else if (decision.equalsIgnoreCase("co_host"))
            participant.setRole(ParticipantRole.CO_HOST);
        else
            participant.setRole(ParticipantRole.REJECTED);


        return participantRepo.save(participant);

    }

    public boolean isCoHost(UUID eventId, UUID userId){
        String role = participantRepo.getRole(eventId, userId);
        return role.equals("CO_HOST");
    }

}
