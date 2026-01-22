package ru.yandex.practicum.smarthome.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
    private boolean first;
    private boolean last;
    private boolean empty;
    private List<SortOrder> sort;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SortOrder {
        private String property;
        private String direction;
    }

    public static <T> PageResponseDto<T> fromPage(Page<T> page) {
        List<SortOrder> sortOrders = page.getSort().stream()
                .map(order -> SortOrder.builder()
                        .property(order.getProperty())
                        .direction(order.getDirection().name())
                        .build())
                .collect(Collectors.toList());

        return PageResponseDto.<T>builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .sort(sortOrders)
                .build();
    }
}
