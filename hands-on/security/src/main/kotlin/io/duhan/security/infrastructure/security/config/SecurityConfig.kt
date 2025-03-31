package io.duhan.security.infrastructure.security.config

import io.duhan.security.application.port.JwtProvider
import io.duhan.security.infrastructure.security.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtProvider: JwtProvider,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            cors { disable() }
            formLogin { disable() }
            headers {
                frameOptions { disable() }
            }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter())
            authorizeHttpRequests {
                authorize("/api/v1/admins/login", permitAll)
                authorize("/api/v1/lecturers/login", permitAll)
                authorize("/api/v1/members/login", permitAll)

                authorize("/api/v1/admins/**", hasRole("ADMIN"))
                authorize("/api/v1/lecturers/**", hasRole("LECTURER"))
                authorize("/api/v1/members/**", hasRole("MEMBER"))

                authorize(anyRequest, permitAll)
            }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authenticationProviders: List<AuthenticationProvider>): AuthenticationManager =
        ProviderManager(authenticationProviders)

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(jwtProvider)
    }
}
