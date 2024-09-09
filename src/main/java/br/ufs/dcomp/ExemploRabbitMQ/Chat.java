//grupo 03:
// Isaias Elias da Silva
// Jardel Santos Nascimento
// Sergio Santana dos Santos
package br.ufs.dcomp.ExemploRabbitMQ;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.google.protobuf.ByteString;

import br.ufs.dcomp.ExemploRabbitMQ.MensagemOuterClass.Conteudo;
import br.ufs.dcomp.ExemploRabbitMQ.MensagemOuterClass.Mensagem;

public class Chat {

    private String usuario;
    private String destinatario;
    private Emissor emissor;
    private Receptor receptor;

    public Chat(String usuario) throws IOException, TimeoutException {
        this.usuario = usuario;
        this.emissor = new Emissor();
        this.receptor = new Receptor(usuario);
        iniciarReceptor();
    }

    private void iniciarReceptor() throws IOException {
        // Receber mensagens e arquivos
        receptor.receberMensagens(usuario, (consumerTag, delivery) -> {
            Mensagem mensagem = Mensagem.parseFrom(delivery.getBody());
            if (mensagem.getConteudo().getTipo().startsWith("text")) {
                System.out.println("(" + mensagem.getData() + " às " + mensagem.getHora() + ") "
                        + mensagem.getEmissor() + " diz: " + mensagem.getConteudo().getCorpo().toStringUtf8());
            } else {
                FileMessage fileMessage = FileMessage.fromMensagem(mensagem);
                System.out.println("(" + mensagem.getData() + " às " + mensagem.getHora() + ") "
                        + "Arquivo \"" + fileMessage.getFileName() + "\" recebido de @" + mensagem.getEmissor() + "!");
            }
        });
    }

    private String getPrompt() {
        return destinatario == null ? ">> " : destinatario + ">> ";
    }

    public void iniciar() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(getPrompt());
            String input = scanner.nextLine();

            if (input.startsWith("@")) {
                destinatario = input;
            } else if (input.startsWith("!upload ")) {
                String caminhoArquivo = input.replace("!upload ", "");
                enviarArquivo(caminhoArquivo);
            } else {
                enviarMensagem(input);
            }
        }
    }

    private void enviarMensagem(String texto) {
        try {
            Conteudo conteudo = Conteudo.newBuilder()
                .setTipo("text/plain")
                .setCorpo(ByteString.copyFrom(texto, StandardCharsets.UTF_8))
                .build();

            Mensagem mensagem = Mensagem.newBuilder()
                .setEmissor(usuario)
                .setData(LocalDate.now().toString())
                .setHora(LocalTime.now().toString())
                .setGrupo(destinatario.startsWith("#") ? destinatario : "")
                .setConteudo(conteudo)
                .build();

            emissor.enviarMensagem(mensagem, destinatario);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enviarArquivo(String caminhoArquivo) {
        try {
            FileMessage fileMessage = new FileMessage(caminhoArquivo);
            System.out.println("Enviando \"" + caminhoArquivo + "\" para " + destinatario + ".");

            new Thread(() -> {
                try {
                    Mensagem mensagem = fileMessage.toMensagem(usuario, LocalDate.now().toString(), LocalTime.now().toString(), destinatario.startsWith("#") ? destinatario : "");
                    emissor.enviarMensagem(mensagem, destinatario);
                    System.out.println("Arquivo \"" + caminhoArquivo + "\" foi enviado para @" + destinatario + "!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.print("User: ");
        Scanner scanner = new Scanner(System.in);
        String usuario = scanner.nextLine();
        Chat chat = new Chat(usuario);
        chat.iniciar();
    }
}
