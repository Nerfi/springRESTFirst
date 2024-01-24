package com.example.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/* TODO ESTE ARCHIVO PERTENECE A LA SEGURIDAD DE SPRING Y DEL TUTORIAL */
// https://spring.academy/courses/building-a-rest-api-with-spring-boot/lessons/simple-spring-security-lab/lab


 /*
 The @Configuration annotation tells Spring to use this class to configure Spring and Spring Boot itself.
  Any Beans specified in
 this class will now be available to Spring's Auto Configuration engine.
 */
@Configuration
public class SecurityConfig {

    /*
    Spring Security expects a Bean to configure its Filter Chain,
     which you learned about in the Simple Spring Security lesson.
     Annotating a method returning a SecurityFilterChain with the @Bean satisfies this expectation.
    * */
     @Bean
     SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http
                 .authorizeHttpRequests(request -> request
                         .requestMatchers("/cashcards/**", "/tutorials/**")


                         .hasRole("CARD-OWNER")
                 )

                 .csrf(csfr -> csfr.disable())
                 .httpBasic(Customizer.withDefaults());
// PD: si nuestra app va a ser llamada por un cliente(Browser)
         // no es conveniente descativar csrf, mirarlo despues


         return http.build();
     }

    // hacemos esto para evitar la seguridad en la bbdd por el momento
     // y para continuar con las pruebas y el tutorial
      @Bean
      public WebSecurityCustomizer webSecurityCustomizer() {
          return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
      }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // esto es para configurar el usuario in memory que vamos a usar con las request(por lo cual ya no nos pide que nos logueemos la app)
     // comentar el codigo de abajo para enterder el comentario de arriba

   @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        // construimos un usuario
        User.UserBuilder users = User.builder();

       UserDetails sarah = users.username("sarah1").password(passwordEncoder.encode("abc123"))
                .roles("CARD-OWNER")
                .build();

        UserDetails hankOwnsNoCards = users.username("hank-owns-no-cards")
                .password(passwordEncoder.encode("qrs456"))
                .roles("NON-OWNER") // new role
                .build();

        UserDetails kumar = users
                .username("kumar2")
                .password(passwordEncoder.encode("xyz789"))
                .roles("CARD-OWNER")
                .build();
        return new InMemoryUserDetailsManager(sarah, hankOwnsNoCards, kumar);

    }


}
