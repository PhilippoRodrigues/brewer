package com.algaworks.brewer.repository.helper.estilo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.dto.EstiloDTO;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.filter.EstiloFilter;

public interface EstilosQueries {
	
	Page<Estilo> filtrar(EstiloFilter filtro, Pageable pageable);
	
	List<EstiloDTO> porNome(String nome);
}
