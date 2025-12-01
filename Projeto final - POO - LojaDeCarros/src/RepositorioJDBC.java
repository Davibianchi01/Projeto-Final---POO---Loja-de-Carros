import java.math.BigDecimal;
import java.sql.*; // Mantém o import geral
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RepositorioJDBC implements Repositorio {

    @Override
    public void addContrato(Contrato c) {
        String sql = "INSERT INTO contrato (cliente_id, vendedor_id, data_contrato, valor_total, saldo_a_pagar) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, c.getCliente().getId());
            pstmt.setInt(2, c.getVendedor().getId());
            // CORREÇÃO: Usar java.sql.Date.valueOf() explicitamente
            pstmt.setDate(3, java.sql.Date.valueOf(c.getData()));
            pstmt.setBigDecimal(4, c.getValorTotal());
            pstmt.setBigDecimal(5, c.getSaldoAPagar());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar contrato", e);
        }
    }

    @Override
    public List<Contrato> relatorioPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<Contrato> contratos = new ArrayList<>();
        String sql = """
            SELECT c.*, cli.nome as cliente_nome, v.nome as vendedor_nome
            FROM contrato c
            JOIN cliente cli ON c.cliente_id = cli.id
            JOIN vendedor v ON c.vendedor_id = v.id
            WHERE c.data_contrato BETWEEN ? AND ?
            ORDER BY c.data_contrato
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // CORREÇÃO: Usar java.sql.Date.valueOf() explicitamente
            pstmt.setDate(1, java.sql.Date.valueOf(inicio));
            pstmt.setDate(2, java.sql.Date.valueOf(fim));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Código continua igual...
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar relatório por período", e);
        }
        return contratos;
    }

    @Override
    public List<Contrato> getContratos() {
        List<Contrato> contratos = new ArrayList<>();
        String sql = """
            SELECT c.*, cli.nome as cliente_nome, cli.cpf as cliente_cpf, 
                   cli.telefone as cliente_tel, cli.necessidades as cliente_nec,
                   v.nome as vendedor_nome, v.cpf as vendedor_cpf
            FROM contrato c
            JOIN cliente cli ON c.cliente_id = cli.id
            JOIN vendedor v ON c.vendedor_id = v.id
            ORDER BY c.data_contrato DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // ... código do cliente e vendedor ...

                // CORREÇÃO: Converter java.sql.Date para LocalDate
                LocalDate dataContrato = rs.getDate("data_contrato").toLocalDate();

                // ... resto do código ...
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contratos", e);
        }
        return contratos;
    }

    // ... resto do código permanece igual ...
}