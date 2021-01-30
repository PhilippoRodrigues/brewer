package com.algaworks.brewer.service;

import java.util.Optional;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.service.exception.CpfCnpjClienteJaCadastradoException;

@Service
public class CadastroClienteService {
	
	@Autowired
	private Clientes clientes;

	@Autowired
	private Cidades cidades;

	@Transactional
	public void salvar(Cliente cliente) {
		if (cliente.isNovo()){
			this.clientes.findByCpfOuCnpj(cliente.getCpfCnpjSemFormatacao())
					.ifPresent(c -> {
						throw new CpfCnpjClienteJaCadastradoException("CPF/CNPJ já cadastrado");
					});
		}
		clientes.save(cliente);
	}

	@Transactional(readOnly = true)
	public void comporDadosEndereco(Cliente cliente) {
		if (cliente.getEndereco() == null
				|| cliente.getEndereco().getCidade() == null
				|| cliente.getEndereco().getCidade().getCodigo() == null) {
			return;
		}

		Cidade cidade = this.cidades.findByCodigoFetchingEstado(cliente.getEndereco().getCidade().getCodigo());

		cliente.getEndereco().setCidade(cidade);
		cliente.getEndereco().setEstado(cidade.getEstado());
	}

}
