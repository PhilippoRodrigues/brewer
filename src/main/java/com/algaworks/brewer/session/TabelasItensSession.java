package com.algaworks.brewer.session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

@SessionScope
@Component
public class TabelasItensSession {
	
	private Set<TabelaItensVenda> tabelas = new HashSet<>();

	public void adicionarItem(String uuid, Cerveja cerveja, int quantidade) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		
		//Adicionar o item e a quantidade do item
		tabela.adicionarItem(cerveja, quantidade);
		tabelas.add(tabela);
		
	}


	public void alterarQuantidadeItens(String uuid, Cerveja cerveja, Integer quantidade) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarQuantidadeItens(cerveja, quantidade);
		
	}

	public void excluirItem(String uuid, Cerveja cerveja) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(cerveja);
		
	}
	
	public List<ItemVenda> getItens(String uuid) {
		//Busca a tabela pelo UUID e retorna os itens dessa tabela
		return buscarTabelaPorUuid(uuid).getItens();
	}

	private TabelaItensVenda buscarTabelaPorUuid(String uuid) {
		TabelaItensVenda tabela = tabelas.stream()
				//Filtrar na tabela o id que será adicionado
				.filter(t -> t.getUuid().equals(uuid))
				//Se tiver, pegá-lo; se não tiver, criar uma tabela 
				.findAny().orElse(new TabelaItensVenda(uuid));
		return tabela;
	}
}
