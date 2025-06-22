package com.pg.payment.util;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MdcLoggingFilter extends OncePerRequestFilter {
    
    private static final String TRACE_ID_KEY = "traceId";
    private static final String URI_KEY = "requestURI";
    private static final String METHOD_KEY = "requestMethod";
    
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws IOException, ServletException {
        
        try {
            String traceId = generateTraceId();
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(URI_KEY, request.getRequestURI());
            MDC.put(METHOD_KEY, request.getMethod());
            
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
    
    private String generateTraceId() {
        long time = System.currentTimeMillis();
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return time + "-" + rand;
    }
    
}
