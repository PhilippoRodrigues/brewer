var Brewer = Brewer || {};

Brewer.MaskMoney = (function() {

	function MaskMoney() {

		this.decimal = $(".js-decimal");
		this.plain = $(".js-plain");

	}

	MaskMoney.prototype.enable = function() {

		this.decimal.maskMoney({
			decimal: ',',
			thousands: '.'
		});

		this.plain.maskMoney({
			precision: 0,
			thousands: '.'
		});
	}

	return MaskMoney;

}());

Brewer.MaskPhoneNumber = (function() {
	function MaskPhoneNumber() {
		this.inputPhoneNumber = $('.js-phone-number');
	}

	MaskPhoneNumber.prototype.enable = function() {

		var maskBehavior = function(val) {
			return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
		},
			options = {
				onKeyPress: function(val, e, field, options) {
					field.mask(maskBehavior.apply({}, arguments), options);
				}
			};

		this.inputPhoneNumber.mask(maskBehavior, options);

	}
	return MaskPhoneNumber;
}());


Brewer.CepNumberMask = (function() {

	function CepNumberMask() {
		this.inputCepNumber = $('.js-cep-number');
	}

	CepNumberMask.prototype.enable = function() {

		this.inputCepNumber.mask('00.000-000');
	}

	return CepNumberMask;
}());

Brewer.MaskDate = (function() {
	
	function MaskDate() {
		
		this.inputDate = $('.js-date');
	}
	
	MaskDate.prototype.enable = function() {
		this.inputDate.mask('00/00/0000');
		this.inputDate.datepicker({
			orientation: 'bottom',
			language: 'pt-BR',
			autoclose: true
		});
	}
	
	return MaskDate;
}());

Brewer.Security = (function(){
	
	function Security(){
		this.token = $('input[name=_csrf]').val();
		this.header = $('input[name=_csrf_header]').val();
	}
	
	//Função para habilitar a função acima
	
	Security.prototype.enable = function(){
		//toda vez que for enviado uma requisição AJAX, será redefinido o token request
		
		$(document).ajaxSend(function(event, jqxhr, settings) {
			jqxhr.setRequestHeader(this.header, this.token);
		}.bind(this));
		
		//O contexto da implementação da função acima está sendo executado dentro dessa. 
		//Dessa forma, o this refere-se ao contexto dessa função (Security.prototype.enable).
		
		/*
		Para que o contexto seja executado dentro da função Security, é implementado o método bind(), 
		que serve para trocar o contexto da função Security.prototype.enable para o contexto da função Security.
		*/
	}
	
	return Security;
	
}());

numeral.locale('pt-br');

Brewer.formatarMoeda = function(valor) {
	return numeral(valor).format('0,0.00');
}

Brewer.recuperarValor = function(valorFormatado) {
	return numeral(valorFormatado).value();
}

$(function() {

	var maskMoney = new Brewer.MaskMoney();
	maskMoney.enable();

	var maskPhoneNumber = new Brewer.MaskPhoneNumber();
	maskPhoneNumber.enable();

	var cepNumberMask = new Brewer.CepNumberMask();
	cepNumberMask.enable();
	
	var maskDate = new Brewer.MaskDate();
	maskDate.enable();
	
	var security = new Brewer.Security();
	security.enable();
});