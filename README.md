# INE5680-TP3

## IfoodOrderSimulation - Simulação de Pedido com Autenticação e Criptografia
O projeto `IfoodOrderSimulation` foi desenvolvido para demonstrar conceitos avançados de segurança, aplicados no contexto da disciplina INE5680. Entre os principais tópicos abordados estão a autenticação de dois fatores (2FA), a criptografia de dados e a manipulação segura de tokens. A escolha da linguagem Java se deu pela familiaridade com os exemplos discutidos em aula e pela robustez que ela oferece para implementações de segurança. O uso do Maven foi essencial para a gestão eficiente das dependências e para a automação das tarefas de construção do projeto, facilitando a integração com diversas bibliotecas externas.

## Autores
- Vanessa Cunha (17100926)
- Feline Neves Dias (18100850)

## Descrição do Projeto

O projeto IfoodOrderSimulation é uma aplicação Java que simula um sistema de pedidos do iFood, incorporando recursos de segurança como autenticação de dois fatores (2FA) usando TOTP e criptografia de mensagens. Ele demonstra o uso de tecnologias de criptografia e autenticação para garantir a segurança dos dados dos usuários durante um pedido online.

## Funcionalidades:
1. Escolha de Prato: O usuário inicia o processo selecionando o prato de comida desejado.
2. Solicitação de Celular: O sistema solicita o número de celular do usuário para criar o pedido e associar com a autenticação de dois fatores.
3. Geração de Código TOTP: Um código TOTP (Time-Based One-Time Password) é gerado dinamicamente e exibido ao usuário via QR Code em formato em PNG, é necessário lê-lo com o smartphone para realizar a autenticaçãao.
4. Validação do Código TOTP: O sistema espera que o usuário insira o código TOTP gerado. Se o código estiver correto, a autenticação de dois fatores é validada com sucesso.
5. Criptografia de Dados de Pagamento: Usando o algoritmo AES-GCM, o sistema criptografa os dados de pagamento, garantindo segurança durante a transmissão e armazenamento das informações.
6. Decifração de Dados: O sistema decifra os dados de pagamento para verificar se a criptografia e decifragem funcionam corretamente, garantindo que a informação foi transmitida de forma segura.
7. Envio e Recebimento de Mensagens Cifradas: O sistema envia uma mensagem cifrada para o usuário com o horário estimado de entrega. O usuário, por sua vez, decifra essa mensagem para visualizar as informações sobre o pedido.

## Estrutura do Projeto

- `IfoodOrderSimulation.java` - Classe principal do projeto, onde está a implementação do trabalho.
- `pom.xml` - Arquivo de configuração e gestão de dependências. 

## Pré-requisitos
- Java 17 ou superior: Certifique-se de que o Java JDK 17 esteja instalado na sua máquina.
- Maven 3.6 ou superior: Ferramenta de build usada para gerenciar dependências e compilar o projeto.

## Dependências

O projeto usa as seguintes bibliotecas:
- Bouncy Castle: Para operações de criptografia.
- Auth0 JWT: Para geração de TOTP.
- Commons Codec: Para operações de codificação Base64.
- Maven Compiler Plugin: Para compilar o código Java.

Todas as dependências são gerenciadas automaticamente pelo Maven e estão listadas no arquivo pom.xml.

## Como Executar o Projeto

### Clonar o Repositório

`git clone https://github.com/seu-usuario/IfoodOrderSimulation.git`


`cd IfoodOrderSimulation`

### Compilar e Instalar

Antes de executar o projeto, compile e instale as dependências:

`mvn clean install`

### Executar a Aplicação

Para rodar a aplicação, use o seguinte comando:

`mvn exec:java -Dexec.mainClass="com.example.IfoodOrderSimulation"`

Durante a execução do projeto IfoodOrderSimulation, as seguintes etapas serão realizadas:
1. O sistema solicitará que você escolha um prato de comida.
2. Em seguida, será pedido que você insira o número de celular.
3. Um código TOTP (Time-Based One-Time Password) será gerado automaticamente e exibido na tela.
4. Você deverá digitar o código TOTP para completar a autenticação de dois fatores.
5. Após a autenticação, a aplicação demonstrará o processo de criptografia e decifragem, utilizando a chave de sessão derivada para proteger dados de pagamento e mensagens, garantindo a segurança e integridade das informações.

### Desenvolvimento e Considerações
No desenvolvimento do projeto, foram utilizadas bibliotecas importantes como o Bouncy Castle FIPS, que proporcionou operações de criptografia seguras e certificadas, atendendo aos requisitos de segurança da aplicação. Além disso, a biblioteca JWT foi incorporada para simular tokens de autenticação, permitindo a geração e validação simplificadas de códigos temporários (TOTP). Essas escolhas de tecnologia garantiram uma implementação robusta e segura, abordando de forma prática conceitos essenciais para sistemas que demandam autenticação forte e proteção de dados.

### Ferramentas Utilizadas

- Visual Studio Code para o desenvolvimento.
- Controle de Versão: Git
- Build e Gerenciamento de Dependências: Maven

### Problemas Conhecidos

- Certifique-se de que o BCFIPS (Bouncy Castle) esteja corretamente configurado para evitar problemas de inicialização de criptografia.
- O código TOTP gerado é básico e não sincroniza com tempo real. Certifique-se de digitar o código correto durante o teste.

### Licença

Este projeto é licenciado sob a MIT License - veja o arquivo LICENSE para mais detalhes.
