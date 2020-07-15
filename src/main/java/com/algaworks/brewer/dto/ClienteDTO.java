package com.algaworks.brewer.dto;

import com.algaworks.brewer.model.TipoPessoa;

public class ClienteDTO {
	
	private Long codigo;
	private String nome;
	private TipoPessoa tipoPessoa;
	private String cpfOuCnpj;
	private String telefone;

	public ClienteDTO(Long codigo, String nome, TipoPessoa tipoPessoa, String cpfOuCnpj, String telefone) {
		this.codigo = codigo;
		this.nome = nome;
		this.tipoPessoa = tipoPessoa;
		this.cpfOuCnpj = cpfOuCnpj;
		this.telefone = telefone;
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

	public TipoPessoa getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(TipoPessoa tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public String getCpfOuCnpj() {
		return cpfOuCnpj;
	}

	public void setCpfOuCnpj(String cpfOuCnpj) {
		this.cpfOuCnpj = cpfOuCnpj;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
}
