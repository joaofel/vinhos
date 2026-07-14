package com.joaofeliciano.vinhos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SegurancaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void exibeAPaginaDeLoginParaUsuarioAnonimo() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
	}

	@Test
	@WithMockUser(username = "joao", roles = "PESQUISAR_VINHO")
	void redirecionaParaVinhosQuandoJaAutenticado() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/vinhos"));
	}
}
