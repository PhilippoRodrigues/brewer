package com.algaworks.brewer.mail;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.storage.FotoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class Mailer {

    @Autowired
    private FotoStorage fotoStorage;

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(Mailer.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine thymeleaf;

    @Async
    public void enviar(Venda venda) {
        Context context = new Context(new Locale("pt", "BR"));
        context.setVariable("venda", venda);
        context.setVariable("logo", "logo");

        Map<String, String> fotos = new HashMap<>();
        boolean adicionarMockCerveja = false;

        for (ItemVenda item : venda.getItens()){
            Cerveja cerveja = item.getCerveja();

            if (cerveja.temFoto()){
                String cid = "foto-" + cerveja.getCodigo();
                context.setVariable(cid, cid);
                fotos.put(cid, cerveja.getFoto() + "|" + cerveja.getContentType());
            } else {
                adicionarMockCerveja = true;
                context.setVariable("mockCerveja", "mockCerveja");
            }
        }


        try{
            String email = thymeleaf.process("mail/ResumoVenda", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage, true, "UTF-8");

            helper.setFrom("testeparaocursodaalgaworks@gmail.com");
            //helper.setTo(venda.getCliente().getEmail());
            helper.setTo("pipophilippo@gmail.com");
            helper.setSubject(String.format("Brewer - Venda nº %d", venda.getCodigo()));
            helper.setText(email, true);

            helper.addInline("logo", new ClassPathResource(
                    "static/images/logo-gray.png"));

            //Obter a imagem cerveja-mock, caso não haja imagem associada à cerveja cadastrada
            if (adicionarMockCerveja)
                helper.addInline("mockCerveja", new ClassPathResource(
                        "static/images/cerveja-mock.png"));

            //Pegar as imagens obtidas no loop para inseri-las no e-mail, no formato de thumbnail
            for (String cid : fotos.keySet()){
                String[] fotoContentType = fotos.get(cid).split("\\|");
                String foto = fotoContentType[0];
                String contentType = fotoContentType[1];
                byte[] arrayFoto = fotoStorage.recuperarThumbnail(foto);
                helper.addInline(cid, new ByteArrayResource(
                        arrayFoto), contentType);
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException e){
            logger.error("Erro ao enviar e-mail", e);
        }
    }
}
