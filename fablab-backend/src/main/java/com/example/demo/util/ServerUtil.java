package com.example.demo.util;

import lombok.SneakyThrows;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class ServerUtil {
    public static final String PORT = "8080";
    @SneakyThrows
    public static String getHostname() {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return "https://" + request.getServerName();
        }
        else
            return "https://peaceful-wave-55830.herokuapp.com";
    }
}
