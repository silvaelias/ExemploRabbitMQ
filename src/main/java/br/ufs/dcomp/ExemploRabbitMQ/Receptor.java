package br.ufs.dcomp.ExemploRabbitMQ;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import com.rabbitmq.client.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rabbitmq.client.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Receptor {
    private static final String EXCHANGE_NAME = "chat_exchange";
    private static final String DOWNLOAD_DIR = "/home/tarcisio/chat/downloads";
    private Connection connection;
    private Channel channel;

    public Receptor() throws TimeoutException {
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

    public void startListening(String queueName) throws IOException {
        channel.queueDeclare(queueName, false, false, true, null);
        channel.queueBind(queueName, EXCHANGE_NAME, queueName);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            byte[] body = delivery.getBody();
            String mimeType = delivery.getProperties().getContentType();
            String fileName = (String) delivery.getProperties().getHeaders().get("fileName");
            String sender = delivery.getEnvelope().getRoutingKey().split("\\.")[1];
            saveFile(fileName, body);
            System.out.println(new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm").format(new Date()) +
                    " Arquivo \"" + fileName + "\" recebido de @" + sender + "!");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }

    private void saveFile(String fileName, byte[] fileBytes) {
        File file = new File(DOWNLOAD_DIR, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
