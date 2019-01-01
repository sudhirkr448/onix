package org.gatblau.onix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private OnixBasicAuthEntryPoint authenticationEntryPoint;

    @Value("${wapi.auth.mode}")
    private String authMode;

    @Value("${wapi.admin.user}")
    private String adminUsername;

    @Value("${wapi.admin.pwd}")
    private String adminPassword;

    @Value("${wapi.reader.user}")
    private String readerUsername;

    @Value("${wapi.reader.pwd}")
    private String readerPassword;

    @Value("${wapi.writer.user}")
    private String writerUsername;

    @Value("${wapi.writer.pwd}")
    private String writerPassword;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        if (authMode.equals("basic")) {
            auth.inMemoryAuthentication()
                .withUser(readerUsername).password(passwordEncoder().encode(readerPassword)).roles("READER").and()
                .withUser(writerUsername).password(passwordEncoder().encode(writerPassword)).roles("WRITER").and()
                .withUser(adminUsername).password(passwordEncoder().encode(adminPassword)).roles("ADMIN");
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (authMode.equals("basic")) {
            http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers(HttpMethod.GET, "(item|link)/**").hasRole("READER")
                .antMatchers(HttpMethod.PUT, "(item|link)/**").hasRole("WRITER")
                .antMatchers(HttpMethod.DELETE, "(item|link)/**").hasRole("WRITER")
                .antMatchers("/**").hasRole("ADMIN")
                // http basic authentication
                .and().httpBasic()
                // 401 challenge response configuration
                .authenticationEntryPoint(authenticationEntryPoint)
                // No need session.
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
        else if (authMode.equals("none")) {
            http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/**").permitAll().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}