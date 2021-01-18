package com.algaworks.brewer.repository;

import com.algaworks.brewer.repository.helper.venda.VendasQueries;
import org.springframework.data.jpa.repository.JpaRepository;

import com.algaworks.brewer.model.Venda;

import java.util.List;

public interface Vendas extends JpaRepository<Venda, Long>, VendasQueries {
}
