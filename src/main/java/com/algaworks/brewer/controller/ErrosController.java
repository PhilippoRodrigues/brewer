package com.algaworks.brewer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrosController {
	
	/*
	 * Na anotação @RequestMapping, é sempre interessante que você especifique o método. 
	 * Evite usar essa anotação sem especificar a propriedade "method". 
	 * As anotações @GetMapping, @PostMapping, @PutMapping, @DeleteMapping, e @PatchMapping, 
	 * terão o mesmo efeito de usar o @RequestMapping com a especificação do atributo "method".
	 * O que significa que esses métodos são uma simplificação do @RequestMapping, pois já dizem qual método é usado.
	 * Atualmente, é mais indicado usar esses métodos mais específicos (@GetMapping, @PostMapping, 
	 * @PutMapping, @DeleteMapping, ou @PatchMapping).
	 */

	@GetMapping("/404")
	public String paginaNaoEncontrada() {
		return "404";
	}
	
	@RequestMapping("/500")
	public String paginaErroServidor() {
		return "500";
	}
}
