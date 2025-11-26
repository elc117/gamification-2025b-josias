package io.literato.model;

public class Usuario {
    private int id;
    private String nome;
    private String senha;
    private int pontos;
    private int streak;
    private String ultimaLeitura;
    
    private String bio;
    private String foto;

    public Usuario() {}

    public Usuario(int id, String nome, String senha, int pontos, int streak, String ultimaLeitura, String bio, String foto) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.pontos = pontos;
        this.streak = streak;
        this.ultimaLeitura = ultimaLeitura;
        this.bio = bio;
        this.foto = foto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public int getPontos() { return pontos; }
    public void setPontos(int pontos) { this.pontos = pontos; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public String getUltimaLeitura() { return ultimaLeitura; }
    public void setUltimaLeitura(String ultimaLeitura) { this.ultimaLeitura = ultimaLeitura; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public void ganharPontos(int qtd) {
        if (qtd > 0) {
            this.pontos += qtd;
        }
    }

    public void incrementarStreak() {
        this.streak++;
    }

    public void resetarStreak() {
        this.streak = 0;
    }

    public void registrarLeitura(java.time.LocalDate dataLeitura) {
        if (this.ultimaLeitura == null) {
            this.streak = 1;
        } else {
            java.time.LocalDate ultima = java.time.LocalDate.parse(this.ultimaLeitura);
            if (ultima.equals(dataLeitura)) {
                return; // Já leu hoje
            } else if (ultima.equals(dataLeitura.minusDays(1))) {
                this.streak++;
            } else {
                this.streak = 1;
            }
        }
        this.ultimaLeitura = dataLeitura.toString();
    }
}