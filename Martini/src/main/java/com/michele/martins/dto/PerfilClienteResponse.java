package com.michele.martins.dto;

public class PerfilClienteResponse {

    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String genero;
    private String status;
    private String proximaConsulta;
    private String consultaAnterior;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProximaConsulta() { return proximaConsulta; }
    public void setProximaConsulta(String proximaConsulta) { this.proximaConsulta = proximaConsulta; }

    public String getConsultaAnterior() { return consultaAnterior; }
    public void setConsultaAnterior(String consultaAnterior) { this.consultaAnterior = consultaAnterior; }
}
