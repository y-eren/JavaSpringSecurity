package com.eazybytes.springsection1.config;


import com.eazybytes.springsection1.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("prod")
public class ProjectSecurityProdConfig {

    // Buradaki kod SpringBootWebSecuriyConfiguration sınıfından gelmektedir.
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
       // http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll()); // hepsine permitlemiş oluyoruz denyAll() metoduda mevcutuur
        http    .securityContext(contextConfig -> contextConfig.requireExplicitSave(false)) // security context holderin içerisinde herhangi bir jsessionid veya authenticated user store etmeyeceğim demektir
                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
//                .sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true)) // invalissession için kullanıyı yönlendirme maximum session sayısı
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)) // ayrı bir login formu olsada aynı jsessionid 'yi oluşturması için kullanılmaktadır
                .requiresChannel(rcc -> rcc.anyRequest().requiresSecure()) // https için
//                .csrf(csrfConfig -> csrfConfig.disable())
                .csrf(csrfConfig -> csrfConfig.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/myAccount").hasAuthority("VIEWACCOUNT")
                        .requestMatchers( "/myBalance").hasAuthority("VIEWBALANCE")
                        .requestMatchers( "/myLoans").hasAuthority("VIEWLOANS")
                        .requestMatchers( "/myCards").hasAuthority("VIEWCARDS")
                        .requestMatchers( "/user").authenticated()
                .requestMatchers("/notices", "/contact", "/error", "/register", "/invalidSession").permitAll()
        );
        http.formLogin(withDefaults());
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())); //https basic config
        return http.build();
    }

// Bu kodlar InMemory kullanım için geçerlidir
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withUsername("user").password("{noop}Eren@12345").authorities("read").build();
//        UserDetails admin = User.withUsername("admin").password("{bcrypt}$2a$12$Krk7EUXSLBKk.wN9aRAGSudnVOBV.S8I.SxYkMu.gOWGmWJfox2si")
//                .authorities("admin").build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }


    // Bu kullanım sadece hazır Jdbc tablolarını kullanıyorusan geçerlidir.
//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource) {
//
//        return new JdbcUserDetailsManager(dataSource);
//    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // compromised kişiler için bakılıyor
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
