package com.joaofeliciano.vinhos.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AutenticacaoTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void autenticaComCredenciaisValidas() throws Exception {
		mockMvc.perform(formLogin("/login").user("maria").password("maria"))
				.andExpect(authenticated().withUsername("maria").withRoles("CADASTRAR_VINHO", "PESQUISAR_VINHO"));
	}

	@Test
	void rejeitaSenhaInvalida() throws Exception {
		mockMvc.perform(formLogin("/login").user("maria").password("errada"))
				.andExpect(unauthenticated())
				.andExpect(redirectedUrl("/login?error"));
	}

	@Test
	void rejeitaUsuarioInexistente() throws Exception {
		mockMvc.perform(formLogin("/login").user("ninguem").password("seja"))
				.andExpect(unauthenticated())
				.andExpect(redirectedUrl("/login?error"));
	}

	@Test
	void logoutViaGetEncerraASessao() throws Exception {
		mockMvc.perform(get("/logout").with(user("maria").roles("CADASTRAR_VINHO")))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?logout"))
				.andExpect(unauthenticated());
	}
}
