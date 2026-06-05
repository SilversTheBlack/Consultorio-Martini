package com.michele.martins.ms_validacao_Email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String CONSULTORIO_NOME = "Consultório Martini";
    private static final String CONSULTORIO_EMAIL = "contato@consultoriomartini.com";

    public void enviarConfirmacaoConsulta(String destinatario, String nomePaciente,
                                         String data, String horarioInicio, String horarioFim, Double valor) {
        String assunto = "Confirmação de Consulta - " + CONSULTORIO_NOME;

        String mensagem = "Olá " + nomePaciente + ",\n\n" +
                "Sua consulta foi confirmada com sucesso!\n\n" +
                "Data: " + formatarData(data) + "\n" +
                "Horário: " + horarioInicio + " às " + horarioFim + "\n" +
                "Valor: R$ " + String.format("%.2f", valor) + "\n\n" +
                "Aguardamos você no dia agendado.\n" +
                "Em caso de dúvidas, entre em contato conosco.\n\n" +
                "Atenciosamente,\n" + CONSULTORIO_NOME;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(destinatario);
        email.setFrom(CONSULTORIO_EMAIL);
        email.setSubject(assunto);
        email.setText(mensagem);

        mailSender.send(email);
    }

    private String formatarData(String dataIso) {
        try {
            LocalDate data = LocalDate.parse(dataIso);
            return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return dataIso;
        }
    }
}