# INE5680-TP3

## Projeto: 
O projeto `IfoodOrderSimulation` foi desenvolvido para demonstrar conceitos avançados de segurança na disciplina INE5680. Ele aborda autenticação de dois fatores (2FA), criptografia de dados e manipulação segura de tokens, usando Java e Maven para automação e gestão de dependências.

## Autores:
- Vanessa Cunha (17100926)
- Felipe Neves Dias (18100850)

## Descrição:
O `IfoodOrderSimulation` é uma aplicação Java que simula um sistema de pedidos do iFood com segurança, incluindo 2FA usando TOTP e criptografia de mensagens, protegendo dados dos usuários em pedidos online.

## Funcionalidades:
1. Menu de escolha de pratos
2. Solicitação de celular para vincular o pedido à autenticação 2FA
3. Geração de código TOTP exibido via QR Code
4. Validação do TOTP para autenticação 2FA
5. Criptografia do comprovante de pagamento com AES-GCM
6. Decifragem do comprovante para garantir segurança
7. Envio e recebimento de mensagens cifradas com tempo estimado de entrega

## Estrutura do Projeto:
- `Client.java` - Cliente que interage com o servidor para fazer o pedido e gerenciar autenticação e criptografia
- `Server.java` - Servidor que processa o pedido, gera TOTP e gerencia criptografia
- `pom.xml` - Gerencia as dependências com Maven

## Pré-requisitos:
- Java 17+ e Maven 3.6+
- Bibliotecas: Bouncy Castle (criptografia), Auth0 JWT (TOTP), Commons Codec (Base64)

## Como Executar:
1. Clonar e navegar até o repositório

`git clone https://github.com/seu-usuario/IfoodOrderSimulation.git`
 
`cd IfoodOrderSimulation`

2. Compilar e instalar dependências

`mvn clean install`

3. Executar aplicação
   Para o servidor:

`mvn exec:java -Dexec.mainClass="com.example.Server"` 

   Para o cliente:

`mvn exec:java -Dexec.mainClass="com.example.Client"`

Interação entre as classes Client e Server:
- O Server inicia e aguarda conexões dos clientes na porta 12345. Quando um cliente se conecta:
  - Solicita o prato escolhido e número de celular
  - Gera e envia um código TOTP como QR Code para o cliente autenticar via 2FA
  - Recebe o código TOTP e valida; se correto, deriva uma chave de sessão para cifrar mensagens
  - Criptografa o comprovante de pagamento e o tempo de entrega usando AES-GCM, e envia para o cliente
- O Client:
  - Exibe menu para escolha de pratos e solicita o celular
  - Recebe o QR Code, gera o código TOTP, o envia e, após validação, envia o comprovante
  - Recebe o comprovante e mensagem cifrados e decifra usando a chave de sessão derivada

Desenvolvimento e Ferramentas:
- Desenvolvimento no Visual Studio Code com controle de versão em Git e gerenciamento de dependências com Maven
- Bibliotecas principais: Bouncy Castle FIPS para criptografia e JWT para TOTP

Problemas Conhecidos:
- Configuração do Bouncy Castle FIPS é essencial para inicializar a criptografia corretamente
- O código TOTP é básico e precisa ser inserido no tempo correto de validade

Licença:
Licenciado sob MIT License - veja LICENSE para mais informações.