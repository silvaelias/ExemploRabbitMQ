package br.ufs.dcomp.ExemploRabbitMQ;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
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

import com.rabbitmq.client.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receptor {

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    public Receptor(String fila) throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("192.168.1.252");
        factory.setUsername("admin");
        factory.setPassword("password");
        factory.setVirtualHost("/");

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(fila, false, false, false, null);
    }

    public void receberMensagens(String fila, DeliverCallback callback) throws IOException {
        channel.basicConsume(fila, true, callback, consumerTag -> {});
    }

    public void fechar() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
