package com.algaworks.brewer.model;

import javax.validation.constraints.NotBlank;

public class Usuario {
	
	@NotBlank(message="Nome é obrigatório")
	private String nome;
	
	@NotBlank(message="E-mail é obrigatório")
	private String email;
	
	@NotBlank(message="Data de nascimento é obrigatório")
	private String dataDeNascimento;
	
	@NotBlank(message="Senha é obrigatório")
	private String senha;
	
	@NotBlank(message="Confirmação de senha é obrigatório")
	private String confirmacaoDeSenha;
	

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDataDeNascimento() {
		return dataDeNascimento;
	}
	
	public void setDataDeNascimento(String dataDeNascimento) {
		this.dataDeNascimento = dataDeNascimento;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public String getConfirmacaoDeSenha() {
		return confirmacaoDeSenha;
	}
	
	public void setConfirmacaoDeSenha(String confirmacaoDeSenha) {
		this.confirmacaoDeSenha = confirmacaoDeSenha;
	}
}
