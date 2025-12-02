package main.java;

public class Cliente extends Pessoa {
    private final int id;
    private String telefone;
    private String necessidades;

    public Cliente(int id, String nome, String cpf, String telefone, String necessidades) {
        super(nome, cpf);
        this.id = id;
        this.telefone = telefone;
        this.necessidades = necessidades;
    }

    public int getId() { return id; }
    public String getTelefone() { return telefone; }
    public String getNecessidades() { return necessidades; }

    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setNecessidades(String necessidades) { this.necessidades = necessidades; }

    @Override
    public String toString() {
        return String.format("Cliente #%d - %s, Tel: %s, Necessidades: %s", id, nome, telefone, necessidades);
    }
}