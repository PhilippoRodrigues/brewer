package com.algaworks.brewer.repository.helper.venda;

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaPorOrigem;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VendasQueries {

    Page<Venda> filtrar(VendaFilter filtro, Pageable pageable);

    Venda buscarComItens(Long codigo);

    BigDecimal valorTotalNoAno();

    BigDecimal valorTotalNoMes();

    BigDecimal valorTicketMedioNoAno();

    List<VendaMes> totalPorMes();

    List<VendaPorOrigem> totalPorOrigem();
}
