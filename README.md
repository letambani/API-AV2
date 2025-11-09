# AV2 - Spring Boot API com JWT

API REST desenvolvida em Spring Boot (Java 21) com autenticação JWT, gerenciamento de alunos e tratamento de erros padronizado.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **MySQL**
- **Maven**
- **JJWT** (JSON Web Tokens)
- **BCrypt** (Criptografia de senhas)

## 📋 Funcionalidades

- ✅ Autenticação JWT (email/senha)
- ✅ CRUD de Alunos
- ✅ Validação de dados (Bean Validation)
- ✅ Tratamento de erros padronizado (JSON)
- ✅ CORS configurado para frontend
- ✅ Senhas criptografadas com BCrypt
- ✅ DTOs para resposta da API
- ✅ Global Exception Handler

## 🔧 Pré-requisitos

- Java 21
- Maven 3.6+
- MySQL 8.0+
- Node.js (opcional, para servidor frontend de teste)

## ⚙️ Configuração

### 1. Banco de Dados

Crie o banco de dados MySQL:

```sql
CREATE DATABASE av2;
CREATE USER 'av2'@'localhost' IDENTIFIED BY 'av2pwd';
GRANT ALL PRIVILEGES ON av2.* TO 'av2'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configuração da API

Edite `src/main/resources/application.properties` se necessário:

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/av2?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=av2
spring.datasource.password=av2pwd
```

### 3. Executar a API

```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8081`

## 📝 Endpoints

### Autenticação

- `POST /auth/login` - Login e obtenção de JWT
  ```json
  {
    "email": "admin@fmp.br",
    "senha": "123456"
  }
  ```

### Alunos (Requer autenticação)

- `GET /aluno` - Listar todos os alunos
- `GET /aluno/{id}` - Buscar aluno por ID
- `POST /aluno` - Criar novo aluno
- `PUT /aluno/{id}` - Atualizar aluno
- `DELETE /aluno/{id}` - Deletar aluno

### Admin (Requer role ADMIN)

- `GET /admin/users` - Listar usuários
- `GET /admin/generate-hash?senha=123456` - Gerar hash BCrypt

## 🔐 Credenciais Padrão

- **Email**: `admin@fmp.br`
- **Senha**: `123456`

O usuário é criado automaticamente na primeira execução via `DataSeeder`.

## 🧪 Testes

### Postman

1. Importe a coleção: `Postman_Collection_AV2.json`
2. Execute os testes na ordem sugerida

### Frontend

1. Inicie o servidor frontend:
   ```bash
   node servidor-teste-frontend.js
   ```
2. Acesse: `http://localhost:3000`

## 📦 Estrutura do Projeto

```
av2-api/
├── src/main/java/br/fmp/av2/
│   ├── config/          # Configurações (CORS, DataSeeder)
│   ├── controller/      # Controllers REST
│   ├── dto/             # Data Transfer Objects
│   ├── exception/        # Exceções customizadas
│   ├── model/           # Entidades JPA
│   ├── repository/      # Repositórios Spring Data
│   ├── security/        # Configuração de segurança e JWT
│   └── service/         # Lógica de negócio
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## 📄 Licença

Este é um projeto acadêmico.
