package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.helper.cidade.CidadesQueries;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface Cidades extends JpaRepository<Cidade, Long>, CidadesQueries {

	List<Cidade> findByEstadoCodigo(Long estadoCodigo);

	Optional<Cidade> findByNomeAndEstado(String nome, Estado estado);

	@Query("select c from Cidade c join fetch c.estado where c.codigo = :codigo")
	public Cidade findByCodigoFetchingEstado(@Param("codigo") Long codigo);
}
