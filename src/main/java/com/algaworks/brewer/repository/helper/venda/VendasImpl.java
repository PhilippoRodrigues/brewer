package com.algaworks.brewer.repository.helper.venda;

import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VendasImpl implements VendasQueries {

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private PaginacaoUtil paginacaoUtil;

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Venda> query = builder.createQuery(Venda.class);
        Root<Venda> vendaEntity = query.from(Venda.class);

        Predicate[] filtros = adicionarFiltro(filtro, vendaEntity);

        query.select(vendaEntity).where(filtros);

        TypedQuery<Venda> typedQuery = (TypedQuery<Venda>) paginacaoUtil.prepararOrdem(
                query, vendaEntity, pageable);
        typedQuery = (TypedQuery<Venda>) paginacaoUtil.prepararIntervalo(typedQuery, pageable);

        return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
    }

    private Long total(VendaFilter filtro) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Venda> vendaEntity = query.from(Venda.class);

        query.select(criteriaBuilder.count(vendaEntity));
        query.where(adicionarFiltro(filtro, vendaEntity));

        return manager.createQuery(query).getSingleResult();
    }

    private Predicate[] adicionarFiltro(VendaFilter filtro, Root<Venda> vendaEntity) {

        List<Predicate> predicateList = new ArrayList<>();
        CriteriaBuilder builder = manager.getCriteriaBuilder();

        if (filtro != null) {
            if (!StringUtils.isEmpty(filtro.getCodigo())) {
                predicateList.add(builder.equal(vendaEntity.get("codigo"), filtro.getCodigo()));
            }

            if (filtro.getStatus() != null) {
                predicateList.add(builder.equal(vendaEntity.get("status"), filtro.getStatus()));
            }

            if (filtro.getDesde() != null) {
                LocalDateTime desde = LocalDateTime.of(filtro.getDesde(), LocalTime.of(0, 0));
                predicateList.add(builder.greaterThanOrEqualTo(vendaEntity.get("dataCriacao"), desde));
            }

            if (filtro.getAte() != null) {
                LocalDateTime ate = LocalDateTime.of(filtro.getDesde(), LocalTime.of(23, 59));
                predicateList.add(builder.lessThanOrEqualTo(vendaEntity.get("dataCriacao"), ate));
            }

            if (filtro.getValorMinimo() != null) {
                predicateList.add(builder.greaterThanOrEqualTo(vendaEntity.get("valorTotal"), filtro.getValorMinimo()));
            }

            if (filtro.getValorMaximo() != null) {
                predicateList.add(builder.lessThanOrEqualTo(vendaEntity.get("valorTotal"), filtro.getValorMaximo()));
            }

            if (!StringUtils.isEmpty(filtro.getNomeCliente())) {
                predicateList.add(builder.like(vendaEntity.get("cliente").get("nome"), filtro.getNomeCliente()));
            }

            if (!StringUtils.isEmpty(filtro.getCpfOuCnpjCliente())) {
                predicateList.add(builder.like(vendaEntity.get("cliente").get("cpfOuCnpj"), filtro.getCpfOuCnpjCliente()));
            }
        }

        Predicate[] predArray = new Predicate[predicateList.size()];
        return predicateList.toArray(predArray);
    }
}
