package dev.joanderson.Logana.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Log {
    private long id;
    private String title;
    private int timeSpent;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
}
