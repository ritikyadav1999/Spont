package org.example.spont.event.dto;

import org.example.spont.common.response.PaginatedResponse;
import org.example.spont.event.entity.Event;


public record MyEventsResponse(
        PaginatedResponse<EventResponseDTO> hosting,
        PaginatedResponse<EventResponseDTO> attending
) {
}
