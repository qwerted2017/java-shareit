package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemOutDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingOutDto lastBooking;
    @ToString.Exclude
    private List<CommentOutDto> comments;
    private BookingOutDto nextBooking;
    private Long requestId;

    public ItemOutDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @Override
    public String toString() {
        return "ItemOutDto{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", available=" + available +
                ", requestId=" + requestId +
                ", nextBooking=" + nextBooking +
                ", lastBooking=" + lastBooking +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemOutDto that = (ItemOutDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(available, that.available) && Objects.equals(lastBooking, that.lastBooking) && Objects.equals(nextBooking, that.nextBooking) && Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, lastBooking, nextBooking, requestId);
    }
}