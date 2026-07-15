package com.joaofeliciano.vinhos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails joao = User.withUsername("joao")
				.password("{noop}joao")
				.roles("PESQUISAR_VINHO")
				.build();

		UserDetails maria = User.withUsername("maria")
				.password("{noop}maria")
				.roles("CADASTRAR_VINHO", "PESQUISAR_VINHO")
				.build();

		return new InMemoryUserDetailsManager(joao, maria);
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().requestMatchers("/layout/**");
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/vinhos").hasRole("PESQUISAR_VINHO")
				.requestMatchers("/vinhos/**").hasRole("CADASTRAR_VINHO")
				.anyRequest().authenticated())
			.formLogin(form -> form
				.loginPage("/login")
				.permitAll())
			.logout(logout -> logout
				.logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/logout")));

		return http.build();
	}
}
