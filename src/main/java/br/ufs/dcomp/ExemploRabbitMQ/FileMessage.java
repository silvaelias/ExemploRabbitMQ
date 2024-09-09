package br.ufs.dcomp.ExemploRabbitMQ;

import java.io.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.protobuf.ByteString;

import br.ufs.dcomp.ExemploRabbitMQ.MensagemOuterClass.Conteudo;
import br.ufs.dcomp.ExemploRabbitMQ.MensagemOuterClass.Mensagem;

import java.io.IOException;

public class FileMessage {

    private String fileName;
    private String mimeType;
    private byte[] fileContent;

    // Construtor para criar um FileMessage a partir do caminho de um arquivo
    public FileMessage(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        this.fileName = path.getFileName().toString();
        this.mimeType = Files.probeContentType(path);
        this.fileContent = Files.readAllBytes(path);
    }

    // Getters
    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    // Método para converter um FileMessage em uma mensagem Protocol Buffers
    public Mensagem toMensagem(String emissor, String data, String hora, String grupo) {
        Conteudo conteudo = Conteudo.newBuilder()
            .setTipo(mimeType)
            .setNome(fileName)
            .setCorpo(ByteString.copyFrom(fileContent))
            .build();

        return Mensagem.newBuilder()
            .setEmissor(emissor)
            .setData(data)
            .setHora(hora)
            .setGrupo(grupo)
            .setConteudo(conteudo)
            .build();
    }

    // Método estático para converter uma Mensagem Protocol Buffers em um FileMessage
    public static FileMessage fromMensagem(Mensagem mensagem) throws IOException {
        String fileName = mensagem.getConteudo().getNome();
        byte[] fileContent = mensagem.getConteudo().getCorpo().toByteArray();
        Path path = Paths.get("downloads/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, fileContent); // Salva o arquivo na pasta downloads
        return new FileMessage(path.toString());
    }
}
