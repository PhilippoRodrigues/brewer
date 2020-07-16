package com.algaworks.brewer.repository.helper.cidade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.CidadeDTO;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class CidadesImpl implements CidadesQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	@Transactional(readOnly = true)
	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Cidade> query = builder.createQuery(Cidade.class);
		Root<Cidade> cidadeEntity = query.from(Cidade.class);

		// Join<Cidade, Estado> estadoCidade = cidadeEntity.join("estado");
		// Fetch<Endereco, Cidade> cidadeCliente = enderecoCliente.fetch("cidade",
		// JoinType.LEFT);
		Fetch<Cidade, Estado> estadoCidade = cidadeEntity.fetch("estado", JoinType.LEFT);

		Predicate[] filtros = adicionarFiltro(filtro, cidadeEntity);

		query.select(cidadeEntity).where(filtros);

		TypedQuery<Cidade> typedQuery = (TypedQuery<Cidade>) paginacaoUtil.prepararOrdem(query, cidadeEntity, pageable);
		typedQuery = (TypedQuery<Cidade>) paginacaoUtil.prepararIntervalo(typedQuery, pageable);

		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}

	private Long total(CidadeFilter filtro) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Cidade> cidadeEntity = query.from(Cidade.class);

		query.select(criteriaBuilder.count(cidadeEntity));
		query.where(adicionarFiltro(filtro, cidadeEntity));

		return manager.createQuery(query).getSingleResult();
	}

	private Predicate[] adicionarFiltro(CidadeFilter filtro, Root<Cidade> cidadeEntity) {
		List<Predicate> predicateList = new ArrayList<>();
		CriteriaBuilder builder = manager.getCriteriaBuilder();

		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicateList.add(builder.like(cidadeEntity.get("nome"), "%" + filtro.getNome() + "%"));
			}

			if (filtro.getEstado() != null) {
				predicateList.add(builder.equal(cidadeEntity.get("estado"), filtro.getEstado()));
			}
		}

		Predicate[] predArray = new Predicate[predicateList.size()];
		return predicateList.toArray(predArray);
	}

	@Override
	public List<CidadeDTO> porNomeOuEstado(String nomeOuEstado) {
		String jpql = "select new com.algaworks.brewer.dto.CidadeDTO(codigo, nome, estado) "
				+ "from Cidade where lower(nome) like lower(:nomeOuEstado) or lower(codigo_estado) like lower(:nomeOuEstado)";
		List<CidadeDTO> cidadesFiltradas = manager.createQuery(jpql, CidadeDTO.class)
				.setParameter("nome", nomeOuEstado + "%").getResultList();
		return cidadesFiltradas;
	}

}