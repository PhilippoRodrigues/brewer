package com.algaworks.brewer.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.model.Cliente;

@Controller
public class ClientesController {
	
	//Quando acessar a URL /clientes/novo, vai retornar a página HTML cliente/CadastroCliente
	
	@RequestMapping("/clientes/novo")
	public String novo(Cliente cliente) {
		return "cliente/CadastroCliente";
	}
	
	@RequestMapping(value = "/clientes/novo", method = RequestMethod.POST)
	public String cadastrar(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes attributes) {
		
		if(result.hasErrors()) {
			return novo(cliente);
		}
		
		attributes.addFlashAttribute("mensagem", "Cliente salvo com sucesso!");
		System.out.println("Cliente: " + cliente.getNome());
		return "redirect:/clientes/novo";
	}
}