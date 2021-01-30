package com.algaworks.brewer.service;

import java.util.Optional;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.service.exception.NomeEstiloJaCadastradoException;

import javax.persistence.PersistenceException;

@Service
public class CadastroEstiloService {
	
	@Autowired
	private Estilos estilos;
	
	@Transactional
	public Estilo salvar(Estilo estilo) {
		
		Optional<Estilo> estiloOptional = estilos.findByNomeIgnoreCase(estilo.getNome());
		
		if(estiloOptional.isPresent()) {
			throw new NomeEstiloJaCadastradoException("Nome do estilo já cadastrado");
		}
		
		return estilos.saveAndFlush(estilo);
	}

	@Transactional
	public void excluir(Estilo estilo) {

		try {
			estilos.delete(estilo);
			estilos.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException(
					"Não é possível apagar estilo. Já está associado a uma cerveja.");
		}

	}
}
