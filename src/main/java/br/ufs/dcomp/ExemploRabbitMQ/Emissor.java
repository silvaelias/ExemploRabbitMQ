package br.ufs.dcomp.ExemploRabbitMQ;



import br.ufs.dcomp.ExemploRabbitMQ.MensagemOuterClass.Mensagem;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;



public class Emissor {

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    public Emissor() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("192.168.1.252");
        factory.setUsername("admin");
        factory.setPassword("password");
        factory.setVirtualHost("/");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public void enviarMensagem(Mensagem mensagem, String destinatario) throws IOException {
        byte[] mensagemSerializada = mensagem.toByteArray();
        if (destinatario.startsWith("#")) {
            // Enviar para um grupo
            channel.basicPublish(destinatario, "", null, mensagemSerializada);
        } else {
            // Enviar para um usuário específico
            channel.basicPublish("", destinatario, null, mensagemSerializada);
        }
    }

    public void fechar() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
