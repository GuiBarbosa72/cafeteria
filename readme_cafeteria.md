# Café Aroma & Grão — API REST e Aplicação Web Integrada

## Descrição do Projeto
O **Café Aroma & Grão** é uma aplicação web integrada a uma API REST desenvolvida em **Java 25** com **Spring Boot 4.2.0** para digitalizar a rotina operacional de uma cafeteria. O sistema substitui o controle manual feito em planilhas e comandas de papel por uma interface web responsiva conectada a um banco de dados relacional **MySQL**.

---

## Problema Atendido, Público Usuário e Funcionalidades

### Problema Resolvido
O controle manual de pedidos, disponibilidade de itens do cardápio, ocupação de mesas e pontuação de fidelidade gera demora no atendimento, falhas na soma dos valores e venda de produtos esgotados. A aplicação centraliza essas operações em tempo real.

### Público Usuário
* **Atendentes e Baristas:** Lançam novos pedidos, acompanham o status de preparo (`ABERTO`, `EM_PREPARO`, `ENTREGUE`, `CANCELADO`) e gerenciam a situação das mesas (`LIVRE`, `OCUPADA`, `RESERVADA`).
* **Gerentes:** Cadastram e atualizam categorias, itens do cardápio (preço e disponibilidade) e clientes do programa de fidelidade.

### Funcionalidades Principais
* **CRUD Completo para 5 Entidades:** Listar todos (`GET`), consultar por ID (`GET /{id}`), cadastrar (`POST`), atualizar (`PUT /{id}`) e excluir (`DELETE /{id}`) para **Categorias**, **Produtos**, **Clientes**, **Mesas** e **Pedidos**.
* **Regras de Negócio Automatizadas:**
  * Cálculo automático do `valorTotal` do pedido (`preço do produto × quantidade`).
  * Acúmulo automático de pontos de fidelidade para o cliente (`1 ponto` a cada `R$ 10,00` gastos).
  * Alteração automática do status da mesa para `OCUPADA` ao abrir um pedido.
  * Bloqueio de pedidos para produtos marcados como indisponíveis (`disponivel = false`).
* **Interface Web Responsiva:** Atualização dinâmica das tabelas sem recarregar a página, com feedback visual de carregamento, sucesso, lista vazia, erro de validação e falha de conexão.

---

## Tecnologias Utilizadas
* **Linguagem:** Java 25 LTS
* **Framework Backend:** Spring Boot 4.2.0 (Spring Web / Spring MVC, Spring Data JPA)
* **Documentação Interativa da API:** SpringDoc OpenAPI 3.2.1 (Swagger UI)
* **Banco de Dados:** MySQL 8+ (`mysql-connector-j`) e H2 (para testes automatizados)
* **Frontend:** HTML5, CSS3 e JavaScript puro (ES6+ com `fetch`)
* **Gerenciador de Dependências e Testes:** Maven e JUnit

---

## Estrutura do Projeto

```text
cafeteria/
├── pom.xml                                  # Dependências e configurações do Maven
├── .env.example                             # Modelo de variáveis de ambiente
├── .gitignore                               # Arquivos ignorados pelo Git
├── README.md                                # Documentação do projeto
├── requests.http                            # Coleção executável de requisições HTTP
└── src/
    ├── main/
    │   ├── java/com/dt2/cafeteria/
    │   │   ├── CafeteriaApplication.java    # Classe principal que inicia a aplicação
    │   │   ├── entities/                    # Entidades JPA (Categoria, Produto, Cliente, Mesa, Pedido)
    │   │   ├── repository/                  # Interfaces Spring Data JPA
    │   │   └── controller/                  # Controladores REST (/api/*)
    │   └── resources/
    │       ├── application.properties       # Configurações do servidor e conexão com MySQL
    │       ├── schema.sql                   # Script de criação das tabelas no MySQL
    │       ├── data.sql                     # Script com dados iniciais para teste
    │       └── static/                      # Página web integrada
    │           ├── index.html
    │           ├── styles.css
    │           └── script.js
    └── test/                                # Testes automatizados
```

---

## Pré-requisitos e Execução Local

### Pré-requisitos
* **JDK 25** instalado (`java -version`)
* **Maven 3.9+** instalado (`mvn -version`)
* **MySQL 8+** em execução na máquina local

### Passo a Passo para Executar
1. Clone o repositório e acesse a pasta do projeto:
   ```bash
   git clone <URL_DO_REPOSITORIO>
   cd cafeteria
   ```
2. Configure o usuário e senha do seu MySQL (veja a seção abaixo).
3. Inicie a aplicação com o Maven:
   ```bash
   mvn spring-boot:run
   ```
