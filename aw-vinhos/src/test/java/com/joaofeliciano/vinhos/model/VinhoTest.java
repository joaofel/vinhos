package com.joaofeliciano.vinhos.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class VinhoTest {

	@Test
	void deveArmazenarEExporOsAtributos() {
		Vinho vinho = new Vinho();
		vinho.setCodigo(1L);
		vinho.setNome("Cabernet Sauvignon");
		vinho.setTipo(TipoVinho.TINTO);
		vinho.setValor(new BigDecimal("59.90"));

		assertThat(vinho.getCodigo()).isEqualTo(1L);
		assertThat(vinho.getNome()).isEqualTo("Cabernet Sauvignon");
		assertThat(vinho.getTipo()).isEqualTo(TipoVinho.TINTO);
		assertThat(vinho.getValor()).isEqualByComparingTo("59.90");
	}

	@Test
	void vinhosComMesmoCodigoDevemSerIguais() {
		Vinho a = new Vinho();
		a.setCodigo(10L);
		a.setNome("Merlot");

		Vinho b = new Vinho();
		b.setCodigo(10L);
		b.setNome("Nome diferente");

		assertThat(a).isEqualTo(b);
		assertThat(a).hasSameHashCodeAs(b);
	}

	@Test
	void vinhosComCodigosDiferentesNaoSaoIguais() {
		Vinho a = new Vinho();
		a.setCodigo(1L);

		Vinho b = new Vinho();
		b.setCodigo(2L);

		assertThat(a).isNotEqualTo(b);
	}

	@Test
	void vinhoNaoEIgualANuloOuAOutroTipo() {
		Vinho vinho = new Vinho();
		vinho.setCodigo(1L);

		assertThat(vinho).isNotEqualTo(null);
		assertThat(vinho).isNotEqualTo("outro-tipo");
	}

	@Test
	void doisVinhosSemCodigoDevemSerIguais() {
		assertThat(new Vinho()).isEqualTo(new Vinho());
	}
}
