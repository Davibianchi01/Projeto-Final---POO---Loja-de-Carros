import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RepositorioMemoria {
    private final Map<Integer, Cliente> clientes = new HashMap<>();
    private final Map<Integer, Vendedor> vendedores = new HashMap<>();
    private final Map<Integer, Veiculo> veiculos = new HashMap<>();
    private final List<Contrato> contratos = new ArrayList<>();

    private int clienteSeq = 1;
    private int vendedorSeq = 1;

    public RepositorioMemoria() {
        seedData();
    }

    private void seedData() {
        addVendedor(new Vendedor(nextVendedorId(), "Carlos Silva", "111.111.111-11"));
        addVendedor(new Vendedor(nextVendedorId(), "Mariana Souza", "222.222.222-22"));
        addVendedor(new Vendedor(nextVendedorId(), "João Pereira", "333.333.333-33"));
        addVendedor(new Vendedor(nextVendedorId(), "Ana Martins", "444.444.444-44"));

        addVeiculo(new Veiculo("Toyota", "Corolla", 2018, new BigDecimal("88000")));
        addVeiculo(new Veiculo("Volkswagen", "Golf", 2017, new BigDecimal("72000")));
        addVeiculo(new Veiculo("Ford", "Ka", 2015, new BigDecimal("32000")));
        addVeiculo(new Veiculo("Chevrolet", "Onix", 2019, new BigDecimal("55000")));
        addVeiculo(new Veiculo("Honda", "Civic", 2020, new BigDecimal("98000")));
        addVeiculo(new Veiculo("Fiat", "Uno", 2014, new BigDecimal("22000")));
        addVeiculo(new Veiculo("Renault", "Sandero", 2016, new BigDecimal("30000")));
        addVeiculo(new Veiculo("Hyundai", "HB20", 2018, new BigDecimal("60000")));
        addVeiculo(new Veiculo("Nissan", "Kicks", 2019, new BigDecimal("82000")));
        addVeiculo(new Veiculo("BMW", "320i", 2015, new BigDecimal("120000")));
        addVeiculo(new Veiculo("Mercedes", "C180", 2016, new BigDecimal("140000")));
        addVeiculo(new Veiculo("Audi", "A3", 2017, new BigDecimal("130000")));
        addVeiculo(new Veiculo("Chevrolet", "Cruze", 2018, new BigDecimal("70000")));
        addVeiculo(new Veiculo("Fiat", "Argo", 2020, new BigDecimal("48000")));
        addVeiculo(new Veiculo("Volkswagen", "Polo", 2019, new BigDecimal("62000")));
        addVeiculo(new Veiculo("Toyota", "Yaris", 2016, new BigDecimal("46000")));
        addVeiculo(new Veiculo("Renault", "Kwid", 2021, new BigDecimal("36000")));
        addVeiculo(new Veiculo("Ford", "EcoSport", 2017, new BigDecimal("68000")));
        addVeiculo(new Veiculo("Honda", "Fit", 2015, new BigDecimal("40000")));
        addVeiculo(new Veiculo("Jeep", "Renegade", 2019, new BigDecimal("90000")));
        addVeiculo(new Veiculo("Hyundai", "Tucson", 2018, new BigDecimal("105000")));
        addVeiculo(new Veiculo("Chevrolet", "Tracker", 2020, new BigDecimal("93000")));
        addVeiculo(new Veiculo("Nissan", "Sentra", 2016, new BigDecimal("45000")));
        addVeiculo(new Veiculo("BMW", "X1", 2018, new BigDecimal("160000")));
        addVeiculo(new Veiculo("Mercedes", "A200", 2020, new BigDecimal("180000")));
    }

    private int nextClienteId() { return clienteSeq++; }
    private int nextVendedorId() { return vendedorSeq++; }

    public void addCliente(String nome, String cpf, String telefone, String necessidades) {
        Cliente c = new Cliente(nextClienteId(), nome, cpf, telefone, necessidades);
        clientes.put(c.getId(), c);
    }

    public void addCliente(Cliente c) { if (c != null) clientes.put(c.getId(), c); }

    public Cliente getCliente(int id) { return clientes.get(id); }

    public Collection<Cliente> getAllClientes() { return Collections.unmodifiableCollection(clientes.values()); }

    public void updateCliente(Cliente c) { if (c != null) clientes.put(c.getId(), c); }

    public Cliente removeCliente(int id) { return clientes.remove(id); }

    public void addVendedor(Vendedor v) { if (v != null) vendedores.put(v.getId(), v); }

    public void addVendedor(String nome, String cpf) {
        Vendedor v = new Vendedor(nextVendedorId(), nome, cpf);
        vendedores.put(v.getId(), v);
    }

    public Vendedor getVendedor(int id) { return vendedores.get(id); }

    public Collection<Vendedor> getAllVendedores() { return Collections.unmodifiableCollection(vendedores.values()); }

    public Vendedor removeVendedor(int id) { return vendedores.remove(id); }

    public void addVeiculo(Veiculo v) { if (v != null) veiculos.put(v.getId(), v); }

    public Veiculo getVeiculo(int id) { return veiculos.get(id); }

    public Collection<Veiculo> getAllVeiculos() { return Collections.unmodifiableCollection(veiculos.values()); }

    public Veiculo removeVeiculo(int id) { return veiculos.remove(id); }

    public void updateVeiculo(Veiculo v) { if (v != null) veiculos.put(v.getId(), v); }

    public void addContrato(Contrato c) { if (c != null) contratos.add(c); }

    public List<Contrato> getContratos() { return Collections.unmodifiableList(contratos); }

    public List<Veiculo> filtrarVeiculos(String marca, Integer anoMin, Integer anoMax, BigDecimal precoMin, BigDecimal precoMax) {
        final String marcaLower = (marca == null ? null : marca.trim().toLowerCase());
        return veiculos.values().stream()
                .filter(v -> marcaLower == null || (v.getMarca() != null && v.getMarca().toLowerCase().contains(marcaLower)))
                .filter(v -> anoMin == null || v.getAno() >= anoMin)
                .filter(v -> anoMax == null || v.getAno() <= anoMax)
                .filter(v -> precoMin == null || v.getPreco().compareTo(precoMin) >= 0)
                .filter(v -> precoMax == null || v.getPreco().compareTo(precoMax) <= 0)
                .collect(Collectors.toList());
    }

    public List<Contrato> relatorioPorPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) return Collections.emptyList();
        return contratos.stream()
                .filter(c -> !c.getData().isBefore(inicio) && !c.getData().isAfter(fim))
                .collect(Collectors.toList());
    }

    public List<Contrato> relatorioPorVendedor(int vendedorId) {
        return contratos.stream()
                .filter(c -> c.getVendedor() != null && c.getVendedor().getId() == vendedorId)
                .collect(Collectors.toList());
    }
}
