# Como Verificar Criptografia BCrypt no Postman

## Objetivo
Demonstrar que:
1. Senhas salvas no banco estão criptografadas (BCrypt)
2. Usuário autentica com email/senha (não com hash)

---

## Teste 1: Gerar Hash BCrypt

### Endpoint:
```
GET http://localhost:8081/admin/generate-hash?senha=123456
```

### O que fazer:
1. Abra o Postman
2. Crie um novo request ou use o teste "9. Gerar Hash BCrypt"
3. Método: `GET`
4. URL: `http://localhost:8081/admin/generate-hash?senha=123456`
5. Clique em "Send"

### Resposta esperada:
```json
{
  "senhaOriginal": "123456",
  "senhaHash": "$2a$10$abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUV",
  "hashLength": "60",
  "isBCrypt": "true"
}
```

### O que observar:
- **senhaOriginal**: `"123456"` (senha em texto plano)
- **senhaHash**: Começa com `$2a$` ou `$2b$` (formato BCrypt)
- **hashLength**: `60` caracteres (tamanho padrão do BCrypt)
- **isBCrypt**: `true` (confirma que é BCrypt)

### Conclusão:
A senha `123456` foi convertida em um hash BCrypt de 60 caracteres que começa com `$2a$` ou `$2b$`.

---

## Teste 2: Verificar Senha no Banco de Dados

### Endpoint:
```
GET http://localhost:8081/admin/users
```

### Pré-requisito:
- Execute primeiro o teste "1. Login - Obter JWT" para obter o token
- O token deve ser de um usuário com role `ROLE_ADMIN`

### O que fazer:
1. Use o teste "10. Listar Usuários - Ver Senhas Criptografadas"
2. Ou crie um novo request:
   - Método: `GET`
   - URL: `http://localhost:8081/admin/users`
   - Header: `Authorization: Bearer {{jwt}}`
3. Clique em "Send"

### Resposta esperada:
```json
[
  {
    "id": 1,
    "email": "admin@fmp.br",
    "role": "ROLE_ADMIN",
    "senhaHash": "$2a$10$abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUV",
    "senhaHashLength": 60,
    "isBCrypt": true
  }
]
```

### O que observar:
- **senhaHash**: Hash BCrypt (não a senha original `123456`)
- **senhaHashLength**: `60` caracteres
- **isBCrypt**: `true`
- **email**: `admin@fmp.br` (usado para autenticação)

### Conclusão:
No banco de dados, a senha está armazenada como hash BCrypt, não como texto plano.

---

## Teste 3: Autenticar com Email/Senha (Não com Hash)

### Endpoint:
```
POST http://localhost:8081/auth/login
```

### Body:
```json
{
  "email": "admin@fmp.br",
  "senha": "123456"
}
```

### O que fazer:
1. Use o teste "1. Login - Obter JWT"
2. Body: `{"email": "admin@fmp.br", "senha": "123456"}`
3. Clique em "Send"

### Resposta esperada:
```json
{
  "type": "Bearer",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 3600
}
```

### O que observar:
- **Autenticação funciona** com email e senha em texto plano
- **NÃO precisa** enviar o hash BCrypt
- O Spring Security:
  1. Recebe email e senha
  2. Busca o usuário no banco pelo email
  3. Compara a senha enviada com o hash armazenado usando `BCryptPasswordEncoder.matches()`
  4. Se corresponder, autentica o usuário

### Conclusão:
O usuário autentica com **email/senha** (texto plano), não com hash. O sistema compara automaticamente a senha com o hash armazenado.

---

## Comparação: Senha vs Hash

### No Login (POST /auth/login):
```json
{
  "email": "admin@fmp.br",
  "senha": "123456"  ← Texto plano
}
```

### No Banco de Dados (tabela `users`):
```
email: admin@fmp.br
senha_hash: $2a$10$abcdefghijklmnopqrstuvwxyz...  ← Hash BCrypt (60 chars)
```

### Processo de Autenticação:
1. Cliente envia: `email` + `senha` (texto plano)
2. Sistema busca usuário por `email`
3. Sistema compara `senha` com `senha_hash` usando BCrypt
4. Se corresponder → Autenticação bem-sucedida
5. Sistema retorna JWT token

---

## Resumo dos Testes no Postman

### Teste 9: Gerar Hash BCrypt
- **URL**: `GET /admin/generate-hash?senha=123456`
- **Sem autenticação** (público)
- **Mostra**: Como a senha `123456` vira um hash BCrypt

### Teste 10: Listar Usuários
- **URL**: `GET /admin/users`
- **Com autenticação**: `Authorization: Bearer {{jwt}}`
- **Requer**: Role `ADMIN`
- **Mostra**: Senhas no banco estão criptografadas

### Teste 1: Login
- **URL**: `POST /auth/login`
- **Body**: `{"email": "admin@fmp.br", "senha": "123456"}`
- **Mostra**: Autenticação funciona com email/senha (não com hash)

---

## Evidências de Criptografia BCrypt

1. **Formato do Hash**: Começa com `$2a$` ou `$2b$`
2. **Tamanho**: Sempre 60 caracteres
3. **Senha Original**: Diferente do hash (não pode ser revertido)
4. **Mesma Senha, Hash Diferente**: Cada vez que você gera um hash para `123456`, o resultado é diferente (mas ambos funcionam para autenticação)

---

## Teste Adicional: Verificar que Mesma Senha Gera Hash Diferente

Execute o teste 9 duas vezes com a mesma senha:
- Primeira vez: `GET /admin/generate-hash?senha=123456`
- Segunda vez: `GET /admin/generate-hash?senha=123456`

**Resultado**: Os hashes serão diferentes, mas ambos funcionam para autenticar com a senha `123456`.

Isso é uma característica do BCrypt: cada hash inclui um "salt" aleatório, tornando impossível descobrir se duas senhas são iguais apenas comparando os hashes.

---

## Pronto!

Agora você pode demonstrar no Postman que:
- ✅ Senhas estão criptografadas (BCrypt)
- ✅ Usuário autentica com email/senha (não com hash)
- ✅ Sistema compara senha com hash automaticamente


