package br.ufs.dcomp.ExemploRabbitMQ;

import com.google.protobuf.InvalidProtocolBufferException;

import br.ufs.dcomp.ExemploRabbitMQ.MensagemOuterClass.Conteudo;
import br.ufs.dcomp.ExemploRabbitMQ.MensagemOuterClass.Mensagem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

import com.rabbitmq.client.*;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.*;

import java.io.IOException;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Emissor {
    private static final String EXCHANGE_NAME = "chat_exchange";
    private Connection connection;
    private Channel channel;

    public Emissor() throws TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.252");
        factory.setUsername("admin"); // Nome de usuário padrão
        factory.setPassword("password"); // Senha padrão
        factory.setVirtualHost("/");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String destination, byte[] fileBytes, String mimeType, String fileName) throws IOException {
        String routingKey = getRoutingKey(destination);
        Map<String, Object> headers = new HashMap<>();
        headers.put("fileName", fileName);

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType(mimeType)
                .headers(headers)
                .build();

        channel.basicPublish(EXCHANGE_NAME, routingKey, props, fileBytes);
    }

    private String getRoutingKey(String destination) {
        if (destination.startsWith("#")) {
            return "group." + destination.substring(1);
        } else if (destination.startsWith("@")) {
            return "user." + destination.substring(1);
        } else {
            throw new IllegalArgumentException("Destinatário inválido.");
        }
    }
}
