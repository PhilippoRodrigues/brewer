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

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
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

		//PESQIUSAR
		//criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		Predicate[] filtros = adicionarFiltro(filtro, usuarioEntity);
		
		query.select(usuarioEntity).where(filtros);
		
		TypedQuery<Usuario> typedQuery =  (TypedQuery<Usuario>) paginacaoUtil.prepararOrdem(query, usuarioEntity, pageable);
		typedQuery = (TypedQuery<Usuario>) paginacaoUtil.prepararIntervalo(typedQuery, pageable);
		
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
		
		//DetachedCriteria dcg = DetachedCriteria.forClass(Grupo.class);
		
		if (filtro != null) {
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicateList.add(builder.like(usuarioEntity.get("nome"), "%" + filtro.getNome() + "%"));
				//predicateList.add(builder.like("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
			if (!StringUtils.isEmpty(filtro.getEmail())) {
				predicateList.add(builder.like(usuarioEntity.get("email"), "%" + filtro.getEmail() + "%"));
				//predicateList.add((Predicate) Restrictions.ilike("email", filtro.getEmail(), MatchMode.START));
			}
			
			//O LEFT_OUTER_JOIN serve para realizar consultas em relacionamentos ManyToMany
			
			//dcg.createAlias("grupo", "g", JoinType.LEFT_OUTER_JOIN);
			if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
				
				//List<Criterion> subqueries = new ArrayList<>();
						
				//Está transformando a string em um iterador, que vai ser mapeado para Long em Grupo.
				//Na classe Grupo, irá pegar o código e chama o toArray() para o for funcionar
//				for(Long codigoGrupo : filtro.getGrupos().stream().mapToLong(Grupo::getCodigo).toArray()) {
//					
//					//Criar o select de um grupo específico
//					DetachedCriteria dc = DetachedCriteria.forClass(UsuarioGrupo.class);
//					//Vai navegar até a variável codigo da entidade Grupo e obter o id correspondente da pesquisa
//					dc.add(Restrictions.eq("id.grupo.codigo", codigoGrupo));
//					dc.setProjection(Projections.property("id.usuario"));
//					
//					//Retornar o código do usuário
//					subqueries.add(Subqueries.propertyIn("codigo", dc));
//				}
				
				//Criar um array de Criterion para que seja passado para a implementação seguinte, que necessita de um array
				//Criterion[] criterions = new Criterion[subqueries.size()];
				
				//Obter a criteria principal
				//dcg.add(Restrictions.and(subqueries.toArray(criterions)));
			}
//
//			if (filtro.getOrigem() != null) {
//				predicateList.add(builder.equal(usuarioEntity.get("origem"), filtro.getOrigem()));
//			}
//
//			if (filtro.getValorDe() != null) {
//				predicateList.add(builder.equal(usuarioEntity.get("valor"), filtro.getValorDe()));
//			}
//
//			if (filtro.getValorAte() != null) {
//				predicateList.add(builder.equal(usuarioEntity.get("valor"), filtro.getValorAte()));
//			}
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
