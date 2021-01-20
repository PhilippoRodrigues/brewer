package com.algaworks.brewer.mail;

import com.algaworks.brewer.model.Venda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class Mailer {
    @Autowired
    private JavaMailSender mailSender;
    @Async
    public void enviar(Venda venda) {

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom("testeparaocursodaalgaworks@gmail.com");

        //Implementação para obter o e-mail do cliente não está funcionando
        mensagem.setTo(venda.getCliente().getEmail());
        mensagem.setSubject("Venda Efetuada");
        mensagem.setText("Obrigado, sua venda foi processada!");

        mailSender.send(mensagem);
    }
}
