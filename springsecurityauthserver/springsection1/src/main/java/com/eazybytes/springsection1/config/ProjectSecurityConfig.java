package com.eazybytes.springsection1.config;


import com.eazybytes.springsection1.exceptionhandling.CustomAccessDeniedHandler;
import com.eazybytes.springsection1.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import com.eazybytes.springsection1.filter.CsrfCookieFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("!prod")
public class ProjectSecurityConfig {


    /*@Value("${spring.security.oauth2.resourceserver.opaque.introspection-uri}")
    String introspectionUri;

    @Value("${spring.security.oauth2.resourceserver.opaque.introspection-client-id}")
    String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque.introspection-client-secret}")
    String clientSecret;*/

    // Buradaki kod SpringBootWebSecuriyConfiguration sınıfından gelmektedir.
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
       // http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll()); // hepsine permitlemiş oluyoruz denyAll() metoduda mevcutuur
    // bu sınıf csrf tokeninin cookie kısmının ve attribute header kısmı olarak ikiye ayrılıyor bu ikiye ayrılmada attribute kısmı için tasarlanmıştır
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloackRoleConverter());
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        http    .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
                .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // bunu stateless yaptığımız zaman jsessionidnin sunucuda barındırılmasına gerek kalmıyor
                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // only http
//                .csrf(csrfConfig -> csrfConfig.disable())
                .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                        .ignoringRequestMatchers("/contact", "/register") // bu apiler public olduğu için csrf protectiona görmezden gel diyoruz
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class) // http basic format kullandığımız için credentialsı extract etmek için kullanılıyor


                .authorizeHttpRequests((requests) -> requests
//                        .requestMatchers("/myAccount").hasAuthority("VIEWACCOUNT")
//                        .requestMatchers( "/myBalance").hasAuthority("VIEWBALANCE")
//                        .requestMatchers( "/myLoans").hasAuthority("VIEWLOANS")
//                        .requestMatchers( "/myCards").hasAuthority("VIEWCARDS")
                                .requestMatchers("/myAccount").hasRole("USER")
                                .requestMatchers( "/myBalance").hasAnyRole("ADMIN", "ROLE")
                                .requestMatchers( "/myLoans").authenticated()
                                .requestMatchers( "/myCards").hasRole("USER")

                        .requestMatchers( "/user").authenticated()
                .requestMatchers("/notices", "/contact", "/error", "/register").permitAll()
        );
      http.oauth2ResourceServer(rsc -> rsc.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));
       /*http.oauth2ResourceServer(rsc -> rsc.opaqueToken(otc -> otc.authenticationConverter(new KeycloakOpaqueRoleConverter())
                .introspectionUri(this.introspectionUri).introspectionClientCredentials(this.clientId,this.clientSecret)));*/
              // oauth2resourceserver sayesinde jwt configurer ile keycloackdan convert ettiğimiz nesneleri config etmiş oluyorz
        // global yapmanın avantajı sadece loginde değil bütün framework boyunca exception sağlamasıdır ve bu daha iyidir
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler())); // global olarak exception üretmektedir
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







}
