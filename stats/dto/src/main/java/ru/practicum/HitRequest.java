package ru.practicum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HitRequest {
    @NotNull
    private String app;
    private String uri;
    @NotNull
    private String ip;
    @NotNull
    private String timestamp;
}
