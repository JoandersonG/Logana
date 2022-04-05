package dev.joanderson.Logana.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.joanderson.Logana.model.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {

    private List<Log> data = new ArrayList<>();

    public boolean isJSONValid(String jsonInString) {
        try {
            return new ObjectMapper().readTree(jsonInString) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private long parseId(JSONObject log) throws JSONException {
        return (long) (int) log.get("id");
    }

    private String parseTitle(JSONObject log) throws JSONException {
        return (String) log.get("title");
    }

    private int parseTimeSpent(JSONObject log) throws JSONException {
        return (int) log.get("timeSpent");
    }

    private String parseDescription(JSONObject log) throws JSONException {
        return (String) log.get("description");
    }

    private LocalDateTime parseCreatedAt(JSONObject log) throws JSONException {
        var startDate = (String) log.get("createdAt");
        return ZonedDateTime.parse(startDate).toLocalDateTime();
    }

    public Log create(JSONObject jsonLog) throws JSONException {
        LocalDateTime now = LocalDateTime.now();
        return new Log(
          parseId(jsonLog),
          parseTitle(jsonLog),
          parseTimeSpent(jsonLog),
          parseDescription(jsonLog),
          now,
          now
        );
    }

    public Log update(Log log, JSONObject jsonLog) throws JSONException {
        log.setTitle(parseTitle(jsonLog));
        log.setTimeSpent(parseTimeSpent(jsonLog));
        log.setDescription(parseDescription(jsonLog));
        log.setEditedAt(LocalDateTime.now());
        return log;
    }

    public List<Log> find() {
        return data;
    }

    public void add(Log log) {
        data.add(log);
    }

    public Log getLog(JSONObject jsonObject) throws JSONException {
        long id = parseId(jsonObject);
        return getById(id);
    }

    public Log getById(Long id) {
        return data.stream().filter(log -> log.getId() == id).findAny().orElse(null);
    }

    public boolean delete(Long id) {
        return data.removeIf(log -> log.getId() == id);
    }
}
