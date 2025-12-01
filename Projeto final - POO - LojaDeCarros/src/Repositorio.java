import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface Repositorio {
    // Clientes
    void addCliente(String nome, String cpf, String telefone, String necessidades);
    void addCliente(Cliente c);
    Cliente getCliente(int id);
    Collection<Cliente> getAllClientes();
    void updateCliente(Cliente c);
    boolean removeCliente(int id);

    // Vendedores
    void addVendedor(String nome, String cpf);
    void addVendedor(Vendedor v);
    Vendedor getVendedor(int id);
    Collection<Vendedor> getAllVendedores();
    boolean removeVendedor(int id);

    // Veículos
    void addVeiculo(Veiculo v);
    Veiculo getVeiculo(int id);
    Collection<Veiculo> getAllVeiculos();
    boolean removeVeiculo(int id);
    void updateVeiculo(Veiculo v);

    // Contratos
    void addContrato(Contrato c);
    List<Contrato> getContratos();

    // Métodos de filtro
    List<Veiculo> filtrarVeiculos(String marca, Integer anoMin, Integer anoMax,
                                  BigDecimal precoMin, BigDecimal precoMax);
    List<Contrato> relatorioPorPeriodo(LocalDate inicio, LocalDate fim);
    List<Contrato> relatorioPorVendedor(int vendedorId);

    // Inicialização
    void initializeDatabase();
    void seedData();
}