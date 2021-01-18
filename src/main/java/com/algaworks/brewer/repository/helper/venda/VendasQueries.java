package com.algaworks.brewer.repository.helper.venda;

import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface VendasQueries {

    Page<Venda> filtrar(VendaFilter filtro, Pageable pageable);
}
