package com.algaworks.brewer.repository.helper.cliente;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.dto.ClienteDTO;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.filter.ClienteFilter;

public interface ClientesQueries {
	
	public Page<Cliente> filtrar(ClienteFilter clienteFilter, Pageable pageable);
	
	public List<ClienteDTO> porNome(String nome);
}
