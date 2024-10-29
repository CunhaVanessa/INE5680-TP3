package com.example;

import org.bouncycastle.util.encoders.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Scanner;

public class Client {
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345)) {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);

            // 1. Escolha do prato e envio para o servidor
            String pratoEscolhido = escolherPrato();
            if (pratoEscolhido == null) {
                System.out.println("Encerrando o programa devido à escolha inválida.");
                return;
            }
            output.writeUTF(pratoEscolhido);
            output.flush();

            // Recebe o tempo de entrega do servidor
            String tempoEntrega = input.readUTF();
            System.out.println("Prato escolhido: " + pratoEscolhido);

            // 2. Solicitar celular e gerar chave de sessão
            System.out.println("Digite seu número de celular: ");
            String celular = scanner.nextLine();
            output.writeUTF(celular);
            output.flush();

            // Recebe confirmação de QR code
            String qrConfirmation = input.readUTF();
            System.out.println(qrConfirmation);

            // Solicita código TOTP do usuário
            System.out.println("Digite o código TOTP exibido no QR Code: ");
            String codigoTOTP = scanner.nextLine();
            output.writeUTF(codigoTOTP);
            output.flush();

            // Verifica se o código TOTP é válido
            String validacaoTOTP = input.readUTF();
            if (validacaoTOTP.equals("Código inválido. Encerrando.")) {
                System.out.println(validacaoTOTP);
                return;
            } else {
                System.out.println("Código TOTP válido. Prosseguindo...");
            }

            // Derivar a chave de sessão
            Key sessionKey = deriveKey(codigoTOTP, celular.getBytes());

            // 3. Solicitar comprovante de pagamento e cifrar com a chave de sessão antes de enviar
            System.out.println("Digite o comprovante de pagamento: ");
            String comprovante = scanner.nextLine();

            // Gera um novo IV para o comprovante e cifra com a chave de sessão
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            byte[] encryptedComprovante = encryptData(comprovante.getBytes(), sessionKey, iv);

            // Envia o IV e o comprovante cifrado para o servidor
            output.writeUTF(Base64.toBase64String(iv)); // Envia o IV usado na cifragem
            output.writeUTF(Base64.toBase64String(encryptedComprovante)); // Envia o comprovante cifrado
            output.flush();
            System.out.println("Comprovante cifrado enviado.");

            // Recebe e exibe a mensagem cifrada sobre o pedido
            String mensagemCifrada = input.readUTF();
            System.out.println("Mensagem cifrada sobre o pedido: " + mensagemCifrada);

            // Descriptografa a mensagem cifrada sobre o pedido
            byte[] decryptedMessage = decryptData(Base64.decode(mensagemCifrada), sessionKey, iv);
            System.out.println("Mensagem decifrada sobre o pedido: " + new String(decryptedMessage));

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método de escolha de prato com menu
    public static String escolherPrato() {
        String[] pratos = {"Pizza", "Hamburguer", "Sushi", "Salada"};
        System.out.println("Escolha o prato:");
        for (int i = 0; i < pratos.length; i++) {
            System.out.println((i + 1) + ". " + pratos[i]);
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o número do prato: ");
        int escolha = scanner.nextInt() - 1;
        scanner.nextLine(); // Consome a nova linha
        if (escolha >= 0 && escolha < pratos.length) {
            return pratos[escolha];
        } else {
            System.out.println("Escolha inválida.");
            return null;
        }
    }

    // Método para derivar uma chave de 256 bits para AES
    public static Key deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    // Método de cifragem de dados
    public static byte[] encryptData(byte[] data, Key key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);
        return cipher.doFinal(data);
    }

    // Método de descriptografia de dados
    public static byte[] decryptData(byte[] encryptedData, Key key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
        return cipher.doFinal(encryptedData);
    }
}
