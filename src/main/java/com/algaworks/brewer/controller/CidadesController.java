package com.algaworks.brewer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;

@Controller
@RequestMapping("/cidades")
public class CidadesController {
	
	@Autowired
	private Cidades cidades;
	
	//Quando acessar a URL /cervejas/novo, vai retornar a página cerveja/CadastroCerveja
	
	@RequestMapping("/novo")
	public String novo(Cidade cidade) {
		return "cidade/CadastroCidade";
	}
	
	@RequestMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado(
			@RequestParam(name="estado", defaultValue="-1") Long codigoEstado){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return cidades.findByEstadoCodigo(codigoEstado);
	}
}
	
//	@RequestMapping(value = "/cidades/novo", method = RequestMethod.POST)
//	public String cadastrar(@Valid Cidade cidade, BindingResult result, Model model, RedirectAttributes attributes) {
//		
//		if(result.hasErrors()) {
//			return novo(cidade);
//		}
//		
//		attributes.addFlashAttribute("mensagem", "Cidade salva com sucesso!");
//		System.out.println("Cidade: " + cidade.getNome());
//		return "redirect:/cidades/novo";
//	}
