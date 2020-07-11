package com.algaworks.brewer.repository.helper.estilo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.EstiloDTO;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.filter.EstiloFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class EstilosImpl implements EstilosQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Estilo> filtrar(EstiloFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Estilo> query = builder.createQuery(Estilo.class);
		Root<Estilo> estiloEntity = query.from(Estilo.class);
		Predicate[] filtros = adicionarFiltro(filtro, estiloEntity);

		query.select(estiloEntity).where(filtros);

		TypedQuery<Estilo> typedQuery = (TypedQuery<Estilo>) paginacaoUtil.prepararOrdem(query, estiloEntity,
				pageable);
		typedQuery = (TypedQuery<Estilo>) paginacaoUtil.prepararIntervalo(typedQuery, pageable);

		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}

	private Long total(EstiloFilter filtro) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Estilo> estiloEntity = query.from(Estilo.class);

		query.select(criteriaBuilder.count(estiloEntity));
		query.where(adicionarFiltro(filtro, estiloEntity));

		return manager.createQuery(query).getSingleResult();
	}

	private Predicate[] adicionarFiltro(EstiloFilter filtro, Root<Estilo> estiloEntity) {
		List<Predicate> predicateList = new ArrayList<>();
		CriteriaBuilder builder = manager.getCriteriaBuilder();

		if (filtro != null) {

			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicateList.add(builder.like(estiloEntity.get("nome"), "%" + filtro.getNome() + "%"));
			}
		}

		Predicate[] predArray = new Predicate[predicateList.size()];
		return predicateList.toArray(predArray);
	}

//	private boolean isEstiloPresente(EstiloFilter filtro) {
//		return filtro.getNome() != null;
//	}

	@Override
	public List<EstiloDTO> porNome(String nome) {
		String jpql = "select new com.algaworks.brewer.dto.EstiloDTO(codigo, nome) "
				+ "from Estilo where lower(nome) like lower(:nome)";
		List<EstiloDTO> estilosFiltrados = manager.createQuery(jpql, EstiloDTO.class).setParameter("nome", nome + "%")
				.getResultList();
		return estilosFiltrados;
	}
}