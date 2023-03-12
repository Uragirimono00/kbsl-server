package com.kbsl.server.boot.advisor;

import com.kbsl.server.boot.dto.response.CommonResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RestControllerAdvice
public class RestResponseAdvisor<T> implements ResponseBodyAdvice<T> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        String className = returnType.getContainingClass().getSimpleName();
        if(className.equals("BasicErrorController") || className.equals("CustomExceptionHandler") || className.equals("OpenApiControllerWebMvc") || className.equals("ApiResourceController") )
            return false;
        return true;
    }
    @Override
    public T beforeBodyWrite(T body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 공통 Response 생성
        ServletServerHttpResponse httpResponse = (ServletServerHttpResponse) response;
        Integer status = httpResponse.getServletResponse().getStatus();

        CommonResponseDto responseDto = CommonResponseDto.builder()
                .success(true)
                .status(status)
                .message("")
                .data(body)
                .build();

        log.info(responseDto.toString());
        return (T) responseDto;
    }


}