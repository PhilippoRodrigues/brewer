package com.algaworks.brewer.dto;

import com.algaworks.brewer.model.Estado;

public class CidadeDTO {
	
	private Long codigo;
	private String nome;
	private Estado estado;

	public CidadeDTO(Long codigo, String nome, Estado estado) {
		this.codigo = codigo;
		this.nome = nome;
		this.estado = estado;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Estado estado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
}
