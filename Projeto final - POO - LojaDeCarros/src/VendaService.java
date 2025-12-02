import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class VendaService {
    private final RepositorioMemoria repo;
    public VendaService(RepositorioMemoria repo) {
        this.repo = repo;
    }

    public BigDecimal simularFinanciamento(BigDecimal valorTotal, BigDecimal entrada, int parcelas, BigDecimal jurosAnualPercent) {
        if (valorTotal == null) throw new IllegalArgumentException("valorTotal não pode ser nulo");
        if (entrada == null) entrada = BigDecimal.ZERO;
        if (parcelas <= 0) return valorTotal.subtract(entrada).setScale(2, RoundingMode.HALF_UP);

        BigDecimal saldo = valorTotal.subtract(entrada);
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (jurosAnualPercent == null) jurosAnualPercent = BigDecimal.ZERO;

        BigDecimal jurosMensal = jurosAnualPercent.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        if (jurosMensal.compareTo(BigDecimal.ZERO) == 0) {
            return saldo.divide(BigDecimal.valueOf(parcelas), 2, RoundingMode.HALF_UP);
        }

        BigDecimal onePlusI = BigDecimal.ONE.add(jurosMensal);
        BigDecimal pow = onePlusI.pow(parcelas);
        BigDecimal numer = saldo.multiply(jurosMensal).multiply(pow);
        BigDecimal denom = pow.subtract(BigDecimal.ONE);
        return numer.divide(denom, 2, RoundingMode.HALF_UP);
    }

    public Contrato negociarEFormalizar(Proposta proposta, Vendedor vendedor, LocalDate dataAssinatura) {
        if (proposta == null) throw new IllegalArgumentException("Proposta nula");
        if (vendedor == null) throw new IllegalArgumentException("Vendedor nulo");

        List<Veiculo> carros = proposta.getCarrosSelecionados();
        if (carros == null || carros.isEmpty()) return null;

        Veiculo v = carros.get(0);
        if (!"disponivel".equalsIgnoreCase(v.getStatus())) return null;

        Cliente c = proposta.getCliente();
        Contrato contrato = new Contrato(c, vendedor, v, dataAssinatura != null ? dataAssinatura : LocalDate.now(), proposta.getValorTotal());
        boolean ok = contrato.formalizar();
        if (ok) {
            repo.addContrato(contrato);
            return contrato;
        }
        return null;
    }
}