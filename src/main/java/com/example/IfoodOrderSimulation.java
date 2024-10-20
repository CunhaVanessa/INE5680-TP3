package com.example;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.util.encoders.Base64;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class IfoodOrderSimulation {

    public static void main(String[] args) {
        try {
            // Adicionar Bouncy Castle FIPS como provedor de segurança
            Security.addProvider(new BouncyCastleFipsProvider());

            // Configuração do Scanner para entrada do usuário
            Scanner scanner = new Scanner(System.in);

            // 1. Escolha do prato
            System.out.println("Escolha o prato: ");
            String pratoEscolhido = scanner.nextLine();
            System.out.println("Prato escolhido: " + pratoEscolhido);

            // 2. Solicitar celular do usuário
            System.out.println("Digite seu número de celular: ");
            String celular = scanner.nextLine();

            // 3. Geração de TOTP e QR Code
            String secret = generateSecret();
            String token = generateTotp(secret);

            // Gerar e exibir QR Code para o usuário
            generateQrCode(token, "qrcode.png");
            System.out.println("QR Code gerado e salvo como 'qrcode.png'. Escaneie para visualizar o código TOTP.");

            // 4. Validação do código TOTP
            System.out.println("Digite o código TOTP gerado: ");
            String codigoTOTP = scanner.nextLine();

            // Validar código TOTP
            if (codigoTOTP.equals(token)) {
                System.out.println("Autenticação de dois fatores validada.");
            } else {
                System.out.println("Código inválido. Encerrando.");
                return;
            }

            // 5. Derivação de chave de sessão usando PBKDF2 com salt dinâmico
            byte[] salt = generateSalt();
            Key sessionKey = deriveKey(codigoTOTP, salt);

            // 6. Geração de IV dinâmico para AES-GCM
            byte[] iv = generateIv();

            // Cifrar comprovante de pagamento usando AES-GCM
            String comprovante = "Comprovante de pagamento";
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS");
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey, new GCMParameterSpec(128, iv));
            byte[] cipherText = cipher.doFinal(comprovante.getBytes());
            System.out.println("Comprovante cifrado: " + Base64.toBase64String(cipherText));

            // Decifrar comprovante e enviar pedido
            cipher.init(Cipher.DECRYPT_MODE, sessionKey, new GCMParameterSpec(128, iv));
            byte[] plainText = cipher.doFinal(cipherText);
            System.out.println("Comprovante decifrado: " + new String(plainText));

            // Enviar mensagem cifrada sobre horário do pedido
            String mensagem = "Seu pedido será entregue em 30 minutos.";
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey, new GCMParameterSpec(128, iv));
            byte[] mensagemCifrada = cipher.doFinal(mensagem.getBytes());

            // Decifrar mensagem para exibição
            cipher.init(Cipher.DECRYPT_MODE, sessionKey, new GCMParameterSpec(128, iv));
            byte[] mensagemDecifrada = cipher.doFinal(mensagemCifrada);
            System.out.println("Mensagem recebida: " + new String(mensagemDecifrada));

            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Key deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    private static String generateSecret() {
        // Simulação de geração de uma chave secreta
        return "MySecretKey";
    }

    private static String generateTotp(String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Date now = new Date();
        long secondsSinceEpoch = now.getTime() / 1000;
        String token = JWT.create().withClaim("totp", secondsSinceEpoch).sign(algorithm);
        return token;
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16]; // 16 bytes = 128 bits de salt
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static byte[] generateIv() {
        byte[] iv = new byte[12]; // IV de 12 bytes recomendado para GCM
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private static void generateQrCode(String data, String filePath) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            File qrFile = new File(filePath);
            ImageIO.write(qrImage, "PNG", qrFile);
            System.out.println("QR Code gerado com sucesso.");
        } catch (WriterException | IOException e) {
            System.err.println("Erro ao gerar QR Code: " + e.getMessage());
        }
    }
}
