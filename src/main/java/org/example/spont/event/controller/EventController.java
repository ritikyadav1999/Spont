package org.example.spont.event.controller;

import lombok.RequiredArgsConstructor;
import org.example.spont.auth.security.CustomUserDetails;
import org.example.spont.common.response.ApiResponse;
import org.example.spont.common.response.PaginatedResponse;
import org.example.spont.common.response.ResponseUtil;
import org.example.spont.event.dto.*;
import org.example.spont.event.entity.Event;
import org.example.spont.event.service.EventService;
import org.example.spont.participant.dto.ParticipantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
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

        Event event = eventService.createEvent(request,user.getUsername());
        return event;
    }

    @PostMapping("/request-join/{token}")
    public ResponseEntity<ApiResponse<RequestJoinEventResponse>> requestJoin(
            @PathVariable String token,
            @RequestBody RequestJoinEventRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        RequestJoinEventResponse response = eventService.requestJoin(token, request, user);
        return ResponseUtil.ok(response);
    }

    @GetMapping("/{token}/participants/approved")
    public ResponseEntity<ApiResponse<List<ParticipantResponse>>>participantList(
            @PathVariable String token,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        List<ParticipantResponse> participantResponses = eventService.fetchParticipantList(token, user);
        return ResponseUtil.ok(participantResponses);
    }

    @GetMapping("/{token}/participants/pending")
    public ResponseEntity<ApiResponse<List<ParticipantResponse>>> watingList(
            @PathVariable String token,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        List<ParticipantResponse> participantResponses = eventService.waitingList(token, user);
        return ResponseUtil.ok(participantResponses);
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
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<EventResponseDTO> upcomingEvents = eventService.getUpcomingEvents(page, size);
        return ResponseUtil.ok(upcomingEvents);
    }

    @GetMapping("/my-events/hosting")
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getMyHostingEvents(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int hostingPage
    ) {
        Page<EventResponseDTO> myEvents = eventService.getMyHostingEvents(user.getUserId(), hostingPage);
        return ResponseUtil.ok(myEvents);
    }

    @GetMapping("/my-events/attending")
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getMyAttendingEvents(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int attendingPage
    ) {
        Page<EventResponseDTO> myEvents = eventService.getMyAttendingEvents(user.getUserId(),attendingPage);
        return ResponseUtil.ok(myEvents);
    }

    @GetMapping("/my-events/past")
    public ResponseEntity<ApiResponse<Page<MyPastEvents>>> getPastEvents(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page
    ) {
        Page<MyPastEvents> pastEvents = eventService.getPastEvents(user.getUserId(), page);
        return ResponseUtil.ok(pastEvents);
    }


    @GetMapping("/{token}")
    public ResponseEntity<ApiResponse<EventResponseDTO>> getEventByToken(@PathVariable String token){
        EventResponseDTO eventResponseDTO = eventService.fetchEventByToken(token);
        return ResponseUtil.ok(eventResponseDTO);
    }


}
