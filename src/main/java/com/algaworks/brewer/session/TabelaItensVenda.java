package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

@SessionScope
@Component
public class TabelaItensVenda {
	
	private List<ItemVenda> itens = new ArrayList<>();
	
	public BigDecimal getValorTotal() {
		//Vai iterar todos os itens adicionados na tabela
		return itens.stream()
				//Vai pegar o resultado da multiplicação de cada item que passou pelo método getValorTotal() 
				.map(ItemVenda::getValorTotal)
				//Vai reduzir para um único valor utilizando determinada técnica, que, nesse caso, é a SOMA 
				//Assim, se houver vários itens, seus valores serão somados e apresentará apenas um valor, que é a soma deles.
				.reduce(BigDecimal::add)
				//Caso não haja nenhum item adicionado, retornada ZERO
				.orElse(BigDecimal.ZERO);
	}
	
	public void adicionarItem(Cerveja cerveja, Integer quantidade) {
		Optional<ItemVenda> itemVendaOptional = itens.stream()
			.filter(i -> i.getCerveja().equals(cerveja))
			.findAny();
		
		ItemVenda itemVenda = null;
		if (itemVendaOptional.isPresent()) {
			itemVenda = itemVendaOptional.get();
			itemVenda.setQuantidade(itemVenda.getQuantidade() + quantidade);
		} else {
			itemVenda = new ItemVenda();
			itemVenda.setCerveja(cerveja);
			itemVenda.setQuantidade(quantidade);
			itemVenda.setValorUnitario(cerveja.getValor());
			itens.add(0, itemVenda);
		}
	}
	
	public int total() {
		return itens.size();
	}

	public List<ItemVenda> getItens() {
		return itens;
	}
}
