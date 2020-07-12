package com.algaworks.brewer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.model.TipoPessoa;

@Controller
@RequestMapping("/clientes")
public class ClientesController {
	
	//Quando acessar a URL /clientes/novo, vai retornar a página HTML cliente/CadastroCliente
	
	@RequestMapping("/novo")
	public ModelAndView novo() {
		ModelAndView mv = new ModelAndView("cliente/CadastroCliente");
		mv.addObject("tiposPessoa", TipoPessoa.values());
		return mv;
	}
	
//	@RequestMapping(value = "/clientes/novo", method = RequestMethod.POST)
//	public String cadastrar(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes attributes) {
//		
//		if(result.hasErrors()) {
//			return novo(cliente);
//		}
//		
//		attributes.addFlashAttribute("mensagem", "Cliente salvo com sucesso!");
//		System.out.println("Cliente: " + cliente.getNome());
//		return "redirect:/clientes/novo";
//	}
}