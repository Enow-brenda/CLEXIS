package com.brenda.clexis.clientGatewayService.Config;


import com.brenda.clexis.clientGatewayService.exceptions.CustomAccessDeniedHandler;
import com.brenda.clexis.clientGatewayService.exceptions.CustomAuthenticationEntryPoint;
import com.brenda.clexis.clientGatewayService.service.implementations.OurUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private static final String[] WHITE_LIST_URL = { "/api/v1/gateway/authentication/**",
            "/v3/api-docs/**", "/swagger-resources/**","/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html",
             };
    private static final String[] ADMIN_URL = {
            "/api/v1/gateway/config/**","/api/v1/gateway/role/**","/api/v1/gateway/subscription/**","/api/v1/gateway/user/**","/api/v1/gateway/platform/restricted/**",
    };
    private static final String[] STUDENT_URL = {
            "/api/v1/gateway/core/**","api/v1/gateway/core/ussd/**"
    };

    private final OurUserDetailsService ourUserDetailsService;
    private final JWTAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers(WHITE_LIST_URL).permitAll()
                        .requestMatchers(ADMIN_URL).hasAnyAuthority("ADMIN")
                        .requestMatchers(STUDENT_URL).hasAnyAuthority("STUDENT")
                        .requestMatchers("/api/v1/gateway/platform/limited/**").hasAnyAuthority("PLATFORM","ADMIN")
                    .anyRequest().authenticated())
            .exceptionHandling(exception -> exception
                .accessDeniedHandler(customAccessDeniedHandler) // Set custom access denied handler
                .authenticationEntryPoint(customAuthenticationEntryPoint) //set custom authentication entry point
        )
                .sessionManagement(manager-> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthFilter, UsernamePasswordAuthenticationFilter.class
                );
        return httpSecurity.build();
    }



    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(ourUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
