package com.eazybytes.springsection1.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
// Bu sınıf csrf sadece istendiği zaman lazy olarak değil de manuel olarak da çağrılabilinsin diye tasarlanmıştır

public class CsrfCookieFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        // token değerini cookieye atarak ilgii cookienin load edilmesini sağlıyor
        csrfToken.getToken();
        // filter chain ile iletim
        filterChain.doFilter(request, response);
    }
}
