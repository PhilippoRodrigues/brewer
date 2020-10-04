Brewer = Brewer || {};

Brewer.MultiSelecao = (function(){
	
	function MultiSelecao(){
		this.statusBtn = $('.js-status-btn');
		this.selecaoCheckbox = $('.js-selecao');
		this.selecaoTodosCheckbox = $('.js-selecao-todos');
		
	}
	
	MultiSelecao.prototype.iniciar = function(){
		this.statusBtn.on('click', onStatusBtnClicado.bind(this));
		this.selecaoTodosCheckbox.on('click', onSelecaoTodosClicado.bind(this));
		this.selecaoCheckbox.on('click', onSelecaoClicado.bind(this));
	}
	
	function onStatusBtnClicado(event){
		//Saber qual botão que foi clicado
		var botaoClicado = $(event.currentTarget);
		var status = botaoClicado.data('status');
		var url = botaoClicado.data('url');
		
		//Variável para recuperar os checkboxes selecionados
		var checkboxSelecionados = this.selecaoCheckbox.filter(':checked');
		
		//Pegar todos os checkboxes selecionados e jogar no parâmetro c
		var codigos = $.map(checkboxSelecionados, function(c){
			//
			return $(c).data('codigo');
		});
		
		//Chamar a API UsuariosController para obter os usuários selecionados
		if(codigos.length > 0){
			$.ajax({
				url: url,
				method: 'PUT',
				data:{
					//Vai ser passado o var codigos criado acima
					codigos: codigos,
					status: status
				},
				success: function(){
					window.location.reload();
				}
			});
		}
	}
	
	function onSelecaoTodosClicado(){
		var status = this.selecaoTodosCheckbox.prop('checked');
		this.selecaoCheckbox.prop('checked', status);
		
		//Ativar os botões, caso o checkbox do cabeçalho esteja selecionado
		statusBotaoAcao.call(this, status);
	}
	
	function onSelecaoClicado(){
		var selecaoCheckboxChecados = this.selecaoCheckbox.filter(':checked');
		
		//Se a seleção dos checkboxes for igual à quantidade dos que estão checados, pode definir que todos estão checados
		
		this.selecaoTodosCheckbox.prop('checked', selecaoCheckboxChecados.length >= this.selecaoCheckbox.length);
		
		//Ativar os botões, caso um objeto esteja selecionado
		statusBotaoAcao.call(this, selecaoCheckboxChecados.length);
	}
	
	function statusBotaoAcao(ativar) {
		ativar ? this.statusBtn.removeClass('disabled') : this.statusBtn.addClass('disabled');
	}
	
	return MultiSelecao;
}());

$(function(){
	var multiSelecao = new Brewer.MultiSelecao();
	multiSelecao.iniciar();
});