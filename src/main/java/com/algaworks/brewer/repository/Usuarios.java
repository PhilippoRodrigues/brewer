package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.helper.usuario.UsuariosQueries;

public interface Usuarios extends JpaRepository<Usuario, Long>, UsuariosQueries {
	
	Optional<Usuario> findByEmail(String email);

	List<Usuario> findByCodigoIn(Long[] codigos);
}
