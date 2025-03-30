package ru.practicum.mappers;

import ru.practicum.HitRequest;
import ru.practicum.stats.Hit;

import java.time.LocalDateTime;

public class HitMapper {
    public static Hit toEntity(HitRequest hitDto) {
        return new Hit(
                null,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                LocalDateTime.now()
        );
    }
}
