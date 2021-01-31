package com.algaworks.brewer.controller;

import java.util.List;
import java.util.UUID;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.*;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.repository.filter.ClienteFilter;
import com.algaworks.brewer.repository.filter.VendaFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroVendaService;
import com.algaworks.brewer.session.TabelasItensSession;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/vendas")
public class VendasController {
	
	@Autowired
	private Cervejas cervejas;
	@Autowired
	private Vendas vendas;
	@Autowired
	private Mailer mailer;
	@Autowired
	private TabelasItensSession tabelaItens;
	@Autowired
	private CadastroVendaService cadastroVendaService;
	@Autowired
	private VendaValidator vendaValidator;
	
	@InitBinder("venda")
	public void inicializarValidador(WebDataBinder binder) {
		binder.setValidator(vendaValidator);
	}
	
	@GetMapping("/nova")
	public ModelAndView nova(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
		
		setUuid(venda);
		
		mv.addObject("itens", venda.getItens());
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
		
		return mv;
	}
	
	@PostMapping(value = "/nova", params = "salvar")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping(value = "/nova", params = "emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem", "Venda emitida com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}
	
	@PostMapping(value = "/nova", params = "enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return nova(venda);
		}

		venda.setUsuario(usuarioSistema.getUsuario());

		venda = cadastroVendaService.salvar(venda);

		mailer.enviar(venda);

		attributes.addFlashAttribute("mensagem",
				String.format("Venda nº %d salva com sucesso e e-mail enviado", venda.getCodigo()));

		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		Cerveja cerveja = cervejas.findById(codigoCerveja).orElse(null);
		tabelaItens.adicionarItem(uuid, cerveja, 1);
		return mvTabelaItensVenda(uuid);
	}

	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("codigoCerveja") Long codigoCerveja
			, Integer quantidade, String uuid) {

		Cerveja cerveja = new Cerveja();
		cerveja.setCodigo(codigoCerveja);
		tabelaItens.alterarQuantidadeItens(uuid, cerveja, quantidade);
		return mvTabelaItensVenda(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Long codigoCerveja
			, @PathVariable String uuid) {

		Cerveja cerveja = new Cerveja();
		cerveja.setCodigo(codigoCerveja);
		tabelaItens.excluirItem(uuid, cerveja);
		return mvTabelaItensVenda(uuid);
	}

	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter, @PageableDefault(size=3) Pageable pageable,
								  HttpServletRequest httpServletRequest) {

		ModelAndView mv = new ModelAndView("venda/PesquisaVendas");

		mv.addObject("todosStatus", StatusVenda.values());
		mv.addObject("tiposPessoa", TipoPessoa.values());

		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(
				vendas.filtrar(vendaFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}

	@PostMapping(value = "/nova", params = "cancelar")
		public ModelAndView cancelar(Venda venda, BindingResult result, RedirectAttributes attributes,
				@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

			try {
				cadastroVendaService.cancelar(venda);
			}catch (AccessDeniedException e){
				return new ModelAndView("/403");
			}

			attributes.addFlashAttribute("mensagem", "Venda cancelada com sucesso!");
			return new ModelAndView("redirect:/vendas/" + venda.getCodigo());
		}

	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mv;
	}
	
	private void validarVenda(Venda venda, BindingResult result) {
		venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));
		venda.calcularValorTotal();
		
		vendaValidator.validate(venda, result);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Venda venda = vendas.buscarComItens(codigo);

		setUuid(venda);
		for (ItemVenda item : venda.getItens()){
			tabelaItens.adicionarItem(
					venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}

		ModelAndView mv = nova(venda);
		mv.addObject(venda);
		return mv;
	}

	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarVendaPorMes(){
		return vendas.totalPorMes();
	}

	private void setUuid(Venda venda){
		if (StringUtils.isEmpty(venda.getUuid())) {
			venda.setUuid(UUID.randomUUID().toString());
		}
	}
}