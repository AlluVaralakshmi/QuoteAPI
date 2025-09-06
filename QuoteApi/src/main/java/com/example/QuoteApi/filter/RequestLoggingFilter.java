package com.example.QuoteApi.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@Component
public class RequestLoggingFilter extends HttpFilter {
	private final Logger logger = Logger.getLogger(RequestLoggingFilter.class.getName());


	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {


		String ip = extractClientIp(request);
		String path = request.getRequestURI();


		// continue -- we will log status after chain
		chain.doFilter(request, response);


		int status = response.getStatus();
		logger.info(String.format("ClientIP=%s Method=%s Path=%s Status=%d", ip, request.getMethod(), path, status));
	}


	private String extractClientIp(HttpServletRequest request) {
		String xf = request.getHeader("X-Forwarded-For");
		if (xf != null && !xf.isBlank()) {
			return xf.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}
}