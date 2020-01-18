package com.algaworks.brewer.model;

import javax.validation.constraints.NotBlank;

public class Cidade {
	
	@NotBlank(message = "Estado é obrigatório")
	private String estado;
	
	@NotBlank(message = "Nome é obrigatório")
	private String nome;
	
	
	
	public String getEstado() {
		return estado;
	}
	public void setSku(String estado) {
		this.estado = estado;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
}
