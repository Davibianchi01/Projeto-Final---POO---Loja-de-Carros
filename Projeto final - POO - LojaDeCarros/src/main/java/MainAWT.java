package main.java;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class MainAWT extends JFrame {
    private final RepositorioJDBC repo;
    private final VendaService vendaService;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private final Map<Integer, Proposta> propostasPendentes = new HashMap<>();
    private int propostaSeq = 1;
    private final Map<Integer, TestDrive> agendaTestDrive = new HashMap<>();
    private int testDriveSeq = 1;

    public MainAWT() {
        try {
            repo = new RepositorioJDBC();
            repo.initializeDatabase();
            repo.seedData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao conectar com o banco de dados: " + e.getMessage(),
                    "Erro de Conexão",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Falha na inicialização do banco de dados", e);
        }

        vendaService = new VendaService(repo);

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
        JMenuItem menuListPropostas = new JMenuItem("Propostas Pendentes");
        JMenuItem menuListTestDrives = new JMenuItem("Test-Drives Agendados");
        menuConsulta.add(menuListClientes);
        menuConsulta.add(menuListVendedores);
        menuConsulta.add(menuListVeiculos);
        menuConsulta.add(menuListPropostas);
        menuConsulta.add(menuListTestDrives);

        JMenu menuVenda = new JMenu("Vendas");
        JMenuItem menuNovaProposta = new JMenuItem("Nova Proposta");
        JMenuItem menuFormalizar = new JMenuItem("Formalizar Venda");
        menuVenda.add(menuNovaProposta);
        menuVenda.add(menuFormalizar);

        JMenu menuTestDrive = new JMenu("Test-Drive");
        JMenuItem menuAgendarTestDrive = new JMenuItem("Agendar");
        menuTestDrive.add(menuAgendarTestDrive);

        JMenu menuRelatorio = new JMenu("Relatórios");
        JMenuItem menuRelatorioAvancado = new JMenuItem("Avançado");
        menuRelatorio.add(menuRelatorioAvancado);

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
        menuListPropostas.addActionListener(e -> listarPropostasPendentes());
        menuListTestDrives.addActionListener(e -> listarTestDrives());

        menuNovaProposta.addActionListener(e -> criarPropostaGUI());
        menuFormalizar.addActionListener(e -> formalizarPropostaGUI());

        menuAgendarTestDrive.addActionListener(e -> agendarTestDriveGUI());

        menuRelatorioAvancado.addActionListener(e -> gerarRelatorioGUI());
    }

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

            try {
                repo.addCliente(nomeStr, cpfStr, telStr, necStr);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao cadastrar cliente: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
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

            try {
                repo.addVendedor(nomeStr, cpfStr);
                JOptionPane.showMessageDialog(this, "Vendedor cadastrado!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao cadastrar vendedor: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
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

            if (marcaStr == null || marcaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Marca não pode ser vazia!");
                return;
            }

            if (modeloStr == null || modeloStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Modelo não pode ser vazio!");
                return;
            }

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
            try {
                repo.addVeiculo(v);
                JOptionPane.showMessageDialog(this, "Veículo cadastrado!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao cadastrar veículo: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarClientes() {
        try {
            modeloTabela.setRowCount(0);
            modeloTabela.setColumnCount(0);
            modeloTabela.setColumnIdentifiers(new String[]{"ID", "Nome", "CPF", "Telefone", "Necessidades"});
            for (Cliente c : repo.getAllClientes()) {
                modeloTabela.addRow(new Object[]{c.getId(), c.getNome(), c.getCPF(), c.getTelefone(), c.getNecessidades()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao listar clientes: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarVendedores() {
        try {
            modeloTabela.setRowCount(0);
            modeloTabela.setColumnCount(0);
            modeloTabela.setColumnIdentifiers(new String[]{"ID", "Nome", "CPF"});
            for (Vendedor v : repo.getAllVendedores()) {
                modeloTabela.addRow(new Object[]{v.getId(), v.getNome(), v.getCPF()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao listar vendedores: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarVeiculos() {
        try {
            modeloTabela.setRowCount(0);
            modeloTabela.setColumnCount(0);
            modeloTabela.setColumnIdentifiers(new String[]{"ID", "Marca", "Modelo", "Ano", "Preço", "Status"});
            for (Veiculo v : repo.getAllVeiculos()) {
                modeloTabela.addRow(new Object[]{v.getId(), v.getMarca(), v.getModelo(), v.getAno(), v.getPreco(), v.getStatus()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao listar veículos: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarPropostaGUI() {
        try {
            java.util.List<Cliente> clientes = new ArrayList<>(repo.getAllClientes());
            java.util.List<Vendedor> vendedores = new ArrayList<>(repo.getAllVendedores());
            java.util.List<Veiculo> veiculos = new ArrayList<>(repo.getAllVeiculos().stream()
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

                Proposta p = new Proposta(propostaSeq++, c, v,
                        java.util.Collections.singletonList(ve),
                        ve.getPreco(), valEntrada, nParcelas, jurosAnual);
                propostasPendentes.put(p.getId(), p);

                JOptionPane.showMessageDialog(this,
                        "Proposta criada com sucesso!\n" +
                                "ID: " + p.getId() + "\n" +
                                "Cliente: " + c.getNome() + "\n" +
                                "Vendedor: " + v.getNome() + "\n" +
                                "Veículo: " + ve.getMarca() + " " + ve.getModelo() + "\n" +
                                "Valor: R$ " + ve.getPreco() + "\n" +
                                "Entrada: R$ " + valEntrada + "\n" +
                                "Parcelas: " + nParcelas + "\n" +
                                "Juros: " + jurosAnual + "%");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao criar proposta: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarPropostasPendentes() {
        if (propostasPendentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há propostas pendentes.");
            return;
        }

        modeloTabela.setRowCount(0);
        modeloTabela.setColumnCount(0);
        modeloTabela.setColumnIdentifiers(new String[]{"ID", "Cliente", "Vendedor", "Veículo", "Valor", "Entrada", "Parcelas", "Juros"});

        for (Proposta p : propostasPendentes.values()) {
            String veiculoInfo = "";
            if (!p.getCarrosSelecionados().isEmpty()) {
                Veiculo v = p.getCarrosSelecionados().get(0);
                veiculoInfo = v.getMarca() + " " + v.getModelo();
            }

            modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getCliente().getNome(),
                    p.getVendedor().getNome(),
                    veiculoInfo,
                    "R$ " + p.getValorTotal(),
                    "R$ " + p.getEntrada(),
                    p.getParcelas(),
                    p.getJurosAnualPercent() + "%"
            });
        }
    }

    private void formalizarPropostaGUI() {
        if (propostasPendentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há propostas pendentes para formalizar.");
            return;
        }

        listarPropostasPendentes();

        String[] propostaIds = propostasPendentes.keySet().stream()
                .map(id -> {
                    Proposta p = propostasPendentes.get(id);
                    String veiculoInfo = p.getCarrosSelecionados().isEmpty() ?
                            "Nenhum veículo" :
                            p.getCarrosSelecionados().get(0).getMarca() + " " +
                                    p.getCarrosSelecionados().get(0).getModelo();
                    return id + " - Cliente: " + p.getCliente().getNome() + " - Veículo: " + veiculoInfo;
                })
                .toArray(String[]::new);

        if (propostaIds.length == 0) {
            JOptionPane.showMessageDialog(this, "Nenhuma proposta disponível.");
            return;
        }

        JComboBox<String> comboPropostas = new JComboBox<>(propostaIds);
        JTextField dataField = new JTextField(LocalDate.now().toString());

        JButton btnDetalhes = new JButton("Ver Detalhes da Proposta Selecionada");
        btnDetalhes.addActionListener(e -> {
            String selected = (String) comboPropostas.getSelectedItem();
            if (selected != null) {
                try {
                    int propostaId = Integer.parseInt(selected.split(" - ")[0]);
                    Proposta proposta = propostasPendentes.get(propostaId);
                    if (proposta != null) {
                        StringBuilder detalhes = new StringBuilder();
                        detalhes.append("=== DETALHES DA PROPOSTA ===\n\n");
                        detalhes.append("ID: ").append(proposta.getId()).append("\n");
                        detalhes.append("Cliente: ").append(proposta.getCliente().getNome()).append("\n");
                        detalhes.append("Vendedor: ").append(proposta.getVendedor().getNome()).append("\n");

                        if (!proposta.getCarrosSelecionados().isEmpty()) {
                            Veiculo v = proposta.getCarrosSelecionados().get(0);
                            detalhes.append("Veículo: ").append(v.getMarca()).append(" ").append(v.getModelo()).append("\n");
                            detalhes.append("Ano: ").append(v.getAno()).append("\n");
                            detalhes.append("Preço: R$ ").append(v.getPreco()).append("\n");
                            detalhes.append("Status: ").append(v.getStatus()).append("\n");
                        }

                        detalhes.append("\n=== TERMOS DA PROPOSTA ===\n");
                        detalhes.append("Valor Total: R$ ").append(proposta.getValorTotal()).append("\n");
                        detalhes.append("Entrada: R$ ").append(proposta.getEntrada()).append("\n");
                        detalhes.append("Parcelas: ").append(proposta.getParcelas()).append("\n");
                        detalhes.append("Juros Anual: ").append(proposta.getJurosAnualPercent()).append("%\n");

                        BigDecimal saldo = proposta.getValorTotal().subtract(proposta.getEntrada());
                        detalhes.append("Saldo a Financiar: R$ ").append(saldo).append("\n");

                        JTextArea textArea = new JTextArea(detalhes.toString(), 15, 50);
                        textArea.setEditable(false);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);

                        JOptionPane.showMessageDialog(MainAWT.this,
                                new JScrollPane(textArea),
                                "Detalhes da Proposta #" + propostaId,
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainAWT.this,
                            "Erro ao exibir detalhes: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        Object[] fields = {
                "Selecione a proposta para formalizar:", comboPropostas,
                "", btnDetalhes,
                "Data da formalização (YYYY-MM-DD):", dataField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Formalizar Proposta", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String selected = (String) comboPropostas.getSelectedItem();
                int propostaId = Integer.parseInt(selected.split(" - ")[0]);

                Proposta p = propostasPendentes.get(propostaId);

                if (p == null) {
                    JOptionPane.showMessageDialog(this, "Proposta não encontrada.");
                    return;
                }

                if (p.getCarrosSelecionados().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Proposta não tem veículos selecionados.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Veiculo veiculoProposta = p.getCarrosSelecionados().get(0);

                Veiculo veiculoAtualizado = repo.getVeiculo(veiculoProposta.getId());
                if (veiculoAtualizado == null) {
                    JOptionPane.showMessageDialog(this,
                            "Veículo não encontrado no banco de dados.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!"disponivel".equalsIgnoreCase(veiculoAtualizado.getStatus())) {
                    JOptionPane.showMessageDialog(this,
                            "Veículo não está disponível para venda.\n" +
                                    "Status atual: " + veiculoAtualizado.getStatus(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    propostasPendentes.remove(propostaId);
                    return;
                }

                // Parse da data
                LocalDate dataVenda;
                try {
                    dataVenda = LocalDate.parse(dataField.getText().trim());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Data inválida! Usando data atual.");
                    dataVenda = LocalDate.now();
                }

                Contrato contrato = criarContratoAPartirDaProposta(p, dataVenda, veiculoAtualizado);

                // Debug: Verificar contrato criado
                System.out.println("Contrato criado com ID: " + contrato.getId());
                System.out.println("Valor Total: " + contrato.getValorTotal());
                System.out.println("Saldo a Pagar: " + contrato.getSaldoAPagar());

                // Formalizar (mudar status do veículo)
                boolean formalizado = contrato.formalizar();
                if (!formalizado) {
                    JOptionPane.showMessageDialog(this,
                            "Não foi possível formalizar a venda.\n" +
                                    "O método formalizar() retornou false.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Salvar no banco de dados
                repo.addContrato(contrato);

                // Atualizar status do veículo no banco
                veiculoAtualizado.setStatus("vendido");
                repo.updateVeiculo(veiculoAtualizado);

                // Remover proposta pendente
                propostasPendentes.remove(propostaId);

                // Mostrar contrato gerado
                StringBuilder contratoInfo = new StringBuilder();
                contratoInfo.append("=== VENDA FORMALIZADA COM SUCESSO ===\n\n");
                contratoInfo.append(contrato.toString()).append("\n");
                contratoInfo.append("\n=== DETALHES ADICIONAIS ===\n");
                contratoInfo.append("Proposta ID: ").append(propostaId).append("\n");
                contratoInfo.append("Data da Formalização: ").append(dataVenda).append("\n");
                contratoInfo.append("Veículo atualizado para: VENDIDO\n");

                JTextArea textArea = new JTextArea(contratoInfo.toString(), 20, 60);
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);

                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this,
                        scrollPane,
                        "Venda Formalizada - Contrato #" + contrato.getId(),
                        JOptionPane.INFORMATION_MESSAGE);

                // Atualizar lista de veículos na interface
                listarVeiculos();

                // Mostrar mensagem de sucesso
                JOptionPane.showMessageDialog(this,
                        "Venda formalizada com sucesso!\n" +
                                "Contrato #" + contrato.getId() + " foi criado.\n" +
                                "O veículo foi marcado como VENDIDO.",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Erro ao formalizar proposta:\n\n" +
                                "Mensagem: " + e.getMessage() + "\n" +
                                "Tipo: " + e.getClass().getName() + "\n\n" +
                                "Por favor, verifique os dados da proposta e tente novamente.",
                        "Erro na Formalização",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private Contrato criarContratoAPartirDaProposta(Proposta p, LocalDate dataVenda, Veiculo veiculo) {
        try {

            return new Contrato(p.getCliente(), p.getVendedor(),
                    dataVenda,
                    p.getValorTotal(),
                    p.getEntrada(),
                    new ArrayList<>(p.getCarrosSelecionados()));
        } catch (Exception e) {
            return new Contrato(p.getCliente(), p.getVendedor(),
                    veiculo,
                    dataVenda,
                    p.getValorTotal());
        }
    }

    private void agendarTestDriveGUI() {
        try {
            java.util.List<Cliente> clientes = new ArrayList<>(repo.getAllClientes());
            java.util.List<Veiculo> veiculos = new ArrayList<>(repo.getAllVeiculos().stream()
                    .filter(v -> "disponivel".equalsIgnoreCase(v.getStatus()))
                    .toList());

            if (clientes.isEmpty() || veiculos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "É necessário ter clientes e veículos disponíveis.");
                return;
            }

            String[] clientesStr = clientes.stream().map(c -> c.getId() + " - " + c.getNome()).toArray(String[]::new);
            String[] veiculosStr = veiculos.stream().map(v -> v.getId() + " - " + v.getMarca() + " " + v.getModelo()).toArray(String[]::new);

            JComboBox<String> comboCliente = new JComboBox<>(clientesStr);
            JComboBox<String> comboVeiculo = new JComboBox<>(veiculosStr);
            JTextField dataField = new JTextField(LocalDate.now().plusDays(1).toString());

            Object[] fields = {
                    "Cliente:", comboCliente,
                    "Veículo:", comboVeiculo,
                    "Data (YYYY-MM-DD):", dataField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Agendar Test-Drive", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                Cliente cliente = clientes.get(comboCliente.getSelectedIndex());
                Veiculo veiculo = veiculos.get(comboVeiculo.getSelectedIndex());

                LocalDate data;
                try {
                    data = LocalDate.parse(dataField.getText().trim());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Data inválida! Usando amanhã.");
                    data = LocalDate.now().plusDays(1);
                }

                TestDrive td = new TestDrive(testDriveSeq++, cliente, veiculo, data);
                agendaTestDrive.put(td.getId(), td);

                JOptionPane.showMessageDialog(this,
                        "Test-Drive agendado com sucesso!\n" +
                                "ID: " + td.getId() + "\n" +
                                "Cliente: " + cliente.getNome() + "\n" +
                                "Veículo: " + veiculo.getMarca() + " " + veiculo.getModelo() + "\n" +
                                "Data: " + data,
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao agendar test-drive: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void listarTestDrives() {
        if (agendaTestDrive.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há test-drives agendados.");
            return;
        }

        modeloTabela.setRowCount(0);
        modeloTabela.setColumnCount(0);
        modeloTabela.setColumnIdentifiers(new String[]{"ID", "Cliente", "Veículo", "Data"});

        for (TestDrive td : agendaTestDrive.values()) {
            modeloTabela.addRow(new Object[]{
                    td.getId(),
                    td.getCliente().getNome(),
                    td.getVeiculo().getMarca() + " " + td.getVeiculo().getModelo(),
                    td.getData().toString()
            });
        }
    }


    private void gerarRelatorioGUI() {
        try {
            java.util.List<Contrato> contratos = repo.getContratos();
            java.util.List<Veiculo> todosVeiculos = new ArrayList<>(repo.getAllVeiculos());


            BigDecimal faturamentoTotal = contratos.stream()
                    .map(Contrato::getValorTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long veiculosDisponiveis = todosVeiculos.stream()
                    .filter(v -> "disponivel".equalsIgnoreCase(v.getStatus()))
                    .count();

            long veiculosVendidos = todosVeiculos.stream()
                    .filter(v -> "vendido".equalsIgnoreCase(v.getStatus()))
                    .count();


            StringBuilder relatorio = new StringBuilder();
            relatorio.append("=== RELATÓRIO AVANÇADO ===\n\n");
            relatorio.append("Faturamento Total: R$ ").append(faturamentoTotal.setScale(2)).append("\n");
            relatorio.append("Total de Contratos: ").append(contratos.size()).append("\n");
            relatorio.append("Veículos Disponíveis: ").append(veiculosDisponiveis).append("\n");
            relatorio.append("Veículos Vendidos: ").append(veiculosVendidos).append("\n");
            relatorio.append("\n=== ÚLTIMOS CONTRATOS ===\n");


            contratos.stream()
                    .limit(5)
                    .forEach(c -> relatorio.append(c.toString()).append("\n"));

            JTextArea textArea = new JTextArea(20, 50);
            textArea.setText(relatorio.toString());
            textArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(this, scrollPane, "Relatório Avançado", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar relatório: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    class Proposta {
        private final int id;
        private final Cliente cliente;
        private final Vendedor vendedor;
        private final java.util.List<Veiculo> carrosSelecionados;
        private final BigDecimal valorTotal;
        private final BigDecimal entrada;
        private final int parcelas;
        private final BigDecimal jurosAnualPercent;

        public Proposta(int id, Cliente cliente, Vendedor vendedor, java.util.List<Veiculo> carrosSelecionados,
                        BigDecimal valorTotal, BigDecimal entrada, int parcelas, BigDecimal jurosAnualPercent) {
            this.id = id;
            this.cliente = cliente;
            this.vendedor = vendedor;
            this.carrosSelecionados = carrosSelecionados != null ? carrosSelecionados : new ArrayList<>();
            this.valorTotal = valorTotal;
            this.entrada = entrada;
            this.parcelas = parcelas;
            this.jurosAnualPercent = jurosAnualPercent;
        }

        public int getId() { return id; }
        public Cliente getCliente() { return cliente; }
        public Vendedor getVendedor() { return vendedor; }
        public java.util.List<Veiculo> getCarrosSelecionados() { return carrosSelecionados; }
        public BigDecimal getValorTotal() { return valorTotal; }
        public BigDecimal getEntrada() { return entrada; }
        public int getParcelas() { return parcelas; }
        public BigDecimal getJurosAnualPercent() { return jurosAnualPercent; }

        @Override
        public String toString() {
            String carroInfo = carrosSelecionados.isEmpty() ?
                    "Nenhum veículo" :
                    carrosSelecionados.get(0).getMarca() + " " + carrosSelecionados.get(0).getModelo();

            return "Proposta #" + id +
                    " - Cliente: " + cliente.getNome() +
                    " - Veículo: " + carroInfo +
                    " - Valor: R$ " + valorTotal;
        }
    }

    class TestDrive {
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
            return "TestDrive #" + id +
                    " - Cliente: " + cliente.getNome() +
                    " - Veículo: " + veiculo.getMarca() + " " + veiculo.getModelo() +
                    " - Data: " + data;
        }
    }

    public static void main(String[] args) {
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "❌ Não foi possível conectar ao MySQL!\n\n" +
                            "Por favor, verifique se:\n" +
                            "1. O MySQL está instalado e rodando\n" +
                            "2. O serviço MySQL está ativo\n" +
                            "3. As credenciais estão corretas\n\n" +
                            "Para instalar rapidamente:\n" +
                            "- Baixe o XAMPP em: https://www.apachefriends.org\n" +
                            "- Instale e inicie o MySQL no XAMPP",
                    "Erro de Conexão",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                MainAWT gui = new MainAWT();
                gui.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao iniciar aplicação: " + e.getMessage(),
                        "Erro de Inicialização",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}