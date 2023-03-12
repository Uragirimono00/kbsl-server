package com.kbsl.server.boot.advisor;

import com.kbsl.server.boot.dto.response.CommonResponseDto;
import com.kbsl.server.boot.exception.RestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {
    @ExceptionHandler(RestException.class)
    public ResponseEntity<CommonResponseDto> restExceptionHandler(RestException e) {
        e.printStackTrace();
        CommonResponseDto responseDto = CommonResponseDto.builder()
                .success(false)
                .status(e.getHttpStatus().value())
                .message(e.getMessage())
                .data(new ArrayList<>())
                .build();
        return new ResponseEntity<>(responseDto, e.getHttpStatus());
    }
}