package com.algaworks.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaPorOrigem;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

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

    @Transactional(readOnly = true)
    @Override
    public Venda buscarComItens(Long codigo) {
        Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
        criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("codigo", codigo));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (Venda) criteria.uniqueResult();
    }

    //Busca realizada com JPQL
    @Override
    public BigDecimal valorTotalNoAno() {
        Optional<BigDecimal> optional = Optional.ofNullable(
                manager.createQuery("select sum(valorTotal) from Venda where year(dataCriacao) = :ano and status = :status",
                        BigDecimal.class)
                .setParameter("ano", Year.now().getValue())
                .setParameter("status", StatusVenda.EMITIDA)
                .getSingleResult());
        return optional.orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal valorTotalNoMes() {
        Optional<BigDecimal> optional = Optional.ofNullable(
                manager.createQuery("select sum(valorTotal) from Venda where month(dataCriacao) = :mes and status = :status",
                        BigDecimal.class)
                        .setParameter("mes", MonthDay.now().getMonthValue())
                        .setParameter("status", StatusVenda.EMITIDA)
                        .getSingleResult());
        return optional.orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal valorTicketMedioNoAno() {
        Optional<BigDecimal> optional = Optional.ofNullable(
                manager.createQuery("select sum(valorTotal)/count(*) from Venda where year(dataCriacao) = :ano and status = :status",
                        BigDecimal.class)
                        .setParameter("ano", Year.now().getValue())
                        .setParameter("status", StatusVenda.EMITIDA)
                        .getSingleResult());
        return optional.orElse(BigDecimal.ZERO);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VendaMes> totalPorMes() {
        List<VendaMes> vendasMes = manager.createNamedQuery("Vendas.totalPorMes").getResultList();

        LocalDate hoje = LocalDate.now();
        for (int i = 1; i <= 6; i++){
            String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());
            boolean possuiMes = vendasMes.stream().anyMatch(v -> v.getMes().equals(mesIdeal));

            if (!possuiMes)
                vendasMes.add(i - 1, new VendaMes(mesIdeal, 0));

            hoje = hoje.minusMonths(1);
        }
        return vendasMes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VendaPorOrigem> totalPorOrigem() {
        List<VendaPorOrigem> vendaPorOrigem = manager.createNamedQuery("Vendas.porOrigem", VendaPorOrigem.class).getResultList();

        LocalDate hoje = LocalDate.now();
        for (int i = 1; i <= 6; i++){
            String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());
            boolean origemMes = vendaPorOrigem.stream().anyMatch(v -> v.getMes().equals(mesIdeal));

            if (!origemMes)
                vendaPorOrigem.add(i - 1, new VendaPorOrigem(mesIdeal, 0, 0));

            hoje = hoje.minusMonths(1);
        }
        return vendaPorOrigem;
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
