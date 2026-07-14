package com.joaofeliciano.vinhos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.joaofeliciano.vinhos.model.TipoVinho;
import com.joaofeliciano.vinhos.model.Vinho;
import com.joaofeliciano.vinhos.repository.Vinhos;

@SpringBootTest
@AutoConfigureMockMvc
class VinhosControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private Vinhos vinhos;

	private Vinho vinhoExemplo() {
		Vinho vinho = new Vinho();
		vinho.setCodigo(1L);
		vinho.setNome("Cabernet Sauvignon");
		vinho.setTipo(TipoVinho.TINTO);
		vinho.setValor(new BigDecimal("59.90"));
		return vinho;
	}

	@Test
	void pesquisaSemAutenticacaoRedirecionaParaLogin() throws Exception {
		mockMvc.perform(get("/vinhos"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("**/login"));
	}

	@Test
	@WithMockUser(username = "joao", roles = "PESQUISAR_VINHO")
	void pesquisaSemFiltroListaTodosOsVinhos() throws Exception {
		when(vinhos.findAll()).thenReturn(List.of(vinhoExemplo()));

		mockMvc.perform(get("/vinhos"))
				.andExpect(status().isOk())
				.andExpect(view().name("vinho/pesquisa-vinhos"))
				.andExpect(model().attributeExists("vinhos"));

		verify(vinhos).findAll();
		verify(vinhos, never()).findByNomeContainingIgnoreCase(any());
	}

	@Test
	@WithMockUser(username = "joao", roles = "PESQUISAR_VINHO")
	void pesquisaComFiltroUsaConsultaPorNome() throws Exception {
		when(vinhos.findByNomeContainingIgnoreCase("cab")).thenReturn(List.of(vinhoExemplo()));

		mockMvc.perform(get("/vinhos").param("nome", "cab"))
				.andExpect(status().isOk())
				.andExpect(view().name("vinho/pesquisa-vinhos"));

		verify(vinhos).findByNomeContainingIgnoreCase("cab");
		verify(vinhos, never()).findAll();
	}

	@Test
	@WithMockUser(username = "joao", roles = "PESQUISAR_VINHO")
	void cadastroNegadoParaQuemSoPodePesquisar() throws Exception {
		mockMvc.perform(get("/vinhos/novo"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "maria", roles = "CADASTRAR_VINHO")
	void abreFormularioDeCadastro() throws Exception {
		mockMvc.perform(get("/vinhos/novo"))
				.andExpect(status().isOk())
				.andExpect(view().name("vinho/cadastro-vinho"))
				.andExpect(model().attributeExists("tipos"));
	}

	@Test
	@WithMockUser(username = "maria", roles = "CADASTRAR_VINHO")
	void salvaVinhoValido() throws Exception {
		mockMvc.perform(post("/vinhos/novo").with(csrf())
						.param("nome", "Malbec")
						.param("tipo", "TINTO")
						.param("valor", "59,90"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/vinhos/novo"));

		verify(vinhos).save(any(Vinho.class));
	}

	@Test
	@WithMockUser(username = "maria", roles = "CADASTRAR_VINHO")
	void naoSalvaVinhoComNomeEmBranco() throws Exception {
		mockMvc.perform(post("/vinhos/novo").with(csrf())
						.param("nome", "")
						.param("tipo", "TINTO")
						.param("valor", "59,90"))
				.andExpect(status().isOk())
				.andExpect(view().name("vinho/cadastro-vinho"))
				.andExpect(model().attributeHasFieldErrors("vinho", "nome"));

		verify(vinhos, never()).save(any(Vinho.class));
	}

	@Test
	@WithMockUser(username = "maria", roles = "CADASTRAR_VINHO")
	void abreVinhoParaEdicao() throws Exception {
		when(vinhos.findById(1L)).thenReturn(Optional.of(vinhoExemplo()));

		mockMvc.perform(get("/vinhos/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("vinho/cadastro-vinho"))
				.andExpect(model().attributeExists("vinho", "tipos"));

		verify(vinhos).findById(1L);
	}

	@Test
	@WithMockUser(username = "maria", roles = "CADASTRAR_VINHO")
	void removeVinhoViaMetodoDelete() throws Exception {
		mockMvc.perform(post("/vinhos/1").with(csrf())
						.param("_method", "DELETE"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/vinhos"));

		verify(vinhos).deleteById(1L);
	}

	@Test
	@WithMockUser(username = "joao", roles = "PESQUISAR_VINHO")
	void remocaoNegadaParaQuemSoPodePesquisar() throws Exception {
		mockMvc.perform(post("/vinhos/1").with(csrf())
						.param("_method", "DELETE"))
				.andExpect(status().isForbidden());

		verify(vinhos, never()).deleteById(any());
	}

	@Test
	@WithMockUser(username = "maria", roles = "CADASTRAR_VINHO")
	void postSemTokenCsrfEhRejeitado() throws Exception {
		mockMvc.perform(post("/vinhos/novo")
						.param("nome", "Malbec")
						.param("tipo", "TINTO")
						.param("valor", "59,90"))
				.andExpect(status().isForbidden());

		verify(vinhos, never()).save(any(Vinho.class));
	}
}
