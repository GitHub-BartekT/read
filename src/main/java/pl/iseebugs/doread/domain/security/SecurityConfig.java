package pl.iseebugs.doread.domain.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import pl.iseebugs.doread.domain.user.AppUserFacade;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    JWTAuthFilter jwtAuthFilter;
    AppUserFacade appUserFacade;
    FilterChainExceptionHandler filterChainExceptionHandler;


    SecurityConfig(JWTAuthFilter jwtAuthFilter, AppUserFacade appUserFacade, FilterChainExceptionHandler filterChainExceptionHandler){
        this.jwtAuthFilter = jwtAuthFilter;
        this.appUserFacade = appUserFacade;
        this.filterChainExceptionHandler = filterChainExceptionHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .addFilterBefore(filterChainExceptionHandler, LogoutFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**",
                                "/favicon.ico",
                                "/home.html",
                                "/about.html",
                                "/dashboard.html",
                                "/session.html",
                                "/modules.html",
                                "/account.html",
                                "/api/auth/delete/delete-confirm",
                                "/changelog.html",
                                "/RODO.html").permitAll()
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/api/auth",
                                "/api/auth/create",
                                "/api/auth/create/**",
                                "/api/auth/confirm",
                                "/api/auth/signin").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources/**").permitAll()
                        .requestMatchers(
                                "/api/auth/users",
                                "/api/auth/delete",
                                "/api/auth/delete/**",
                                "/api/allModules",
                                "/api/auth/users/password",
                                "/api/auth/refresh",
                                "/api/dashboard",
                                "/api/session",
                                "/api/module",
                                "/api/module/**",
                                "/dashboard"
                                )
                        .hasAnyAuthority("USER")
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthFilter, UsernamePasswordAuthenticationFilter.class
                );
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new AppUserInfoService(appUserFacade);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}