4. Acesse no navegador:
   * **Aplicação Web (Frontend):** `http://localhost:8080`
   * **Documentação Swagger / OpenAPI:** `http://localhost:8080/swagger-ui.html`

---

## Configuração do Banco de Dados e Variáveis de Ambiente

A criação das tabelas (`src/main/resources/schema.sql`) e a inserção dos registros iniciais (`src/main/resources/data.sql`) acontecem automaticamente quando a aplicação sobe (`spring.sql.init.mode=always`). O banco `cafeteria_db` também é criado automaticamente caso ainda não exista (`createDatabaseIfNotExist=true`).

Para não expor senhas ou dados sensíveis no repositório, utilize as variáveis de ambiente descritas no arquivo `.env.example`:

| Variável | Descrição | Valor Padrão |
| :--- | :--- | :--- |
| `SERVER_PORT` | Porta HTTP em que a API será iniciada | `8080` |
| `DB_URL` | URL JDBC de conexão com o banco MySQL | `jdbc:mysql://localhost:3306/cafeteria_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo` |
| `DB_USER` | Usuário de acesso ao banco de dados | `root` |
| `DB_PASS` | Senha de acesso ao banco de dados | *(definir no ambiente)* |
| `DB_DRIVER` | Driver JDBC utilizado | `com.mysql.cj.jdbc.Driver` |

---

## Endereço Base e Resumo dos Endpoints da API

* **Endereço Base:** `http://localhost:8080/api`
* **Formato de Dados:** `application/json`

| Entidade | Listar Todos (`GET`) | Consultar por ID (`GET`) | Cadastrar (`POST`) | Atualizar (`PUT`) | Excluir (`DELETE`) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Categorias** | `/categorias` | `/categorias/{id}` | `/categorias` | `/categorias/{id}` | `/categorias/{id}` |
| **Produtos** | `/produtos` | `/produtos/{id}` | `/produtos` | `/produtos/{id}` | `/produtos/{id}` |
| **Clientes** | `/clientes` | `/clientes/{id}` | `/clientes` | `/clientes/{id}` | `/clientes/{id}` |
| **Mesas** | `/mesas` | `/mesas/{id}` | `/mesas` | `/mesas/{id}` | `/mesas/{id}` |
| **Pedidos** | `/pedidos` | `/pedidos/{id}` | `/pedidos` | `/pedidos/{id}` | `/pedidos/{id}` |

### Códigos HTTP Retornados
* `200 OK`: Consulta ou atualização realizada com sucesso.
* `201 Created`: Novo registro cadastrado com sucesso.
* `204 No Content`: Registro excluído com sucesso.
* `400 Bad Request`: Dados obrigatórios ausentes, valores inválidos ou regra de negócio violada (ex.: produto indisponível).
* `404 Not Found`: Identificador (`id`) não encontrado no banco de dados.

### Exemplo de Requisição e Resposta (`POST /api/pedidos`)

**Requisição:**
```json
{
  "cliente": { "id": 1 },
  "mesa": { "id": 1 },
  "produto": { "id": 2 },
  "quantidade": 2,
  "observacoes": "Sem açúcar, canela extra",
  "status": "ABERTO"
}
```

**Resposta (`201 Created`):**
```json
{
  "id": 2,
  "cliente": {
    "id": 1,
    "nome": "Mariana Costa",
    "telefone": "(11) 98877-6655",
    "email": "mariana.costa@email.com",
    "pontosFidelidade": 17
  },
  "mesa": {
    "id": 1,
    "numero": 1,
    "capacidade": 2,
    "status": "OCUPADA"
  },
  "produto": {
    "id": 2,
    "nome": "Cappuccino Italiano",
    "preco": 14.0,
    "disponivel": true
  },
  "quantidade": 2,
  "observacoes": "Sem açúcar, canela extra",
  "valorTotal": 28.0,
  "status": "ABERTO"
}
```

---

## Testes e Coleção de Requisições

1. **Testes Automatizados (JUnit):**
   ```bash
   mvn test
   ```
2. **Coleção Executável de Requisições (`requests.http`):**
   * Abra o arquivo `requests.http` localizado na raiz do projeto.
   * No **VS Code**, utilize a extensão **REST Client** e clique em *Send Request* acima de cada rota.
   * No **IntelliJ IDEA**, clique no ícone verde de *Play* ao lado de cada requisição para testar os cenários de sucesso (`200`, `201`, `204`) e erro (`400`, `404`).

---

## Créditos
* **Spring Boot & Spring Data JPA:** Framework utilizado para construção da API REST e persistência de dados.
* **SpringDoc OpenAPI:** Biblioteca utilizada para geração automática da documentação interativa Swagger UI.
* **MySQL Connector/J:** Driver oficial de conexão com o banco de dados MySQL.