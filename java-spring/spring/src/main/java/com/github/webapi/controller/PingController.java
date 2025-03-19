package com.github.webapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling Ping requests.
 */
@RestController
@RequestMapping("/api/ping")
public class PingController {

    @GetMapping
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("Healthy");
    }

}
