package com.michele.martins.ms_validacao_Email.dto;

public class EmailRequest {
    private String destinatario;
    private String nomePaciente;
    private String data;
    private String horarioInicio;
    private String horarioFim;
    private double valor;

    // Getters and setters
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public String getNomePaciente() { return nomePaciente; }
    public void setNomePaciente(String nomePaciente) { this.nomePaciente = nomePaciente; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public String getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(String horarioInicio) { this.horarioInicio = horarioInicio; }
    public String getHorarioFim() { return horarioFim; }
    public void setHorarioFim(String horarioFim) { this.horarioFim = horarioFim; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}
