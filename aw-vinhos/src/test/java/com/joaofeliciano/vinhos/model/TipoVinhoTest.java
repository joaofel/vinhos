package com.joaofeliciano.vinhos.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TipoVinhoTest {

	@Test
	void deveConterOsTiposEsperados() {
		assertThat(TipoVinho.values())
				.containsExactly(TipoVinho.TINTO, TipoVinho.BRANCO, TipoVinho.ROSE);
	}

	@Test
	void deveResolverPeloNome() {
		assertThat(TipoVinho.valueOf("BRANCO")).isEqualTo(TipoVinho.BRANCO);
	}
}
