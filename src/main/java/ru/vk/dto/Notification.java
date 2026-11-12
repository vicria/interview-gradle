package ru.vk.dto;

import java.time.Instant;

public record Notification(String name, Instant instant) implements Comparable<Notification> {
    @Override
    public int compareTo(Notification o) {
        return this.instant.isBefore(o.instant) ? -1 : +1;
    }
}
