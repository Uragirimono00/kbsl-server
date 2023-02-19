package com.kbsl.server.example.web;

import com.kbsl.server.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {private final AuthService authService;


    @GetMapping
    public ResponseEntity<?> test(@RequestParam String param) {
        return new ResponseEntity<>(param, HttpStatus.OK);
    }

    @GetMapping("/authtest")
    public ResponseEntity<?> authtest() throws Exception {
        return new ResponseEntity<>(authService.getUserName(), HttpStatus.OK);
    }
}
