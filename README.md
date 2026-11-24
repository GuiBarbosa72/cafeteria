# Café Aroma & Grão — API REST e Aplicação Web Integrada

## Descrição do Projeto
Aplicação web completa para digitalizar o atendimento e o controle interno da cafeteria **Café Aroma & Grão**, substituindo controles manuais em planilhas e comandas de papel por uma API REST em Java 25 integrada a uma interface web responsiva.

## Problema Atendido, Público Usuário e Funcionalidades
- **Problema:** Dificuldade no controle simultâneo de mesas, disponibilidade de produtos do cardápio, pontos de fidelidade e registro de pedidos.
- **Público Usuário:** Atendentes, baristas e gerentes da cafeteria.
- **Funcionalidades:** CRUD completo (`GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`) para **Categorias**, **Produtos**, **Clientes**, **Mesas** e **Pedidos**, com cálculo automático de valor total, acúmulo de pontos de fidelidade (1 ponto a cada R$ 10,00) e bloqueio de venda para produtos indisponíveis.

## Tecnologias Utilizadas (Spring Initializr)
- **Backend:** Java 25 LTS, Spring Boot **4.2.0** (`jar`), Spring Web (`spring-boot-starter-webmvc`), Spring Data JPA, SpringDoc OpenAPI 3.2.1 (Swagger).
- **Banco de Dados:** MySQL 8+ (`mysql-connector-j`).
- **Frontend:** HTML5, CSS3 e JavaScript (`fetch`).
- **Build e Testes:** Maven e JUnit.

## Pré-requisitos e Execução
1. Ter **JDK 25**, **Maven 3.9+** e **MySQL 8+** em execução.
2. Ajustar as credenciais do MySQL em `src/main/resources/application.properties` ou via variáveis de ambiente (consulte `.env.example`). O banco `cafeteria_db` é criado automaticamente na conexão (`createDatabaseIfNotExist=true`).
3. Executar na raiz do projeto (`cafeteria`):
   ```bash
   mvn spring-boot:run
   ```
4. Acessar no navegador:
   - **Aplicação Web:** `http://localhost:8080`
   - **Documentação Swagger/OpenAPI:** `http://localhost:8080/swagger-ui.html`

## Endereço Base e Resumo dos Endpoints
Endereço base: `http://localhost:8080/api`

| Entidade | Listar (`GET`) | Consultar ID (`GET`) | Cadastrar (`POST`) | Atualizar (`PUT`) | Excluir (`DELETE`) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Categorias** | `/categorias` | `/categorias/{id}` | `/categorias` | `/categorias/{id}` | `/categorias/{id}` |
| **Produtos** | `/produtos` | `/produtos/{id}` | `/produtos` | `/produtos/{id}` | `/produtos/{id}` |
| **Clientes** | `/clientes` | `/clientes/{id}` | `/clientes` | `/clientes/{id}` | `/clientes/{id}` |
| **Mesas** | `/mesas` | `/mesas/{id}` | `/mesas` | `/mesas/{id}` | `/mesas/{id}` |
| **Pedidos** | `/pedidos` | `/pedidos/{id}` | `/pedidos` | `/pedidos/{id}` | `/pedidos/{id}` |
