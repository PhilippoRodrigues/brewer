package com.algaworks.brewer.repository.helper.cidade;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.dto.CidadeDTO;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.filter.CidadeFilter;

public interface CidadesQueries {
	
	public Page<Cidade> filtrar(CidadeFilter cidadeFilter, Pageable pageable);
	
	public List<CidadeDTO> porNomeOuEstado(String nomeOuEstado);
}
