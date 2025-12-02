import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainAWT extends JFrame {
    private final RepositorioMemoria repo = new RepositorioMemoria();
    private final VendaService vendaService = new VendaService(repo);

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public MainAWT() {
        setTitle("🚗 Sistema de Venda de Carros");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Menu principal
        JMenuBar menuBar = new JMenuBar();

        JMenu menuCadastro = new JMenu("Cadastro");
        JMenuItem menuCliente = new JMenuItem("Cliente");
        JMenuItem menuVendedor = new JMenuItem("Vendedor");
        JMenuItem menuVeiculo = new JMenuItem("Veículo");
        menuCadastro.add(menuCliente);
        menuCadastro.add(menuVendedor);
        menuCadastro.add(menuVeiculo);

        JMenu menuConsulta = new JMenu("Consulta");
        JMenuItem menuListClientes = new JMenuItem("Clientes");
        JMenuItem menuListVendedores = new JMenuItem("Vendedores");
        JMenuItem menuListVeiculos = new JMenuItem("Veículos");
        menuConsulta.add(menuListClientes);
        menuConsulta.add(menuListVendedores);
        menuConsulta.add(menuListVeiculos);

        JMenu menuVenda = new JMenu("Vendas");
        JMenuItem menuNovaProposta = new JMenuItem("Nova Proposta");
        JMenuItem menuFormalizar = new JMenuItem("Formalizar Venda");
        menuVenda.add(menuNovaProposta);
        menuVenda.add(menuFormalizar);

        JMenu menuTestDrive = new JMenu("Test-Drive");
        JMenuItem menuAgendarTestDrive = new JMenuItem("Agendar");

        JMenu menuRelatorio = new JMenu("Relatórios");
        JMenuItem menuRelatorioAvancado = new JMenuItem("Avançado");

        menuBar.add(menuCadastro);
        menuBar.add(menuConsulta);
        menuBar.add(menuVenda);
        menuBar.add(menuTestDrive);
        menuBar.add(menuRelatorio);

        setJMenuBar(menuBar);

        // Tabela principal
        modeloTabela = new DefaultTableModel();
        tabela = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // Ações
        menuCliente.addActionListener(e -> cadastrarClienteGUI());
        menuVendedor.addActionListener(e -> cadastrarVendedorGUI());
        menuVeiculo.addActionListener(e -> cadastrarVeiculoGUI());

        menuListClientes.addActionListener(e -> listarClientes());
        menuListVendedores.addActionListener(e -> listarVendedores());
        menuListVeiculos.addActionListener(e -> listarVeiculos());

        menuNovaProposta.addActionListener(e -> criarPropostaGUI());
        menuFormalizar.addActionListener(e -> formalizarPropostaGUI());

        menuAgendarTestDrive.addActionListener(e -> agendarTestDriveGUI());

        menuRelatorioAvancado.addActionListener(e -> gerarRelatorioGUI());
    }

    // ---------- Cadastro ----------
    private void cadastrarClienteGUI() {
        JTextField nome = new JTextField();
        JTextField cpf = new JTextField();
        JTextField telefone = new JTextField();
        JTextField necessidades = new JTextField();

        Object[] fields = {
                "Nome:", nome,
                "CPF (11 números):", cpf,
                "Telefone (apenas números):", telefone,
                "Necessidades:", necessidades
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Cadastrar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nomeStr = nome.getText().trim();
            String cpfStr = cpf.getText().trim();
            String telStr = telefone.getText().trim();
            String necStr = necessidades.getText().trim();

            if (!nomeStr.matches("[a-zA-ZÀ-ÿ ]+")) {
                JOptionPane.showMessageDialog(this, "Nome inválido! Apenas letras e espaços.");
                return;
            }

            if (!cpfStr.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(this, "CPF inválido! Deve conter 11 números.");
                return;
            }

            if (!telStr.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Telefone inválido! Apenas números.");
                return;
            }

            if (necStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Necessidades não podem ser vazias.");
                return;
            }

            repo.addCliente(nomeStr, cpfStr, telStr, necStr);
            JOptionPane.showMessageDialog(this, "Cliente cadastrado!");
        }
    }

    private void cadastrarVendedorGUI() {
        JTextField nome = new JTextField();
        JTextField cpf = new JTextField();

        Object[] fields = {"Nome:", nome, "CPF (11 números):", cpf};
        int option = JOptionPane.showConfirmDialog(this, fields, "Cadastrar Vendedor", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nomeStr = nome.getText().trim();
            String cpfStr = cpf.getText().trim();

            if (!nomeStr.matches("[a-zA-ZÀ-ÿ ]+")) {
                JOptionPane.showMessageDialog(this, "Nome inválido! Apenas letras e espaços.");
                return;
            }

            if (!cpfStr.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(this, "CPF inválido! Deve conter 11 números.");
                return;
            }

            repo.addVendedor(nomeStr, cpfStr);
            JOptionPane.showMessageDialog(this, "Vendedor cadastrado!");
        }
    }

    private void cadastrarVeiculoGUI() {
        JTextField marca = new JTextField();
        JTextField modelo = new JTextField();
        JTextField ano = new JTextField();
        JTextField preco = new JTextField();

        Object[] fields = {
                "Marca:", marca,
                "Modelo:", modelo,
                "Ano:", ano,
                "Preço:", preco
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Cadastrar Veículo", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String marcaStr = marca.getText().trim();
            String modeloStr = modelo.getText().trim();
            String anoStr = ano.getText().trim();
            String precoStr = preco.getText().trim();

            if (!marcaStr.matches("[a-zA-Z0-9 ]+")) {
                JOptionPane.showMessageDialog(this, "Marca inválida! Apenas letras, números e espaços.");
                return;
            }

            if (!modeloStr.matches("[a-zA-Z0-9 ]+")) {
                JOptionPane.showMessageDialog(this, "Modelo inválido! Apenas letras, números e espaços.");
                return;
            }

            int anoInt;
            try {
                anoInt = Integer.parseInt(anoStr);
                if (anoInt <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ano inválido! Deve ser um número positivo.");
                return;
            }

            BigDecimal precoBD;
            try {
                precoBD = new BigDecimal(precoStr);
                if (precoBD.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Preço inválido! Deve ser um número positivo.");
                return;
            }

            Veiculo v = new Veiculo(marcaStr, modeloStr, anoInt, precoBD);
            repo.addVeiculo(v);
            JOptionPane.showMessageDialog(this, "Veículo cadastrado!");
        }
    }

    // ---------- Listagem ----------
    private void listarClientes() {
        modeloTabela.setRowCount(0);
        modeloTabela.setColumnCount(0);
        modeloTabela.setColumnIdentifiers(new String[]{"ID", "Nome", "CPF", "Telefone", "Necessidades"});
        for (Cliente c : repo.getAllClientes()) {
            modeloTabela.addRow(new Object[]{c.getId(), c.getNome(), c.getCPF(), c.getTelefone(), c.getNecessidades()});
        }
    }

    private void listarVendedores() {
        modeloTabela.setRowCount(0);
        modeloTabela.setColumnCount(0);
        modeloTabela.setColumnIdentifiers(new String[]{"ID", "Nome", "CPF"});
        for (Vendedor v : repo.getAllVendedores()) {
            modeloTabela.addRow(new Object[]{v.getId(), v.getNome(), v.getCPF()});
        }
    }

    private void listarVeiculos() {
        modeloTabela.setRowCount(0);
        modeloTabela.setColumnCount(0);
        modeloTabela.setColumnIdentifiers(new String[]{"ID", "Marca", "Modelo", "Ano", "Preço", "Status"});
        for (Veiculo v : repo.getAllVeiculos()) {
            modeloTabela.addRow(new Object[]{v.getId(), v.getMarca(), v.getModelo(), v.getAno(), v.getPreco(), v.getStatus()});
        }
    }

    // ---------- Propostas ----------
    private void criarPropostaGUI() {
        List<Cliente> clientes = new ArrayList<>(repo.getAllClientes());
        List<Vendedor> vendedores = new ArrayList<>(repo.getAllVendedores());
        List<Veiculo> veiculos = new ArrayList<>(repo.getAllVeiculos().stream()
                .filter(v -> "disponivel".equalsIgnoreCase(v.getStatus()))
                .toList());

        if (clientes.isEmpty() || vendedores.isEmpty() || veiculos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "É necessário ter clientes, vendedores e veículos disponíveis.");
            return;
        }

        String[] clientesStr = clientes.stream().map(c -> c.getId() + " - " + c.getNome()).toArray(String[]::new);
        String[] vendedoresStr = vendedores.stream().map(v -> v.getId() + " - " + v.getNome()).toArray(String[]::new);
        String[] veiculosStr = veiculos.stream().map(v -> v.getId() + " - " + v.getMarca() + " " + v.getModelo()).toArray(String[]::new);

        JComboBox<String> comboCliente = new JComboBox<>(clientesStr);
        JComboBox<String> comboVendedor = new JComboBox<>(vendedoresStr);
        JComboBox<String> comboVeiculo = new JComboBox<>(veiculosStr);

        JTextField entrada = new JTextField("0");
        JTextField parcelas = new JTextField("12");
        JTextField juros = new JTextField("0");

        Object[] fields = {
                "Cliente:", comboCliente,
                "Vendedor:", comboVendedor,
                "Veículo:", comboVeiculo,
                "Entrada:", entrada,
                "Parcelas:", parcelas,
                "Juros anual %:", juros
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Criar Proposta", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Cliente c = clientes.get(comboCliente.getSelectedIndex());
            Vendedor v = vendedores.get(comboVendedor.getSelectedIndex());
            Veiculo ve = veiculos.get(comboVeiculo.getSelectedIndex());

            BigDecimal valEntrada;
            int nParcelas;
            BigDecimal jurosAnual;

            try {
                valEntrada = new BigDecimal(entrada.getText());
                if (valEntrada.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Entrada inválida!");
                return;
            }

            try {
                nParcelas = Integer.parseInt(parcelas.getText());
                if (nParcelas <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Número de parcelas inválido!");
                return;
            }

            try {
                jurosAnual = new BigDecimal(juros.getText());
                if (jurosAnual.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Juros inválido!");
                return;
            }

            Main.Proposta p = new Main.Proposta(1, c, v, List.of(ve), ve.getPreco(), valEntrada, nParcelas, jurosAnual);
            JOptionPane.showMessageDialog(this, "Proposta criada!\n" + p);
        }
    }

    private void formalizarPropostaGUI() {
        JOptionPane.showMessageDialog(this, "Funcionalidade de formalização de proposta será adaptada do Main.Proposta -> Contrato.");
    }

    // ---------- Test-Drive ----------
    private void agendarTestDriveGUI() {
        JOptionPane.showMessageDialog(this, "Funcionalidade de agendamento de test-drive será adaptada do Main.TestDrive.");
    }

    // ---------- Relatório ----------
    private void gerarRelatorioGUI() {
        JOptionPane.showMessageDialog(this, "Funcionalidade de relatório avançado será adaptada do Main.AdvancedReport.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainAWT gui = new MainAWT();
            gui.setVisible(true);
        });
    }
}
