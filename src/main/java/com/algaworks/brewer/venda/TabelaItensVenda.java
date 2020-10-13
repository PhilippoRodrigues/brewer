package com.algaworks.brewer.venda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

public class TabelaItensVenda {
	
	private List<ItemVenda> itens = new ArrayList<>();
	
	public BigDecimal getValorTotal() {
		return itens
				//Vai iterar todos os itens adicionados na tabela
				.stream()
				//Vai pegar o resultado da multiplicação de cada item que passou pelo método getValorTotal() 
				.map(ItemVenda::getValorTotal)
				//Vai reduzir para um único valor utilizando determinada técnica, que, nesse caso, é a SOMA 
				//Assim, se houver vários itens, seus valores serão somados e apresentará apenas um valor, que é a soma deles.
				.reduce(BigDecimal::add)
				//Caso não haja nenhum item adicionado, retornada ZERO
				.orElse(BigDecimal.ZERO);
	}
	
	public void adicionarItem(Cerveja cerveja, Integer quantidade) {
		ItemVenda itemVenda = new ItemVenda();
		
		itemVenda.setCerveja(cerveja);
		itemVenda.setQuantidade(quantidade);
		itemVenda.setValorUnitario(cerveja.getValor());
		
		itens.add(itemVenda);
	}
}
