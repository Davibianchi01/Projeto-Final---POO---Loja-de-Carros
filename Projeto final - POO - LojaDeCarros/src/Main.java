import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final String C_RESET = "\u001B[0m";
    private static final String C_RED = "\u001B[31m";
    private static final String C_GREEN = "\u001B[32m";
    private static final String C_YELLOW = "\u001B[33m";
    private static final String C_BLUE = "\u001B[34m";

    private static final Scanner sc = new Scanner(System.in);
    // ALTERAÇÃO: Mudado de RepositorioMemoria para RepositorioJDBC
    private static final Repositorio repo = new RepositorioJDBC();
    private static AdvancedReport lastAdvancedReport = null;

    public static void main(String[] args) {

        // ALTERAÇÃO: Inicializar banco de dados
        System.out.println(C_BLUE + "Inicializando banco de dados..." + C_RESET);
        try {
            ((RepositorioJDBC) repo).initializeDatabase();
            ((RepositorioJDBC) repo).seedData();
            System.out.println(C_GREEN + "✅ Banco de dados inicializado!" + C_RESET);
        } catch (Exception e) {
            System.out.println(C_RED + "Erro ao inicializar banco: " + e.getMessage() + C_RESET);
        }

        // ALTERAÇÃO: Carregar clientes iniciais do banco (se não existirem)
        carregarClientesIniciais();

        System.out.println("\n========================================");
        System.out.println("🚗  SISTEMA DE VENDA DE CARROS INICIADO");
        System.out.println("========================================");
        System.out.println("✅ " + repo.getAllVeiculos().size() + " carros carregados");
        System.out.println("✅ " + repo.getAllVendedores().size() + " vendedores disponíveis");
        System.out.println("✅ " + repo.getAllClientes().size() + " clientes cadastrados");
        System.out.println("----------------------------------------");

        int opc;
        do {
            System.out.println("\n" + C_BLUE + "Menu Principal:" + C_RESET);
            System.out.println("1 - Cadastro (Veículos, Clientes, Vendedores)");
            System.out.println("2 - Consulta / CRUD");
            System.out.println("3 - Ver carros na garagem");
            System.out.println("4 - Venda / Propostas");
            System.out.println("5 - Relatórios avançados");
            System.out.println("6 - Exportar relatório (TXT/CSV/JSON)");
            System.out.println("7 - Agendar Test-Drive");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            opc = lerInt();

            switch (opc) {
                case 1 -> menuCadastro();
                case 2 -> menuConsulta();
                case 3 -> mostrarGaragem();
                case 4 -> menuVendas();
                case 5 -> gerarRelatorioAvancado();
                case 6 -> exportarRelatorioMenu();
                case 7 -> agendarTestDrive();
                case 0 -> {
                    System.out.println(C_GREEN + "Encerrando..." + C_RESET);
                    // ALTERAÇÃO: Fechar conexão com o banco
                    DatabaseConnection.closeConnection();
                }
                default -> System.out.println(C_YELLOW + "Opção inválida." + C_RESET);
            }

        } while (opc != 0);

        sc.close();
    }

    private static void carregarClientesIniciais() {
        // ALTERAÇÃO: Verificar se já existem clientes no banco antes de adicionar
        if (repo.getAllClientes().size() < 3) {
            repo.addCliente("Ana Silva", "12345678901", "11988887777", "Sedan");
            repo.addCliente("Bruno Santos", "23456789012", "11977776666", "SUV");
            repo.addCliente("Carla Oliveira", "34567890123", "11966665555", "Hatch");
        }
    }

    private static void menuCadastro() {
        System.out.println("\nCadastro:");
        System.out.println("1 - Veículo");
        System.out.println("2 - Cliente");
        System.out.println("3 - Vendedor");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");

        int op = lerInt();

        switch (op) {
            case 1 -> cadastrarVeiculo();
            case 2 -> cadastrarCliente();
            case 3 -> cadastrarVendedor();
            case 0 -> {}
            default -> System.out.println(C_YELLOW + "Opção inválida." + C_RESET);
        }
    }

    private static void menuConsulta() {
        System.out.println("\nConsulta / CRUD:");
        System.out.println("1 - Clientes");
        System.out.println("2 - Vendedores");
        System.out.println("3 - Veículos");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");

        int op = lerInt();

        switch (op) {
            case 1 -> repo.getAllClientes().forEach(System.out::println);
            case 2 -> repo.getAllVendedores().forEach(System.out::println);
            case 3 -> repo.getAllVeiculos().forEach(System.out::println);
            case 0 -> {}
            default -> System.out.println(C_YELLOW + "Opção inválida." + C_RESET);
        }
    }

    private static void menuVendas() {
        System.out.println("\nVendas / Propostas:");
        System.out.println("1 - Criar proposta (cliente escolhe veículo)");
        System.out.println("2 - Simular financiamento (Price)");
        System.out.println("3 - Aceitar proposta e formalizar venda");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");

        int op = lerInt();

        switch (op) {
            case 1 -> criarPropostaFlow();
            case 2 -> simuladorPriceFlow();
            case 3 -> aceitarPropostaFlow();
            case 0 -> {}
            default -> System.out.println(C_YELLOW + "Opção inválida." + C_RESET);
        }
    }

    private static void cadastrarCliente() {
        String nome = lerStringComPadrao("Nome completo (mín. 2 palavras): ", ".+\\s+.+", "Nome inválido, use pelo menos 2 palavras.");
        String cpf = lerStringComPadrao("CPF (11 dígitos): ", "\\d{11}", "CPF inválido. Use 11 dígitos.");
        String telefone = lerStringComPadrao("Telefone (8-15 dígitos): ", "\\d{8,15}", "Telefone inválido.");
        System.out.print("Necessidades: ");
        String nec = sc.nextLine().trim();

        repo.addCliente(nome, cpf, telefone, nec);
        System.out.println(C_GREEN + "Cliente cadastrado!" + C_RESET);
    }

    private static void cadastrarVendedor() {
        String nome = lerStringComPadrao("Nome completo (mín. 2 palavras): ", ".+\\s+.+", "Nome inválido, use pelo menos 2 palavras.");
        String cpf = lerStringComPadrao("CPF (11 dígitos): ", "\\d{11}", "CPF inválido. Use 11 dígitos.");

        repo.addVendedor(nome, cpf);
        System.out.println(C_GREEN + "Vendedor cadastrado!" + C_RESET);
    }

    private static void cadastrarVeiculo() {
        String marca = lerStringComPadrao("Marca: ", "[\\p{L}0-9 ]{1,50}", "Marca inválida. Use letras/números.");
        String modelo = lerStringComPadrao("Modelo: ", "[\\p{L}0-9 .\\-_/]{1,60}", "Modelo inválido.");
        int ano = lerIntComIntervalo("Ano: ", 1900, LocalDate.now().getYear());
        BigDecimal preco = lerBigDecimalPositivo("Preço: ");

        Veiculo v = new Veiculo(marca.trim(), modelo.trim(), ano, preco);
        v.setStatus("disponivel");
        repo.addVeiculo(v);

        System.out.println(C_GREEN + "Veículo cadastrado com sucesso!" + C_RESET);
    }

    private static void mostrarGaragem() {
        System.out.println("\n🏠 Garagem:");
        List<Veiculo> veiculos = repo.getAllVeiculos().stream().toList();
        if (veiculos.isEmpty()) {
            System.out.println("Nenhum veículo cadastrado.");
            return;
        }
        veiculos.forEach(System.out::println);
    }

    private static final Map<Integer, Proposta> propostasPendentes = new HashMap<>();
    private static int propostaSeq = 1;

    private static void criarPropostaFlow() {
        System.out.println("\nCriar Proposta:");

        System.out.println("\nClientes:");
        repo.getAllClientes().forEach(c -> System.out.println(c.getId() + " - " + c.getNome()));
        int idCliente = lerIntComPrompt("ID do Cliente: ");
        Cliente cliente = repo.getCliente(idCliente);
        if (cliente == null) { System.out.println(C_RED + "Cliente não encontrado." + C_RESET); return; }

        System.out.println("\nVendedores:");
        repo.getAllVendedores().forEach(v -> System.out.println(v.getId() + " - " + v.getNome()));
        int idVendedor = lerIntComPrompt("ID do Vendedor responsável (registro): ");
        Vendedor vendedor = repo.getVendedor(idVendedor);
        if (vendedor == null) { System.out.println(C_RED + "Vendedor não encontrado." + C_RESET); return; }

        System.out.println("\nVeículos disponíveis:");
        repo.getAllVeiculos().stream()
                .filter(v -> "disponivel".equalsIgnoreCase(v.getStatus()))
                .forEach(v -> System.out.println(v.getId() + " - " + v.getMarca() + " " + v.getModelo() + " - R$ " + v.getPreco()));

        int idVeiculo = lerIntComPrompt("ID do Veículo escolhido: ");
        Veiculo veiculo = repo.getVeiculo(idVeiculo);
        if (veiculo == null || !"disponivel".equalsIgnoreCase(veiculo.getStatus())) {
            System.out.println(C_RED + "Veículo inválido/disponibilidade." + C_RESET); return;
        }

        BigDecimal entrada = lerBigDecimalPositivoAllowZero("Entrada (0 para nenhuma): ");
        int parcelas = lerIntComIntervalo("Parcelas (1-360): ", 1, 360);
        BigDecimal jurosAnualPercent = lerBigDecimalAllowZero("Juros anual (%) (0 para sem juros): ");

        Proposta p = new Proposta(propostaSeq++, cliente, vendedor, List.of(veiculo), veiculo.getPreco(), entrada, parcelas, jurosAnualPercent);
        propostasPendentes.put(p.getId(), p);
        System.out.println(C_GREEN + "Proposta criada! ID proposta: " + p.getId() + C_RESET);
        System.out.println("-> Para aceitar e formalizar vá em 'Vendas / Aceitar proposta' e informe o ID.");
    }

    private static void simuladorPriceFlow() {
        System.out.println("\nSimulador (Tabela Price) - calcula parcela mensal");

        BigDecimal valor = lerBigDecimalPositivo("Valor total (R$): ");
        BigDecimal entrada = lerBigDecimalPositivoAllowZero("Entrada (0 para nenhuma): ");
        int parcelas = lerIntComIntervalo("Parcelas (1-360): ", 1, 360);
        BigDecimal jurosAnualPercent = lerBigDecimalAllowZero("Juros anual (%) (0 para sem juros): ");

        BigDecimal parcela = calcularParcelaPrice(valor, entrada, parcelas, jurosAnualPercent);
        System.out.println(C_GREEN + "Parcela (mensal) estimada: R$ " + parcela.setScale(2, RoundingMode.HALF_UP) + C_RESET);
    }

    private static void aceitarPropostaFlow() {
        if (propostasPendentes.isEmpty()) {
            System.out.println(C_YELLOW + "Não há propostas pendentes." + C_RESET);
            return;
        }

        System.out.println("\nPropostas pendentes:");
        propostasPendentes.values().forEach(System.out::println);

        int id = lerIntComPrompt("ID da proposta a aceitar: ");
        Proposta p = propostasPendentes.get(id);
        if (p == null) {
            System.out.println(C_RED + "Proposta não encontrada." + C_RESET);
            return;
        }

        Veiculo v = p.getCarrosSelecionados().get(0);
        if (!"disponivel".equalsIgnoreCase(v.getStatus())) {
            System.out.println(C_YELLOW + "Veículo não está mais disponível." + C_RESET);
            propostasPendentes.remove(id);
            return;
        }

        Contrato contrato = new Contrato(p, LocalDate.now());
        contrato.formalizar();
        repo.addContrato(contrato);
        repo.updateVeiculo(v);

        propostasPendentes.remove(id);
        System.out.println(C_GREEN + "Proposta aceita e venda formalizada. Contrato criado." + C_RESET);
    }

    private static BigDecimal calcularParcelaPrice(BigDecimal valorTotal, BigDecimal entrada, int nParcelas, BigDecimal jurosAnualPercent) {
        if (valorTotal == null) throw new IllegalArgumentException("valorTotal nulo");
        if (entrada == null) entrada = BigDecimal.ZERO;
        BigDecimal saldo = valorTotal.subtract(entrada);
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (jurosAnualPercent == null) jurosAnualPercent = BigDecimal.ZERO;

        BigDecimal i = jurosAnualPercent.divide(BigDecimal.valueOf(12 * 100), 20, RoundingMode.HALF_UP);
        return getBigDecimal(nParcelas, saldo, i);
    }

    static BigDecimal getBigDecimal(int nParcelas, BigDecimal saldo, BigDecimal i) {
        if (i.compareTo(BigDecimal.ZERO) == 0) {
            return saldo.divide(BigDecimal.valueOf(nParcelas), 2, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusI = BigDecimal.ONE.add(i);
        BigDecimal pow = onePlusI.pow(nParcelas);
        BigDecimal numer = saldo.multiply(i).multiply(pow);
        BigDecimal denom = pow.subtract(BigDecimal.ONE);
        BigDecimal parcela = numer.divide(denom, 10, RoundingMode.HALF_UP);
        return parcela.setScale(2, RoundingMode.HALF_UP);
    }

    private static void gerarRelatorioAvancado() {
        List<Contrato> contratos = repo.getContratos();
        List<Veiculo> todos = repo.getAllVeiculos().stream().toList();

        BigDecimal faturamento = contratos.stream()
                .map(Contrato::getValorTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Integer, Long> vendasPorVendedor = contratos.stream()
                .filter(c -> c.getVendedor() != null)
                .collect(Collectors.groupingBy(c -> c.getVendedor().getId(), Collectors.counting()));

        double mediaVenda = contratos.stream()
                .map(Contrato::getValorTotal)
                .filter(Objects::nonNull)
                .mapToDouble(b -> b.doubleValue())
                .average().orElse(0.0);

        long disponiveis = todos.stream().filter(v -> "disponivel".equalsIgnoreCase(v.getStatus())).count();
        long vendidos = todos.stream().filter(v -> "vendido".equalsIgnoreCase(v.getStatus())).count();

        AdvancedReport rpt = new AdvancedReport(faturamento, vendasPorVendedor, mediaVenda, disponiveis, vendidos, contratos);
        lastAdvancedReport = rpt;

        System.out.println("\n=== RELATÓRIO AVANÇADO ===");
        System.out.println("Faturamento total: R$ " + faturamento.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Média por venda: R$ " + BigDecimal.valueOf(rpt.mediaVenda).setScale(2, RoundingMode.HALF_UP));
        System.out.println("Veículos disponíveis: " + rpt.disponiveis);
        System.out.println("Veículos vendidos: " + rpt.vendidos);
        System.out.println("Vendas por vendedor:");
        rpt.vendasPorVendedor.forEach((vid, qtd) -> {
            Vendedor v = repo.getVendedor(vid);
            String nome = v == null ? ("Vendedor#" + vid) : v.getNome();
            System.out.println("  - " + nome + ": " + qtd);
        });

        System.out.println("\nContratos listados:");
        rpt.contratos.forEach(System.out::println);
    }

    private static void exportarRelatorioMenu() {
        if (lastAdvancedReport == null) {
            System.out.println(C_YELLOW + "Nenhum relatório avançado gerado ainda. Gere um relatório antes de exportar." + C_RESET);
            return;
        }
        System.out.println("\nExportar relatório em:");
        System.out.println("1 - TXT");
        System.out.println("2 - CSV");
        System.out.println("3 - JSON");
        System.out.print("Escolha: ");
        int op = lerInt();
        System.out.print("Nome do arquivo (sem extensão): ");
        String base = sc.nextLine().trim();

        try {
            switch (op) {
                case 1 -> exportTxt(base + ".txt", lastAdvancedReport);
                case 2 -> exportCsv(base + ".csv", lastAdvancedReport);
                case 3 -> exportJson(base + ".json", lastAdvancedReport);
                default -> System.out.println(C_YELLOW + "Opção inválida." + C_RESET);
            }
        } catch (IOException e) {
            System.out.println(C_RED + "Erro ao exportar: " + e.getMessage() + C_RESET);
        }
    }

    private static void exportTxt(String filename, AdvancedReport rpt) throws IOException {
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(rpt.toTxt());
        }
        System.out.println(C_GREEN + "Exportado TXT: " + filename + C_RESET);
    }

    private static void exportCsv(String filename, AdvancedReport rpt) throws IOException {
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(rpt.toCsv());
        }
        System.out.println(C_GREEN + "Exportado CSV: " + filename + C_RESET);
    }

    private static void exportJson(String filename, AdvancedReport rpt) throws IOException {
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(rpt.toJson());
        }
        System.out.println(C_GREEN + "Exportado JSON: " + filename + C_RESET);
    }

    private static final Map<Integer, TestDrive> agenda = new HashMap<>();
    private static int testDriveSeq = 1;

    private static void agendarTestDrive() {
        System.out.println("\nAgendar Test-Drive:");

        System.out.println("Clientes:");
        repo.getAllClientes().forEach(c -> System.out.println(c.getId() + " - " + c.getNome()));
        int idCliente = lerIntComPrompt("ID cliente: ");
        Cliente cliente = repo.getCliente(idCliente);
        if (cliente == null) { System.out.println(C_RED + "Cliente não encontrado." + C_RESET); return; }

        System.out.println("Veículos disponíveis:");
        repo.getAllVeiculos().stream()
                .filter(v -> "disponivel".equalsIgnoreCase(v.getStatus()))
                .forEach(v -> System.out.println(v.getId() + " - " + v.getMarca() + " " + v.getModelo()));

        int idVeiculo = lerIntComPrompt("ID veículo: ");
        Veiculo veiculo = repo.getVeiculo(idVeiculo);
        if (veiculo == null) { System.out.println(C_RED + "Veículo não encontrado." + C_RESET); return; }

        System.out.print("Data do test-drive (YYYY-MM-DD): ");
        LocalDate data;
        try {
            data = LocalDate.parse(sc.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println(C_YELLOW + "Data inválida." + C_RESET); return;
        }

        TestDrive td = new TestDrive(testDriveSeq++, cliente, veiculo, data);
        agenda.put(td.getId(), td);
        System.out.println(C_GREEN + "Test-Drive agendado (ID " + td.getId() + "): " + data + C_RESET);
    }

    private static int lerInt() {
        while (true) {
            String line = sc.nextLine().trim();
            try { return Integer.parseInt(line); }
            catch (Exception e) { System.out.print(C_YELLOW + "Valor inválido. Digite novamente: " + C_RESET); }
        }
    }

    private static int lerIntComPrompt(String prompt) {
        System.out.print(prompt);
        return lerInt();
    }

    private static int lerIntComIntervalo(String prompt, int min, int max) {
        System.out.print(prompt);
        while (true) {
            String line = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val < min || val > max) { System.out.print(C_YELLOW + "Fora do intervalo. Tente: " + C_RESET); }
                else return val;
            } catch (Exception e) { System.out.print(C_YELLOW + "Valor inválido. Digite novamente: " + C_RESET); }
        }
    }

    private static BigDecimal lerBigDecimalPositivo(String prompt) {
        System.out.print(prompt);
        while (true) {
            String line = sc.nextLine().trim();
            try {
                BigDecimal bd = new BigDecimal(line);
                if (bd.compareTo(BigDecimal.ZERO) <= 0) { System.out.print(C_YELLOW + "Deve ser >0. Tente: " + C_RESET); }
                else return bd;
            } catch (Exception e) { System.out.print(C_YELLOW + "Número inválido. Digite novamente: " + C_RESET); }
        }
    }

    private static BigDecimal lerBigDecimalPositivoAllowZero(String prompt) {
        System.out.print(prompt);
        while (true) {
            String line = sc.nextLine().trim();
            try {
                BigDecimal bd = new BigDecimal(line);
                if (bd.compareTo(BigDecimal.ZERO) < 0) { System.out.print(C_YELLOW + "Deve ser >=0. Tente: " + C_RESET); }
                else return bd;
            } catch (Exception e) { System.out.print(C_YELLOW + "Número inválido. Digite novamente: " + C_RESET); }
        }
    }

    private static BigDecimal lerBigDecimalAllowZero(String prompt) {
        return lerBigDecimalPositivoAllowZero(prompt);
    }

    private static String lerStringComPadrao(String prompt, String regex, String erroMsg) {
        Pattern p = Pattern.compile(regex);
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (p.matcher(line).matches()) return line;
            System.out.println(C_YELLOW + erroMsg + C_RESET);
        }
    }

    static class Proposta {
        private final int id;
        private final Cliente cliente;
        private final Vendedor vendedor;
        private final List<Veiculo> carrosSelecionados;
        private final BigDecimal valorTotal;
        private final BigDecimal entrada;
        private final int parcelas;
        private final BigDecimal jurosAnualPercent;

        public Proposta(int id, Cliente cliente, Vendedor vendedor, List<Veiculo> carrosSelecionados,
                        BigDecimal valorTotal, BigDecimal entrada, int parcelas, BigDecimal jurosAnualPercent) {
            this.id = id;
            this.cliente = cliente;
            this.vendedor = vendedor;
            this.carrosSelecionados = carrosSelecionados;
            this.valorTotal = valorTotal;
            this.entrada = entrada;
            this.parcelas = parcelas;
            this.jurosAnualPercent = jurosAnualPercent;
        }

        public int getId() { return id; }
        public Cliente getCliente() { return cliente; }
        public Vendedor getVendedor() { return vendedor; }
        public List<Veiculo> getCarrosSelecionados() { return carrosSelecionados; }
        public BigDecimal getValorTotal() { return valorTotal; }
        public BigDecimal getEntrada() { return entrada; }
        public int getParcelas() { return parcelas; }
        public BigDecimal getJurosAnualPercent() { return jurosAnualPercent; }

        @Override
        public String toString() {
            return "Proposta#" + id + " Cliente:" + cliente.getNome() + " Vendedor:" + vendedor.getNome() +
                    " Veículo:" + carrosSelecionados.get(0).getMarca() + " " + carrosSelecionados.get(0).getModelo() +
                    " Valor:" + valorTotal + " Entrada:" + entrada + " Parcelas:" + parcelas + " Juros%:" + jurosAnualPercent;
        }
    }

    private static class TestDrive {
        private final int id;
        private final Cliente cliente;
        private final Veiculo veiculo;
        private final LocalDate data;

        public TestDrive(int id, Cliente cliente, Veiculo veiculo, LocalDate data) {
            this.id = id;
            this.cliente = cliente;
            this.veiculo = veiculo;
            this.data = data;
        }

        public int getId() { return id; }
        public Cliente getCliente() { return cliente; }
        public Veiculo getVeiculo() { return veiculo; }
        public LocalDate getData() { return data; }

        @Override
        public String toString() {
            return "TestDrive#" + id + " Cliente:" + cliente.getNome() + " Veículo:" + veiculo.getMarca() + " " + veiculo.getModelo() + " Data:" + data;
        }
    }

    private static class AdvancedReport {
        final BigDecimal faturamento;
        final Map<Integer, Long> vendasPorVendedor;
        final double mediaVenda;
        final long disponiveis;
        final long vendidos;
        final List<Contrato> contratos;

        AdvancedReport(BigDecimal faturamento, Map<Integer, Long> vendasPorVendedor, double mediaVenda, long disponiveis, long vendidos, List<Contrato> contratos) {
            this.faturamento = faturamento;
            this.vendasPorVendedor = vendasPorVendedor;
            this.mediaVenda = mediaVenda;
            this.disponiveis = disponiveis;
            this.vendidos = vendidos;
            this.contratos = contratos;
        }

        String toTxt() {
            StringBuilder sb = new StringBuilder();
            sb.append("Faturamento: ").append(faturamento).append("\n");
            sb.append("Média por venda: ").append(BigDecimal.valueOf(mediaVenda).setScale(2, RoundingMode.HALF_UP)).append("\n");
            sb.append("Disponíveis: ").append(disponiveis).append("\n");
            sb.append("Vendidos: ").append(vendidos).append("\n");
            sb.append("Vendas por vendedor:\n");
            vendasPorVendedor.forEach((id, qtd) -> {
                Vendedor v = repo.getVendedor(id);
                sb.append(" - ").append(v == null ? ("Vendedor#" + id) : v.getNome()).append(": ").append(qtd).append("\n");
            });
            sb.append("\nContratos:\n");
            contratos.forEach(c -> sb.append(c.toString()).append("\n"));
            return sb.toString();
        }

        String toCsv() {
            StringBuilder sb = new StringBuilder();
            sb.append("Faturamento,").append(faturamento).append("\n");
            sb.append("MediaVenda,").append(BigDecimal.valueOf(mediaVenda).setScale(2, RoundingMode.HALF_UP)).append("\n");
            sb.append("Disponiveis,").append(disponiveis).append("\n");
            sb.append("Vendidos,").append(vendidos).append("\n");
            sb.append("\nVendedorID;VendedorNome;QtdVendas\n");
            vendasPorVendedor.forEach((id, qtd) -> {
                Vendedor v = repo.getVendedor(id);
                sb.append(id).append(";").append(v == null ? ("Vendedor#" + id) : v.getNome()).append(";").append(qtd).append("\n");
            });
            sb.append("\nContratos (id;cliente;vendedor;data;valor)\n");
            contratos.forEach(c -> {
                sb.append(c.getId()).append(";")
                        .append(c.getCliente() == null ? "N/A" : c.getCliente().getNome()).append(";")
                        .append(c.getVendedor() == null ? "N/A" : c.getVendedor().getNome()).append(";")
                        .append(c.getData()).append(";")
                        .append(c.getValorTotal()).append("\n");
            });
            return sb.toString();
        }

        String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"faturamento\": ").append(faturamento).append(",\n");
            sb.append("  \"mediaVenda\": ").append(BigDecimal.valueOf(mediaVenda).setScale(2, RoundingMode.HALF_UP)).append(",\n");
            sb.append("  \"disponiveis\": ").append(disponiveis).append(",\n");
            sb.append("  \"vendidos\": ").append(vendidos).append(",\n");
            sb.append("  \"vendasPorVendedor\": {\n");
            int i = 0;
            for (Map.Entry<Integer, Long> e : vendasPorVendedor.entrySet()) {
                Vendedor v = repo.getVendedor(e.getKey());
                sb.append("    \"").append(e.getKey()).append("\": { \"nome\": \"").append(v == null ? ("Vendedor#" + e.getKey()) : v.getNome()).append("\", \"qtd\": ").append(e.getValue()).append(" }");
                if (++i < vendasPorVendedor.size()) sb.append(",");
                sb.append("\n");
            }
            sb.append("  },\n");
            sb.append("  \"contratos\": [\n");
            for (int j = 0; j < contratos.size(); j++) {
                Contrato c = contratos.get(j);
                sb.append("    { \"id\": ").append(c.getId())
                        .append(", \"cliente\": \"").append(c.getCliente() == null ? "" : c.getCliente().getNome()).append("\"")
                        .append(", \"vendedor\": \"").append(c.getVendedor() == null ? "" : c.getVendedor().getNome()).append("\"")
                        .append(", \"data\": \"").append(c.getData()).append("\"")
                        .append(", \"valor\": ").append(c.getValorTotal()).append(" }");
                if (j + 1 < contratos.size()) sb.append(",\n"); else sb.append("\n");
            }
            sb.append("  ]\n}");
            return sb.toString();
        }
    }
}