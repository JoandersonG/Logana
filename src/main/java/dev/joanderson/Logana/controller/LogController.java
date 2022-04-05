package dev.joanderson.Logana.controller;

import dev.joanderson.Logana.model.Log;
import dev.joanderson.Logana.service.LogService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/logana/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping
    public ResponseEntity<List<Log>> getAllLogs() {
        if (logService.find().isEmpty()) {
            System.out.println("No logs were found");
            return ResponseEntity.notFound().build();
        }
        System.out.println("Logs found");
        return ResponseEntity.ok(logService.find());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Log> getLog(@PathVariable long id) {
        Log logFound = logService.getById(id);
        if (logFound == null) {
            System.out.println("Log not found");
            return ResponseEntity.notFound().build();
        } else {
            System.out.println("Log requested was found");
            return ResponseEntity.ok(logFound);
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Log> create(@RequestBody String log) {
        try {
            JSONObject logJsonObject = new JSONObject(log);
            return createLogIfValid(logJsonObject);
        } catch (JSONException e) {
            System.out.println("JSON fields are not parsable");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
    }

    private ResponseEntity<Log> createLogIfValid(JSONObject logJsonObject) throws JSONException {
        if (logService.isJSONValid(logJsonObject.toString())) {
            Log logCreated = logService.create(logJsonObject);
            var uri = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path(String.valueOf(logCreated.getId()))
                    .build()
                    .toUri();
            logService.add(logCreated);
            System.out.println("Log service created successfully");
            return ResponseEntity.created(uri).body(null);
        } else {
            System.out.println("Failed to create log: Invalid JSON provided");
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Log> update(@RequestBody String jsonLog) {
        try {
            return updateLogIfValid(jsonLog);
        } catch (JSONException e) {
            System.out.println("JSON fields are not parsable");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
    }

    private ResponseEntity<Log> updateLogIfValid(String jsonLog) throws JSONException {
        JSONObject logJsonObject = new JSONObject(jsonLog);
        if (logService.isJSONValid(jsonLog)) {
            Log logToUpdate = logService.getLog(logJsonObject);
            return updateLogIfExists(logJsonObject, logToUpdate);
        } else {
            System.out.println("Failed to create log: Invalid JSON provided");
            return ResponseEntity.badRequest().body(null);
        }
    }

    private ResponseEntity<Log> updateLogIfExists(JSONObject logJsonObject, Log logToUpdate) throws JSONException {
        if (logToUpdate == null) {
            System.out.println("Log not found");
            return ResponseEntity.notFound().build();
        } else {
            logToUpdate = logService.update(logToUpdate, logJsonObject);
            System.out.println("Log service updated successfully");
            return ResponseEntity.ok(logToUpdate);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        if (logService.delete(id)) {
            System.out.println("Log deleted successfully");
            return ResponseEntity.ok(true);
        }
        System.out.println("Unable to find Log to delete");
        return ResponseEntity.notFound().build();
    }

}
