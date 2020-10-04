package com.algaworks.brewer.repository.helper.usuario;

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

import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.model.UsuarioGrupo;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class UsuariosImpl implements UsuariosQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Usuario> query = builder.createQuery(Usuario.class);
		Root<Usuario> usuarioEntity = query.from(Usuario.class);

		Predicate[] filtros = adicionarFiltro(filtro, usuarioEntity);

		query.select(usuarioEntity).where(filtros);

		TypedQuery<Usuario> typedQuery =  (TypedQuery<Usuario>) paginacaoUtil.prepararOrdem(query, usuarioEntity, pageable);
		typedQuery = (TypedQuery<Usuario>) paginacaoUtil.prepararIntervalo(typedQuery, pageable);
		
		List<Usuario> filtrados = typedQuery.getResultList();
		filtrados.forEach(u -> Hibernate.initialize(u.getGrupos()));
		
		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}
	
	private Long total(UsuarioFilter filtro) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Usuario> usuarioEntity = query.from(Usuario.class);
		
		query.select(criteriaBuilder.count(usuarioEntity));
		query.where(adicionarFiltro(filtro, usuarioEntity));
		
		return manager.createQuery(query).getSingleResult();
	}
	
	private Predicate[] adicionarFiltro(UsuarioFilter filtro, Root<Usuario> usuarioEntity) {
		List<Predicate> predicateList = new ArrayList<>();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
//		CriteriaQuery<UsuarioGrupo> query = builder.createQuery(UsuarioGrupo.class);
//		Root<UsuarioGrupo> usuarioGrupoEntity = query.from(UsuarioGrupo.class);
		
		
		if (filtro != null) {
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicateList.add(builder.like(usuarioEntity.get("nome"), "%" + filtro.getNome() + "%"));
				//predicateList.add(builder.like("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
			if (!StringUtils.isEmpty(filtro.getEmail())) {
				predicateList.add(builder.like(usuarioEntity.get("email"), "%" + filtro.getEmail() + "%"));
				//predicateList.add((Predicate) Restrictions.ilike("email", filtro.getEmail(), MatchMode.START));
			}
			
			//NÃO ESTÁ EXIBINDO O FILTRO POR GRUPOS NA TELA
			
			if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
				
				List<Criterion> subqueries = new ArrayList<>();
//				//Está transformando a string em um iterador, que vai ser mapeado para Long em Grupo.
//				//Na classe Grupo, irá pegar o código e chama o toArray() para o for funcionar
				for(Long codigoGrupo : filtro.getGrupos().stream().mapToLong(Grupo::getCodigo).toArray()) {
					
					DetachedCriteria dc = DetachedCriteria.forClass(UsuarioGrupo.class);
					//CriteriaQuery<UsuarioGrupo> queryGrupo = builder.createQuery(UsuarioGrupo.class);
					
					dc.add(Restrictions.eq("id.grupo.codigo", codigoGrupo));
					dc.setProjection(Projections.property("id.usuario"));
					
					subqueries.add(Subqueries.propertyIn("codigo", dc));
							
//					//Criar o select de um grupo específico
//					Root<UsuarioGrupo> grupoEntity = query.from(UsuarioGrupo.class);
//					//Vai navegar até a variável codigo da entidade Grupo e obter o id correspondente da pesquisa
//					((DetachedCriteria) grupoEntity).add(Restrictions.eq("id.grupo.codigo", codigoGrupo));
//					((DetachedCriteria) grupoEntity).setProjection(Projections.property("id.usuario"));
//					
//					//Retornar o código do usuário
//					predicateList.add((Predicate) Subqueries.propertyIn("codigo", (DetachedCriteria) grupoEntity));
				}
//				Criterion[] criterions = new Criterion[subqueries.size()];
//				criteria.add(Restrictions.and(subqueries.toArray(criterions)));
			}
		}
		Predicate[] predArray = new Predicate[predicateList.size()];
		return predicateList.toArray(predArray);
	}

	@Override
	public Optional<Usuario> porEmailEAtivo(String email) {
		return manager.createQuery("from Usuario where lower(email) = lower(:email) and ativo = true", Usuario.class)
				.setParameter("email", email).getResultList().stream().findFirst();
	}

	@Override
	public List<String> permissoes(Usuario usuario) {
		return manager.createQuery(
				"select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario",
				String.class)
				.setParameter("usuario", usuario)
				.getResultList();
	}
}
