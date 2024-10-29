package com.example;

import org.apache.commons.codec.binary.Base32;
import org.bouncycastle.util.encoders.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

public class Server {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private Map<String, String> orders = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.start();
    }

    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(12345); // Porta do servidor
        System.out.println("Servidor iniciado na porta 12345.");

        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Cliente conectado.");

                // Comunicação com o cliente
                DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                // Recebe o prato escolhido pelo cliente
                String pratoEscolhido = input.readUTF();
                String tempoEntrega = processOrder(pratoEscolhido);
                output.writeUTF(tempoEntrega); // Envia o tempo de entrega

                // Recebe o número de celular do cliente
                String celular = input.readUTF();
                byte[] salt = celular.getBytes();

                // Gera o TOTP
                String secret = generateSecret();
                String token = generateTotp(secret);

                // Gera QR Code do TOTP
                generateQrCode(token, "qrcode.png");

                // Informa ao cliente que o QR code foi gerado
                output.writeUTF("QR code gerado e salvo como 'qrcode.png'. Escaneie para visualizar o código TOTP.");

                // Recebe o código TOTP digitado pelo cliente
                String codigoTOTP = input.readUTF();

                if (!codigoTOTP.equals(token)) {
                    output.writeUTF("Código inválido. Encerrando.");
                    continue;
                } else {
                    output.writeUTF("Código TOTP válido"); // Confirmação adicional para o cliente
                }

                Key sessionKey = deriveKey(codigoTOTP, salt);

                // Recebe o IV e o comprovante cifrado do cliente
                byte[] iv = Base64.decode(input.readUTF());
                byte[] encryptedComprovante = Base64.decode(input.readUTF());

                // Decifra o comprovante usando o IV e a chave de sessão
                String comprovanteDecifrado = decryptData(encryptedComprovante, sessionKey, iv);
                System.out.println("Comprovante decifrado: " + comprovanteDecifrado);

                // Cifra e envia uma mensagem ao cliente com o tempo de entrega
                byte[] mensagemCifrada = encryptData(tempoEntrega.getBytes(), sessionKey, iv);
                output.writeUTF(Base64.toBase64String(mensagemCifrada));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String processOrder(String pratoEscolhido) {
        String tempoEntrega;
        switch (pratoEscolhido.toLowerCase()) {
            case "pizza": tempoEntrega = "30 minutos"; break;
            case "hamburguer": tempoEntrega = "20 minutos"; break;
            case "sushi": tempoEntrega = "40 minutos"; break;
            case "salada": tempoEntrega = "15 minutos"; break;
            default: tempoEntrega = "tempo indefinido";
        }
        return "Seu pedido de " + pratoEscolhido + " será entregue em " + tempoEntrega + ".";
    }

    public String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] secretKeyBytes = new byte[20];
        random.nextBytes(secretKeyBytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(secretKeyBytes).replace("=", "");
    }

    public String generateTotp(String secret) throws Exception {
        long timeStep = 30;
        long currentTimeSeconds = System.currentTimeMillis() / 1000L;
        long counter = currentTimeSeconds / timeStep;
        Base32 base32 = new Base32();
        byte[] decodedKey = base32.decode(secret);
        byte[] data = ByteBuffer.allocate(8).putLong(counter).array();
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec signKey = new SecretKeySpec(decodedKey, "HmacSHA256");
        mac.init(signKey);
        byte[] hmacResult = mac.doFinal(data);
        int offset = hmacResult[hmacResult.length - 1] & 0xf;
        int binaryCode = (hmacResult[offset] & 0x7f) << 24
                | (hmacResult[offset + 1] & 0xff) << 16
                | (hmacResult[offset + 2] & 0xff) << 8
                | (hmacResult[offset + 3] & 0xff);
        return String.format("%06d", binaryCode % 1_000_000);
    }

    public void generateQrCode(String data, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ImageIO.write(qrImage, "PNG", new File(filePath));
        System.out.println("QR Code gerado e salvo em " + filePath);
    }

    public Key deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public byte[] encryptData(byte[] data, Key key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return cipher.doFinal(data);
    }

    public String decryptData(byte[] encryptedData, Key key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return new String(cipher.doFinal(encryptedData));
    }
}
