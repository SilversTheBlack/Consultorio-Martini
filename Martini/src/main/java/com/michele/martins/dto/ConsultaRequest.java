package com.michele.martins.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ConsultaRequest {

    @NotNull
    private Long clienteId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate data;

    @JsonFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime horario;

    @JsonFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime horarioFim;

    @Positive
    private float valor;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }

    public LocalTime getHorarioFim() { return horarioFim; }
    public void setHorarioFim(LocalTime horarioFim) { this.horarioFim = horarioFim; }

    public float getValor() { return valor; }
    public void setValor(float valor) { this.valor = valor; }
}
