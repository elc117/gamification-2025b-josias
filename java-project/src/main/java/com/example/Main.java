package com.example;

import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {

        LeituraController controller = new LeituraController();


        Javalin app = Javalin.create(config -> {
        }).start(7070); 

        System.out.println("--- Servidor de Leitura Gamificado rodando! ---");
        System.out.println("Acesse: http://localhost:7070");

        app.get("/", controller::getBoasVindas);
        app.post("/leitor", controller::criarLeitor);
        app.get("/leitor/{email}", controller::getLeitorPorEmail);
        app.get("/leitores", controller::getTodosLeitores);
    }
}