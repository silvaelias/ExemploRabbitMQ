//grupo 03:
// Isaias Elias da Silva
// Jardel Santos Nascimento
// Sergio Santana dos Santos
package br.ufs.dcomp.ExemploRabbitMQ;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Scanner;

public class Chat {
    private Emissor emissor;
    private ExecutorService executorService;

    public Chat() throws TimeoutException {
        emissor = new Emissor();
        executorService = Executors.newFixedThreadPool(10); // Pool de threads para upload em background
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(getPrompt());
            String command = scanner.nextLine();
            if (command.startsWith("!upload")) {
                handleUploadCommand(command);
            } else {
                // Outros comandos
            }
        }
    }

    private void handleUploadCommand(String command) {
        // Exemplo de comando: !upload /home/tarcisio/aula1.pdf @marciocosta
        String[] parts = command.split(" ", 3);
        if (parts.length < 3) {
            System.out.println("Comando inválido. Uso: !upload <caminho-do-arquivo> <destinatário>");
            return;
        }

        String filePath = parts[1];
        String destination = parts[2];

        Path source = Paths.get(filePath);
        if (!Files.exists(source)) {
            System.out.println("Arquivo não encontrado: " + filePath);
            return;
        }

        String mimeType = getMimeType(source);
        byte[] fileBytes;
        try {
            fileBytes = Files.readAllBytes(source);
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

        // Exibe a mensagem não bloqueante
        System.out.println("Enviando \"" + filePath + "\" para " + destination + ".");

        // Envio do arquivo em background
        executorService.submit(() -> {
            try {
                emissor.sendFile(destination, fileBytes, mimeType, filePath);
                System.out.println("Arquivo \"" + filePath + "\" foi enviado para " + destination + " !");
            } catch (IOException e) {
                System.out.println("Erro ao enviar o arquivo: " + e.getMessage());
            }
        });
    }

    private String getMimeType(Path path) {
        try {
            return Files.probeContentType(path);
        } catch (IOException e) {
            return "application/octet-stream"; // Tipo MIME padrão
        }
    }

    private String getPrompt() {
        // Prompt personalizado para o usuário ou grupo corrente
        // Exemplo: Retornar prompt para usuário ou grupo corrente
        return "@usuario>> "; // Alterar conforme necessário
    }

    public static void main(String[] args) throws TimeoutException {
        Chat chat = new Chat();
        chat.start();
    }
}
