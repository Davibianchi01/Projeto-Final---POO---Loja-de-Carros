package main.java;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Proposta {
    private static int contadorIds = 1;
    private int id;
    private BigDecimal valorVeiculo;
    private BigDecimal entrada;
    private int parcelas;
    private Cliente cliente;
    private List<Veiculo> carrosSelecionados;
    private BigDecimal valorTotal;

    public Proposta(BigDecimal valorVeiculo, BigDecimal entrada, int parcelas, List<Veiculo> carros, Cliente cliente) {
        this.id = contadorIds++;
        this.valorVeiculo = valorVeiculo != null ? valorVeiculo : BigDecimal.ZERO;
        this.entrada = entrada != null ? entrada : BigDecimal.ZERO;
        this.parcelas = parcelas;
        this.carrosSelecionados = carros != null ? carros : new ArrayList<>();
        this.cliente = cliente;
        this.valorTotal = this.valorVeiculo;
    }
    public int getId() {
        return id;
    }
    public BigDecimal getValorVeiculo() {
        return valorVeiculo;
    }
    public BigDecimal getEntrada() {
        return entrada;
    }
    public int getParcelas() {
        return parcelas;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public List<Veiculo> getCarrosSelecionados() {
        return carrosSelecionados;
    }
    public BigDecimal getValorTotalFinal() {
        return valorVeiculo.subtract(entrada);
    }
    @Override
    public String toString() {
        String carro = carrosSelecionados.isEmpty()
                ? "nenhum veículo"
                : carrosSelecionados.get(0).getMarca() + " " + carrosSelecionados.get(0).getModelo();
        return "Proposta#" + id +
                " Cliente:" + (cliente != null ? cliente.getNome() : "N/A") +
                " Veículo:" + carro +
                " Valor:" + valorVeiculo +
                " Entrada:" + entrada +
                " Parcelas:" + parcelas;

    }
    public BigDecimal getValorFinal() {
        return valorTotal;
    }
    public BigDecimal getValorTotal() {
        return valorTotal;
    }
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}