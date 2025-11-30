
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicInteger;

public class Veiculo {
    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private final int id;
    private String marca;
    private String modelo;
    private int ano;
    private BigDecimal preco;
    private String status;

    public Veiculo(String marca, String modelo, int ano, BigDecimal preco) {
        this.id = SEQ.getAndIncrement();
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.preco = preco == null ? BigDecimal.ZERO : preco;
        this.status = "disponivel";
    }

    public int getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAno() { return ano; }
    public BigDecimal getPreco() { return preco; }
    public String getStatus() { return status; }

    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setAno(int ano) { this.ano = ano; }
    public void setPreco(BigDecimal preco) { this.preco = preco == null ? BigDecimal.ZERO : preco; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Veículo #%d - %s %s (%d) - R$ %s - %s",
                id,
                marca,
                modelo,
                ano,
                preco.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                status);
    }
}
