package ru.practicum.stats;

import org.springframework.stereotype.Component;
import ru.practicum.HitRequest;
import ru.practicum.ViewStats;
import ru.practicum.mappers.HitMapper;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatsServiceImpl implements StatsService {

    HitRepository hitRepository;

    public StatsServiceImpl(HitRepository hitRepository) {
        this.hitRepository = hitRepository;
    }


    @Override
    public void hit(HitRequest hitDto) {
        Hit hit = HitMapper.toEntity(hitDto);
        hitRepository.save(hit);
    }

    @Override
    public List<ViewStats> stats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
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
                           (long) hit[2]
                   ))
                   .toList();
    }
}
