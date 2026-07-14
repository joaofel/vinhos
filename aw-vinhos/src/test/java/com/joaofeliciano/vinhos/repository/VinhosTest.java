package com.joaofeliciano.vinhos.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.joaofeliciano.vinhos.model.TipoVinho;
import com.joaofeliciano.vinhos.model.Vinho;

@DataJpaTest
class VinhosTest {

	@Autowired
	private Vinhos vinhos;

	private Vinho novoVinho(String nome, TipoVinho tipo) {
		Vinho vinho = new Vinho();
		vinho.setNome(nome);
		vinho.setTipo(tipo);
		vinho.setValor(new BigDecimal("42.00"));
		return vinho;
	}

	@BeforeEach
	void setUp() {
		vinhos.deleteAll();
		vinhos.save(novoVinho("Cabernet Sauvignon", TipoVinho.TINTO));
		vinhos.save(novoVinho("Chardonnay", TipoVinho.BRANCO));
		vinhos.save(novoVinho("Merlot", TipoVinho.TINTO));
	}

	@Test
	void deveSalvarEGerarCodigo() {
		Vinho salvo = vinhos.save(novoVinho("Malbec", TipoVinho.TINTO));
		assertThat(salvo.getCodigo()).isNotNull();
	}

	@Test
	void deveEncontrarPorParteDoNomeIgnorandoCase() {
		List<Vinho> resultado = vinhos.findByNomeContainingIgnoreCase("cab");

		assertThat(resultado)
				.extracting(Vinho::getNome)
				.containsExactly("Cabernet Sauvignon");
	}

	@Test
	void deveEncontrarVariosResultadosPeloTrechoComum() {
		List<Vinho> resultado = vinhos.findByNomeContainingIgnoreCase("on");

		assertThat(resultado)
				.extracting(Vinho::getNome)
				.containsExactlyInAnyOrder("Cabernet Sauvignon", "Chardonnay");
	}

	@Test
	void deveRetornarVazioQuandoNaoHaCorrespondencia() {
		assertThat(vinhos.findByNomeContainingIgnoreCase("inexistente")).isEmpty();
	}
}
