package io.literato.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Database {
    
    private static final String DB_URL = "jdbc:sqlite:literato.db";

    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA busy_timeout = 5000;");
        }
        return conn;
    }

    public static void initialize() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("PRAGMA journal_mode=WAL;");
            stmt.execute("PRAGMA synchronous=NORMAL;");
            
            String sqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL UNIQUE,
                senha TEXT NOT NULL,
                pontos INTEGER DEFAULT 0,
                streak INTEGER DEFAULT 0,
                ultima_leitura TEXT,
                bio TEXT,
                foto TEXT
            );
            """;
            stmt.execute(sqlUsuarios);

            String sqlLeituras = """
            CREATE TABLE IF NOT EXISTS leituras (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER,
                livro_titulo TEXT,
                livro_autor TEXT,
                livro_capa TEXT,
                paginas_lidas INTEGER,
                data_leitura TEXT,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
            );
            """;
            stmt.execute(sqlLeituras);

            String sqlClubes = """
            CREATE TABLE IF NOT EXISTS clubes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                descricao TEXT,
                foto TEXT,
                dono_id INTEGER,
                publico BOOLEAN DEFAULT 1,
                FOREIGN KEY(dono_id) REFERENCES usuarios(id)
            );
            """;
            stmt.execute(sqlClubes);

            String sqlMembros = """
            CREATE TABLE IF NOT EXISTS membros_clube (
                clube_id INTEGER,
                usuario_id INTEGER,
                cargo TEXT DEFAULT 'MEMBRO',
                PRIMARY KEY (clube_id, usuario_id),
                FOREIGN KEY(clube_id) REFERENCES clubes(id),
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
            );
            """;
            stmt.execute(sqlMembros);

            String sqlLivros = """
            CREATE TABLE IF NOT EXISTS livros (
                id INTEGER PRIMARY KEY,
                titulo TEXT,
                autor TEXT,
                nota_media REAL,
                isbn TEXT,
                paginas INTEGER
            );
            """;
            stmt.execute(sqlLivros);

            String sqlEstante = """
            CREATE TABLE IF NOT EXISTS estante (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER,
                livro_id INTEGER,
                titulo TEXT,
                autor TEXT,
                paginas_total INTEGER,
                paginas_lidas INTEGER DEFAULT 0,
                status TEXT DEFAULT 'LENDO',
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id),
                FOREIGN KEY(livro_id) REFERENCES livros(id)
            );
            """;
            stmt.execute(sqlEstante);
            
            String sqlSolicitacoes = """
            CREATE TABLE IF NOT EXISTS solicitacoes_clube (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                clube_id INTEGER,
                usuario_id INTEGER,
                status TEXT DEFAULT 'PENDENTE', -- PENDENTE, ACEITO, REJEITADO
                data_solicitacao TEXT,
                FOREIGN KEY(clube_id) REFERENCES clubes(id),
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
            );
            """;
            stmt.execute(sqlSolicitacoes);

            try {
                System.out.println("Tentando adicionar coluna 'publico' na tabela 'clubes'...");
                stmt.execute("ALTER TABLE clubes ADD COLUMN publico BOOLEAN DEFAULT 1");
                System.out.println("Coluna 'publico' adicionada com sucesso!");
            } catch (SQLException e) {
                if (e.getMessage().contains("duplicate column name")) {
                    System.out.println("Coluna 'publico' já existe.");
                } else {
                    System.err.println("Erro ao adicionar coluna 'publico': " + e.getMessage());
                    e.printStackTrace();
                }
            }

            try {
                System.out.println("Tentando adicionar coluna 'cargo' na tabela 'membros_clube'...");
                stmt.execute("ALTER TABLE membros_clube ADD COLUMN cargo TEXT DEFAULT 'MEMBRO'");
                System.out.println("Coluna 'cargo' adicionada com sucesso!");
            } catch (SQLException e) {
                if (e.getMessage().contains("duplicate column name")) {
                    System.out.println("Coluna 'cargo' já existe.");
                } else {
                    System.err.println("Erro ao adicionar coluna 'cargo': " + e.getMessage());
                    e.printStackTrace();
                }
            }

            try {
                System.out.println("Tentando adicionar coluna 'capa' na tabela 'clubes'...");
                stmt.execute("ALTER TABLE clubes ADD COLUMN capa TEXT");
                System.out.println("Coluna 'capa' adicionada com sucesso!");
            } catch (SQLException e) {
                if (e.getMessage().contains("duplicate column name")) {
                    System.out.println("Coluna 'capa' já existe.");
                } else {
                    System.err.println("Erro ao adicionar coluna 'capa': " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Popula com dados falsos se estiver vazio
            seed(stmt);
            
            // Importa CSV se a tabela de livros estiver vazia
            importarLivrosCsv(conn);

            System.out.println("Banco de dados V2 inicializado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco: " + e.getMessage());
        }
    }

    private static void importarLivrosCsv(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM livros");
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Já tem livros importados
            }

            System.out.println("Importando livros do CSV...");
            
            java.io.File csvFile = new java.io.File("books.csv");
            if (!csvFile.exists()) {
                System.out.println("Arquivo books.csv não encontrado. Pulei a importação.");
                return;
            }

            String sql = "INSERT INTO livros (id, titulo, autor, nota_media, isbn, paginas) VALUES (?, ?, ?, ?, ?, ?)";
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
                 java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(csvFile))) {
                
                String line;
                br.readLine(); // Pula cabeçalho
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    
                    if (parts.length >= 8) 
                    {
                        try {
                            pstmt.setInt(1, Integer.parseInt(parts[0])); // bookID
                            pstmt.setString(2, parts[1].replace("\"", "")); // title
                            pstmt.setString(3, parts[2].replace("\"", "")); // authors
                            pstmt.setDouble(4, Double.parseDouble(parts[3])); // average_rating
                            pstmt.setString(5, parts[4]); // isbn
                            pstmt.setInt(6, Integer.parseInt(parts[7])); // num_pages
                            pstmt.addBatch();
                        } catch (NumberFormatException e) {
                            System.err.println("Erro ao importar linha: " + line);
                        }
                    }
                }
                pstmt.executeBatch();
                System.out.println("Livros importados com sucesso!");
            }

        } catch (Exception e) {
            System.err.println("Erro ao importar CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seed(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuarios")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; 
            }
        }

        System.out.println("Semeando banco de dados com dados de teste...");

        stmt.execute("INSERT INTO usuarios (nome, senha, pontos, streak, bio, foto) VALUES " +
            "('Josias', '123', 150, 5, 'Apaixonado por Java e Café.', 'https://ui-avatars.com/api/?name=Josias&background=0D8ABC&color=fff'), " +
            "('Maria Leitora', '123', 300, 12, 'Devoradora de livros de fantasia.', 'https://ui-avatars.com/api/?name=Maria&background=random')");

        stmt.execute("INSERT INTO clubes (nome, descricao, foto, dono_id) VALUES " +
            "('Clube dos Javaleiros', 'Grupo para discutir livros técnicos e tomar café.', 'https://ui-avatars.com/api/?name=Java&background=ff0000&color=fff', 1)");

        stmt.execute("INSERT INTO membros_clube (clube_id, usuario_id, cargo) VALUES (1, 1, 'DONO')"); // Josias no clube como Dono
        stmt.execute("INSERT INTO membros_clube (clube_id, usuario_id, cargo) VALUES (1, 2, 'MEMBRO')"); // Maria no clube como Membro
    }
}