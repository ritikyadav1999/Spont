package org.example.spont.common.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PaginatedResponse<T> {
    private List<T> data;
    private int page;
    private boolean hasNext;

    public PaginatedResponse(Page<T> pageData) {
        this.data = pageData.getContent();
        this.page = pageData.getNumber();
        this.hasNext = pageData.hasNext();
    }
}