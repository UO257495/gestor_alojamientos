package com.nayarasanchez.gestor_alojamientos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userService(UsuarioRepository usuarioRepository) {
        return (email) -> {
            Usuario usuario = usuarioRepository.buscarUsuarioPorEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

            return org.springframework.security.core.userdetails.User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getPassword())
                    .roles(usuario.getRol().name())
                    .build();
        };
    }


    @Bean
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

          http.formLogin(login -> login
            .loginPage("/auth/login")
            .loginProcessingUrl("/auth/login")
            .defaultSuccessUrl("/inicio", true)
            .usernameParameter("email") 
            .passwordParameter("password")
            .permitAll()
        );

        http.logout(logout -> logout
            .permitAll());

        // Las reglas más específicas necesitan estar primero, seguidas por las más generales
        http.authorizeHttpRequests(authorize -> authorize
            // Recursos públicos
            .requestMatchers("/css/**", "/img/**", "/fontawesome/**", "/js/**").permitAll()
            // Rutas accesibles sin sesión
            .requestMatchers("/inicio", "/login", "/registro", "/auth/**","/gestion/usuarios/detalle", "/gestion/usuarios/nuevo").permitAll()
            // Todo lo demás requiere autenticación
            .anyRequest().authenticated()
        );

        return http.build();
    }
}
