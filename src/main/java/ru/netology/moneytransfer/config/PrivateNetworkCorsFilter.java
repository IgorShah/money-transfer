package ru.netology.moneytransfer.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PrivateNetworkCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String privateNetworkHeader = httpRequest.getHeader("Access-Control-Request-Private-Network");
        if ("true".equalsIgnoreCase(privateNetworkHeader)) {
            httpResponse.setHeader("Access-Control-Allow-Private-Network", "true");
        }

        chain.doFilter(request, response);
    }
}
