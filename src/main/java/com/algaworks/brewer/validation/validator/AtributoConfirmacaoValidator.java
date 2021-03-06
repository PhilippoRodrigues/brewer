package com.algaworks.brewer.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.springframework.beans.BeanUtils;

import com.algaworks.brewer.validation.AtributoConfirmacao;

public class AtributoConfirmacaoValidator implements ConstraintValidator<AtributoConfirmacao, Object> {

	private String atributo;
	private String atributoConfirmacao;
	
	@Override
	public void initialize(AtributoConfirmacao constraintAnnotation) {
		this.atributo = constraintAnnotation.atributo();
		this.atributoConfirmacao = constraintAnnotation.atributoConfirmacao();
	}
	
	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		
		boolean valido = false;
		
		try {
			Object valorAtributo = BeanUtils.getPropertyDescriptor((Class<?>) object, this.atributo);
			Object valorAtributoConfirmacao = BeanUtils.getPropertyDescriptor((Class<?>) object, this.atributoConfirmacao);
			
			valido = ambosSaoNull(valorAtributo, valorAtributoConfirmacao) || 
					ambosSaoIguais(valorAtributo, valorAtributoConfirmacao);
		} catch (Exception e) {
			throw new RuntimeException("Erro na recuperação de valores dos atributos", e);
		}

		if(!valido) {
			context.disableDefaultConstraintViolation();
			String message = context.getDefaultConstraintMessageTemplate();
			ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(message);
			violationBuilder.addPropertyNode(atributoConfirmacao).addConstraintViolation();
		}
		
		return valido;
	}

	private boolean ambosSaoIguais(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo != null && valorAtributo.equals(valorAtributoConfirmacao);
	}

	private boolean ambosSaoNull(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo ==  null && valorAtributoConfirmacao == null;
	}
}
