package com.algaworks.brewer.model;

import javax.validation.constraints.NotBlank;

public class Estilo {
	
	@NotBlank(message="Nome é obrigatório")
	private String nome;
	

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
