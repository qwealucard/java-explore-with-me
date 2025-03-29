package ru.practicum;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HitRequest {
    @NotNull
    private String app;
    private String uri;
    @NotNull
    private String ip;
    @NotNull
    private String timestamp;
}
