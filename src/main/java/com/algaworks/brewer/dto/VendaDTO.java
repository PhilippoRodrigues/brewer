package com.algaworks.brewer.dto;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.TipoPessoa;

public class VendaDTO {

    private Long codigo;
    private Cliente cliente;

    public VendaDTO(Long codigo, Cliente cliente) {
        this.codigo = codigo;
        this.cliente = cliente;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
