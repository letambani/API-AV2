# Guia de Teste - Frontend (localhost:3000)

## Como Testar o Login no Frontend

### Passo 1: Iniciar o Servidor Frontend

```bash
cd av2-api
node servidor-teste-frontend.js
```

Você verá:
```
Servidor de teste rodando em http://localhost:3000
Abra no navegador: http://localhost:3000
```

### Passo 2: Abrir no Navegador

Abra o navegador e acesse:
```
http://localhost:3000
```

### Passo 3: Fazer Login

1. O formulário já vem preenchido:
   - Email: `admin@fmp.br`
   - Senha: `123456`

2. Clique em **"Login"**

3. Resultado esperado:
   - Mensagem de sucesso verde
   - Token exibido (primeiros 80 caracteres)
   - Lista de alunos carregada automaticamente

### Passo 4: Verificar Funcionamento

Após o login bem-sucedido, você deve ver:

1. **Mensagem de Sucesso:**
   - "Login realizado com sucesso!"
   - Tipo: Bearer
   - Expira em: 3600 segundos

2. **Token:**
   - Token JWT exibido (primeiros 80 caracteres)

3. **Lista de Alunos:**
   - Alunos cadastrados no banco
   - Cada aluno mostra: ID, Nome, Idade, CPF

## Verificar CORS

O frontend está configurado para:
- Fazer requisições para `http://localhost:8081`
- Enviar `credentials: 'include'` para CORS
- Usar header `Authorization: Bearer <token>`

Se houver erro CORS no console do navegador:
1. Verifique se a API está rodando em `http://localhost:8081`
2. Verifique se o CORS está configurado no backend

## Testar no Console do Navegador

Abra o DevTools (F12) e vá na aba Console. Execute:

```javascript
// Teste de login
fetch('http://localhost:8081/auth/login', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    credentials: 'include',
    body: JSON.stringify({email: 'admin@fmp.br', senha: '123456'})
})
.then(r => r.json())
.then(data => {
    console.log('Login OK:', data);
    return data.token;
})
.then(token => {
    // Testar endpoint protegido
    return fetch('http://localhost:8081/aluno', {
        headers: {'Authorization': 'Bearer ' + token},
        credentials: 'include'
    });
})
.then(r => r.json())
.then(alunos => console.log('Alunos:', alunos))
.catch(err => console.error('Erro:', err));
```

## Troubleshooting

### Erro: "Failed to fetch"
- **Causa:** API não está rodando
- **Solução:** Inicie a API com `./mvnw spring-boot:run`

### Erro: "CORS policy blocked"
- **Causa:** CORS não configurado ou origem incorreta
- **Solução:** Verifique se `CorsConfig` permite `http://localhost:3000`

### Erro: "401 Unauthorized"
- **Causa:** Token não está sendo enviado ou está inválido
- **Solução:** Faça login novamente

### Erro: "Erro ao ler arquivo"
- **Causa:** Servidor frontend não encontrou o arquivo HTML
- **Solução:** Verifique se `teste-frontend.html` está no mesmo diretório do servidor

## Checklist de Teste

- [ ] API rodando em `http://localhost:8081`
- [ ] Servidor frontend rodando em `http://localhost:3000`
- [ ] Navegador aberto em `http://localhost:3000`
- [ ] Login funciona (POST /auth/login)
- [ ] Token recebido e salvo
- [ ] Alunos listados (GET /aluno)
- [ ] Sem erros CORS no console
- [ ] Dados exibidos corretamente

## Pronto!

Agora você pode testar o login no frontend em `http://localhost:3000`


