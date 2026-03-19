package org.example.spont.event.controller;

import lombok.RequiredArgsConstructor;
import org.example.spont.auth.security.CustomUserDetails;
import org.example.spont.event.dto.CreateEventRequest;
import org.example.spont.event.dto.RequestJoinEventRequest;
import org.example.spont.event.dto.RequestJoinEventResponse;
import org.example.spont.event.entity.Event;
import org.example.spont.event.service.EventService;
import org.example.spont.participant.dto.ParticipantResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    @PostMapping("/create")
    public Event createEvent(@RequestBody CreateEventRequest request ,
                             @AuthenticationPrincipal CustomUserDetails user
                             ) {

        System.out.println("checkpoint: Event controller");

        Event event = eventService.createEvent(request,user.getUsername());
        return event;
    }

    @PostMapping("/request_join/{token}")
    public RequestJoinEventResponse requestJoin(
            @PathVariable String token,
            @RequestBody RequestJoinEventRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        RequestJoinEventResponse response = eventService.requestJoin(token, request, user);
        return  response;
    }

    @GetMapping("/{token}/participants/approved")
    public List<ParticipantResponse> participantList(
            @PathVariable String token,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        return eventService.fetchParticipantList(token,user);
    }

    @GetMapping("/{token}/participants/waiting")
    public List<ParticipantResponse> watingList(
            @PathVariable String token,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        return eventService.waitingList(token,user);
    }

    @PutMapping("/{token}/participant/{participantId}/{decision}")
    public void reviewParticipantRequest(
            @PathVariable String token,
            @PathVariable UUID participantId,
            @PathVariable String decision,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        eventService.reviewParticipantRequest(token,participantId,decision,user);

    }


    @GetMapping
    public Page<Event> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return eventService.getUpcomingEvents(page, size);
    }


}
