package com.algaworks.brewer.repository.helper.cerveja;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.algaworks.brewer.dto.CervejaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class CervejasImpl implements CervejasQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Cerveja> query = builder.createQuery(Cerveja.class);
		Root<Cerveja> cervejaEntity = query.from(Cerveja.class);
		Predicate[] filtros = adicionarFiltro(filtro, cervejaEntity);

		query.select(cervejaEntity).where(filtros);
		
		TypedQuery<Cerveja> typedQuery =  (TypedQuery<Cerveja>) paginacaoUtil.prepararOrdem(query, cervejaEntity, pageable);
		typedQuery = (TypedQuery<Cerveja>) paginacaoUtil.prepararIntervalo(typedQuery, pageable);
		
		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}
	
	private Long total(CervejaFilter filtro) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Cerveja> cervejaEntity = query.from(Cerveja.class);
		
		query.select(criteriaBuilder.count(cervejaEntity));
		query.where(adicionarFiltro(filtro, cervejaEntity));
		
		return manager.createQuery(query).getSingleResult();
	}
	
	private Predicate[] adicionarFiltro(CervejaFilter filtro, Root<Cerveja> cervejaEntity) {
		List<Predicate> predicateList = new ArrayList<>();
		CriteriaBuilder builder = manager.getCriteriaBuilder();

		
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getSku())) {
				predicateList.add(builder.equal(cervejaEntity.get("sku"), filtro.getSku()));
			}
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicateList.add(builder.like(cervejaEntity.get("nome"), "%" + filtro.getNome() + "%"));
			}

			if (isEstiloPresente(filtro)) {
				predicateList.add(builder.equal(cervejaEntity.get("estilo"), filtro.getEstilo()));
			}

			if (filtro.getSabor() != null) {
				predicateList.add(builder.equal(cervejaEntity.get("sabor"), filtro.getSabor()));
			}

			if (filtro.getOrigem() != null) {
				predicateList.add(builder.equal(cervejaEntity.get("origem"), filtro.getOrigem()));
			}

			if (filtro.getValorDe() != null) {
				predicateList.add(builder.equal(cervejaEntity.get("valor"), filtro.getValorDe()));
			}

			if (filtro.getValorAte() != null) {
				predicateList.add(builder.equal(cervejaEntity.get("valor"), filtro.getValorAte()));
			}
		}
		
		Predicate[] predArray = new Predicate[predicateList.size()];
		return predicateList.toArray(predArray);
	}

	private boolean isEstiloPresente(CervejaFilter filtro) {
		return filtro.getEstilo() != null && filtro.getEstilo().getCodigo() != null;
	}

	@Override
	public List<CervejaDTO> porSkuOuNome(String skuOuNome) {
		String jpql = "select new com.algaworks.brewer.dto.CervejaDTO(codigo, sku, nome, origem, valor, foto) "
				+ "from Cerveja where lower(sku) like lower(:skuOuNome) or lower(nome) like lower(:skuOuNome)";

		return manager.createQuery(jpql, CervejaDTO.class)
					.setParameter("skuOuNome", skuOuNome + "%")
					.getResultList();
	}
}