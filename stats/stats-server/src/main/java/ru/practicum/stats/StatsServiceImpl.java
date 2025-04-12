package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.HitRequest;
import ru.practicum.ViewStats;
import ru.practicum.exception.ValidationException;
import ru.practicum.mappers.HitMapper;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;

    @Override
    public HitRequest hit(HitRequest hitDto) {
        Hit hit = HitMapper.toEntity(hitDto);
        hitRepository.save(hit);
        return HitMapper.toHitRequestDto(hit);
    }

    @Override
    public List<ViewStats> stats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Start date must be before end date");
        }

        List<Object[]> hits;

        if (uris == null) {
            if (unique) {
                hits = hitRepository.findUniqueHitsWithoutUri(start, end);
            } else {
                hits = hitRepository.findHitsWithoutUri(start, end);
            }
        } else {
            if (unique) {
                hits = hitRepository.findUniqueHitsWithUri(start, end, uris);
            } else {
                hits = hitRepository.findHitsWithUri(start, end, uris);
            }
        }
        return hits.stream()
                   .map(hit -> new ViewStats(
                           hit[0].toString(),
                           hit[1].toString(),
                           (Long) hit[2]
                   ))
                   .toList();
    }
}
