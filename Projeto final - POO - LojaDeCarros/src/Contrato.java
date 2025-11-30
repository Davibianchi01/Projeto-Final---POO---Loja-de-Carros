import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Contrato {
    private static int contadorIds = 1;
    private int id;
    private Cliente cliente;
    private Vendedor vendedor;
    private LocalDate data;
    private BigDecimal valorTotal;
    private BigDecimal saldoAPagar;
    private List<Veiculo> veiculos;

    public Contrato(Cliente cliente, Vendedor vendedor, Veiculo veiculo, LocalDate data, BigDecimal valorTotal) {
        this.id = contadorIds++;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.data = data;
        this.valorTotal = valorTotal != null ? valorTotal : (veiculo != null ? veiculo.getPreco() : BigDecimal.ZERO);
        BigDecimal entrada = null;
        this.saldoAPagar = this.valorTotal.subtract(BigDecimal.ZERO);
        this.veiculos = new ArrayList<>();
        if (veiculo != null) this.veiculos.add(veiculo);
    }

    public Contrato(Cliente cliente, Vendedor vendedor, LocalDate data, BigDecimal valorTotal, BigDecimal entrada, List<Veiculo> veiculos) {
        this.id = contadorIds++;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.data = data;
        this.valorTotal = valorTotal != null ? valorTotal : BigDecimal.ZERO;
        this.saldoAPagar = this.valorTotal.subtract(entrada != null ? entrada : BigDecimal.ZERO);
        this.veiculos = veiculos != null ? veiculos : new ArrayList<>();
    }

    public Contrato(Main.Proposta p, LocalDate data) {
        this.id = contadorIds++;
        this.cliente = p.getCliente();
        this.vendedor = p.getVendedor();
        this.data = data;
        this.valorTotal = p.getValorTotal() != null ? p.getValorTotal() : BigDecimal.ZERO;
        this.saldoAPagar = this.valorTotal.subtract(
                p.getEntrada() != null ? p.getEntrada() : BigDecimal.ZERO
        );
        this.veiculos = new ArrayList<>(p.getCarrosSelecionados());
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public LocalDate getData() {
        return data;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getSaldoAPagar() {
        return saldoAPagar;
    }

    public List<Veiculo> getVeiculos() {
        return veiculos;
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        if (veiculos == null) veiculos = new ArrayList<>();
        veiculos.add(veiculo);
        valorTotal = valorTotal.add(veiculo.getPreco());
        saldoAPagar = saldoAPagar.add(veiculo.getPreco());
    }

    public boolean formalizar() {
        if (veiculos == null || veiculos.isEmpty()) return false;
        for (Veiculo v : veiculos) v.setStatus("vendido");
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== CONTRATO #").append(id).append(" ===\n");
        sb.append("Cliente: ").append(cliente != null ? cliente.getNome() : "N/A").append("\n");
        sb.append("Vendedor: ").append(vendedor != null ? vendedor.getNome() : "N/A").append("\n");
        sb.append("Data: ").append(data != null ? data : "N/A").append("\n");
        sb.append("Valor Total: R$ ").append(valorTotal != null ? valorTotal : 0).append("\n");
        sb.append("Saldo a Pagar: R$ ").append(saldoAPagar != null ? saldoAPagar : 0).append("\n");
        sb.append("Veículos:\n");
        if (veiculos == null || veiculos.isEmpty()) {
            sb.append(" (nenhum veículo)\n");
        } else {
            for (Veiculo v : veiculos) {
                sb.append(" - ").append(v.getMarca()).append(" ").append(v.getModelo()).append("\n");
            }
        }
        sb.append("=========================\n");
        return sb.toString();
    }
}
