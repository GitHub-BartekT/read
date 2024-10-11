package pl.iseebugs.doread.domain.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;

import java.io.IOException;

@Log4j2
@Component
public class FilterChainExceptionHandler extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // Przechwyć wyjątek i zwróć odpowiednią odpowiedź bez dalszego rzucania wyjątku
            log.warn("JWT has expired: {}", e.getMessage());

            // Ustaw status 401 Unauthorized w odpowiedzi
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Zwróć odpowiedź JSON z informacją o błędzie
            String jsonResponse = new ObjectMapper().writeValueAsString(
                    ApiResponseFactory.createResponseWithoutData(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "JWT expired: " + e.getMessage()));

            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            // Obsłuż inne wyjątki
            log.error("Spring Security Filter Chain Exception:", e);
            resolver.resolveException(request, response, null, e);
        }
    }
}