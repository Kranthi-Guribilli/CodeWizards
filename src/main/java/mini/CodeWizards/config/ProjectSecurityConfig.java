package mini.CodeWizards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectSecurityConfig {

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/saveMsg", "*/public/**") // Ignore CSRF for these paths
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers( "/", "/home").permitAll()
            .requestMatchers("/contact").permitAll()
            .requestMatchers("/saveMsg").permitAll()
            .requestMatchers("/about").permitAll()
            .requestMatchers("/assets/**").permitAll()
            .requestMatchers("/login").permitAll()
            .requestMatchers("/courses").permitAll()
            .requestMatchers("/level1").permitAll()
            .requestMatchers("/termsncond").permitAll()
            .requestMatchers("/privacy").permitAll()
            .requestMatchers("/logout").authenticated()
            .requestMatchers("/public/**").permitAll()
            .requestMatchers("/dashboard").authenticated()
            .requestMatchers("/displayMessages").hasRole("ADMIN")
            .requestMatchers("/closeMsg/**").hasRole("ADMIN")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/student/**").hasRole("STUDENT")
            .requestMatchers("/displayProfile").authenticated()
            .requestMatchers("/updateProfile").authenticated()
        )
        .formLogin(login -> login
            .loginPage("/login")
            .defaultSuccessUrl("/dashboard", true)
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/login?logout=true")
            .invalidateHttpSession(true)
            .permitAll()
        )
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}