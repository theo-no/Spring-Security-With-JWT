package com.theono.securitywithjwt.filter;

import com.theono.securitywithjwt.exception.ErrorStatusException;
import com.theono.securitywithjwt.util.RequestResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class HandleErrorStatusExceptionFilter extends GenericFilterBean {

    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ErrorStatusException ex) {
            RequestResponseUtil.setResponseToErrorResponse(
                    (HttpServletResponse) servletResponse, ex.getErrorCase());
        }
    }
}
