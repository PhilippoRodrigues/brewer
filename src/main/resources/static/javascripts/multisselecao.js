Brewer = Brewer || {};

Brewer.MultiSelecao = (function(){
	
	function MultiSelecao(){
		this.statusBtn = $('.js-status-btn');
		this.selecaoCheckbox = $('.js-selecao');
		
	}
	
	MultiSelecao.prototype.iniciar = function(){
		this.statusBtn.on('click', onStatusBtnClicado.bind(this));
	}
	
	function onStatusBtnClicado(event){
		//Saber qual botão que foi clicado
		var botaoClicado = $(event.currentTarget);
		var status = botaoClicado.data('status');
		
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
				url: '/brewer/usuarios/status',
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
	
	return MultiSelecao;
}());

$(function(){
	var multiSelecao = new Brewer.MultiSelecao();
	multiSelecao.iniciar();
});