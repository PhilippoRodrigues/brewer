package com.algaworks.brewer.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.algaworks.brewer.service.event.venda.VendaEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Vendas;

@Service
public class CadastroVendaService {

	@Autowired
	private Vendas vendas;
	@Autowired
	private ApplicationEventPublisher publisher;

	@Transactional
	public Venda salvar(Venda venda) {
		if (venda.isSalvarProibido())
			throw new RuntimeException("Usuário tentando salvar uma venda proibida");

		if (venda.isNova()) {
			venda.setDataCriacao(LocalDateTime.now());
		} else {
			Venda vendaExistente = vendas.findById(venda.getCodigo()).orElse(null);
			assert vendaExistente != null;
			venda.setDataCriacao(vendaExistente.getDataCriacao());
		}
		
		if (venda.getDataEntrega() != null) {
			venda.setDataHoraEntrega(LocalDateTime.of(
					venda.getDataEntrega(), venda.getHorarioEntrega() != null ?
							venda.getHorarioEntrega() : LocalTime.NOON));
		}
		
		return vendas.saveAndFlush(venda);
	}

	@Transactional
	public void emitir(Venda venda) {
		venda.setStatus(StatusVenda.EMITIDA);
		salvar(venda);

		publisher.publishEvent(new VendaEvent(venda));
	}

	@PreAuthorize("#venda.usuario == authentication.principal or hasRole('CANCELAR_VENDA')")
	@Transactional
	public void cancelar(Venda venda) {
		Venda vendaExistente = vendas.findById(venda.getCodigo()).orElse(venda);

		vendaExistente.setStatus(StatusVenda.CANCELADA);
		vendas.save(vendaExistente);
	}
}

