package com.kbsl.server.score.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Score", description = "점수 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/score")
public class ScoreController {
}
