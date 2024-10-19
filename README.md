# INE5680-TP3

## IfoodOrderSimulation - Simulação de Pedido com Autenticação e Criptografia
O projeto `IfoodOrderSimulation` foi desenvolvido para demonstrar conceitos de segurança alinhados com a disciplina INE5680, incluindo autenticação de dois fatores (2FA), criptografia e manipulação de tokens. A linguagem Java foi escolhida em decorrência dos exemplos vistos na disciplina. O Maven foi utilizado para gerenciar dependências e automatizar tarefas de construção do projeto, facilitando a inclusão de bibliotecas externas. Entre as bibliotecas adotadas, destacam-se o Bouncy Castle FIPS, para operações de criptografia, e a JWT, para simular tokens de autenticação, aproveitando a simplicidade de geração e validação de códigos de autenticação temporários (TOTP).

## Autores
- Vanessa Cunha (17100926)
- Feline Neves Dias (18100850)

## Descrição do Projeto

O projeto IfoodOrderSimulation é uma aplicação Java que simula um sistema de pedidos do iFood, incorporando recursos de segurança como autenticação de dois fatores (2FA) usando TOTP e criptografia de mensagens. Ele demonstra o uso de tecnologias de criptografia e autenticação para garantir a segurança dos dados dos usuários durante um pedido online.

## Funcionalidades:
1. Escolha de pratos pelo usuário.
2. Solicitação de um número de celular para criar um pedido.
3. Geração de um código TOTP (Token One-Time Password) para autenticação de dois fatores.
4. Validação do código TOTP fornecido pelo usuário.
5. Criptografia de informações de pagamento usando AES-GCM.
6. Decifração dos dados para garantir que as operações de criptografia estejam funcionando corretamente.
7. Envio de mensagens criptografadas sobre o horário de entrega e decifração das mesmas.

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

Durante a execução, você verá as seguintes etapas:
1. Será solicitado que você escolha um prato.
2. Digite o número de celular.
3. Um código TOTP será gerado e exibido.
4. Digite o código TOTP para autenticar.
5. A aplicação então irá criptografar e decifrar dados de pagamento e mensagens, demonstrando a segurança dos dados.

### Desenvolvimento e Considerações
A estrutura do projeto foi organizada seguindo boas práticas, com pacotes bem definidos para facilitar a modularidade e futura expansão, caso necessário para os trabalhos com implementação futuros. A definição de um arquivo .gitignore ajudou a manter o repositório limpo, incluindo apenas o código-fonte e arquivos essenciais. Para operações de criptografia, foi selecionado o AES-GCM, um modo que oferece segurança adicional com autenticação e integridade dos dados cifrados. 

### Ferramentas Utilizadas

- Visual Studio Code para o desenvolvimento.
- Controle de Versão: Git
- Build e Gerenciamento de Dependências: Maven

### Problemas Conhecidos

- Certifique-se de que o BCFIPS (Bouncy Castle) esteja corretamente configurado para evitar problemas de inicialização de criptografia.
- O código TOTP gerado é básico e não sincroniza com tempo real. Certifique-se de digitar o código correto durante o teste.

### Licença

Este projeto é licenciado sob a MIT License - veja o arquivo LICENSE para mais detalhes.
