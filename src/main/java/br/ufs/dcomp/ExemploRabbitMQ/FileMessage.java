package br.ufs.dcomp.ExemploRabbitMQ;

import java.io.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class FileMessage implements Serializable {
    private String fileName;
    private String mimeType;
    private byte[] fileBytes;

    public FileMessage(String fileName, String mimeType, byte[] fileBytes) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
            return baos.toByteArray();
        }
    }

    public static FileMessage deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(data);
             java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais)) {
            return (FileMessage) ois.readObject();
        }
    }
}
