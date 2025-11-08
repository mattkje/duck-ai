package no.mattikj.mkd.duckai.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Actuator Controller class. Provides basic health check endpoint.
 *
 * @author Matti Kjellstadli
 * @version 1.1.0
 */
@RestController
@RequestMapping("/api/actuator")
public class ActuatorController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        final Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        return ResponseEntity.ok(status);
    }

    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> version() {
        final Map<String, String> versionInfo = new HashMap<>();
        String version = getClass().getPackage().getImplementationVersion();
        versionInfo.put("version",
                        version != null
                        ? version
                        : "unknown");
        return ResponseEntity.ok(versionInfo);
    }
}
