package com.algaworks.brewer.repository.helper.cliente;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.ClienteDTO;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.Endereco;
import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.filter.ClienteFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class ClientesImpl implements ClientesQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	@Transactional(readOnly = true)
	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Cliente> query = builder.createQuery(Cliente.class);
		Root<Cliente> clienteEntity = query.from(Cliente.class);

		Join<Cliente, Endereco> enderecoCliente = clienteEntity.join("endereco", JoinType.LEFT);
		Fetch<Endereco, Cidade> cidadeCliente = enderecoCliente.fetch("cidade", JoinType.LEFT);
		Fetch<Cidade, Estado> estadoCliente = cidadeCliente.fetch("estado", JoinType.LEFT);

		Predicate[] filtros = adicionarFiltro(filtro, clienteEntity);

		query.select(clienteEntity).where(filtros);

		TypedQuery<Cliente> typedQuery = (TypedQuery<Cliente>) paginacaoUtil.prepararOrdem(query, clienteEntity,
				pageable);
		typedQuery = (TypedQuery<Cliente>) paginacaoUtil.prepararIntervalo(typedQuery, pageable);

		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}

	private Long total(ClienteFilter filtro) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Cliente> clienteEntity = query.from(Cliente.class);

		query.select(criteriaBuilder.count(clienteEntity));
		query.where(adicionarFiltro(filtro, clienteEntity));

		return manager.createQuery(query).getSingleResult();
	}

	private Predicate[] adicionarFiltro(ClienteFilter filtro, Root<Cliente> clienteEntity) {
		List<Predicate> predicateList = new ArrayList<>();
		CriteriaBuilder builder = manager.getCriteriaBuilder();

		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicateList.add(builder.like(clienteEntity.get("nome"), "%" + filtro.getNome() + "%"));
			}

			if (filtro.getCpfOuCnpj() != null) {
				predicateList.add(builder.equal(clienteEntity.get("cpfOuCnpj"), filtro.getCpfOuCnpj()));
			}
		}

		Predicate[] predArray = new Predicate[predicateList.size()];
		return predicateList.toArray(predArray);
	}

	@Override
	public List<ClienteDTO> porNome(String nome) {
		String jpql = "select new com.algaworks.brewer.dto.ClienteDTO(codigo, nome, tipoPessoa, cpfOuCnpj, telefone) "
				+ "from Cliente where lower(nome) like lower(:nome) or lower(cpfOuCnpj) like lower(:cpfOuCnpj)";
		List<ClienteDTO> clientesFiltrados = manager.createQuery(jpql, ClienteDTO.class)
				.setParameter("nome", nome + "%").getResultList();
		return clientesFiltrados;
	}

}