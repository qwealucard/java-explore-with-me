package ru.practicum.stats;

import ru.practicum.HitRequest;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void hit(HitRequest hitDto);

    List<ViewStats> stats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
