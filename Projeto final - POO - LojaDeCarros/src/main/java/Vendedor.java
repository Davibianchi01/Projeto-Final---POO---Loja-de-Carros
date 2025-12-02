package main.java;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Vendedor extends Pessoa {
    private final int id;

    public Vendedor(int id, String nome, String cpf) {
        super(nome, cpf);
        this.id = id;
    }
    public Vendedor(String nome, String cpf, int id) { this(id, nome, cpf); }
    public int getId() { return id; }

    @Override
    public String toString() {
        return String.format("Vendedor #%d - %s", id, nome);
    }

    public List<Veiculo> filtrarNecessidade(Cliente cliente, List<Veiculo> allVehicles) {
        String key;
        key = (cliente == null || cliente.getNecessidades() == null) ? "" : cliente.getNecessidades().trim().toLowerCase();
        final String k = key;
        return allVehicles.stream()
                .filter(Objects::nonNull)
                .filter(v -> "disponivel".equalsIgnoreCase(v.getStatus()))
                .filter(v -> {
                    if (k.isEmpty()) return true;
                    if (v.getMarca() != null && v.getMarca().toLowerCase().contains(k)) return true;
                    if (v.getModelo() != null && v.getModelo().toLowerCase().contains(k)) return true;
                    return String.valueOf(v.getAno()).contains(k);
                })
                .collect(Collectors.toList());
    }
}