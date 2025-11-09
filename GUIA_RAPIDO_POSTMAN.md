# Guia Rápido - Postman AV2

## Problema: Teste 2 retorna 401

Se o teste "2. GET /aluno - Listar com Token" está retornando 401, significa que o token não está sendo enviado.

## Solução Rápida:

### 1. Verificar se o Environment está selecionado
- No canto superior direito do Postman, verifique se há um environment selecionado
- Se não houver, crie um:
  - Clique no ícone de "Environments" (⚙️)
  - Clique em "+" para criar novo
  - Nome: `AV2-Local`
  - Adicione variável: `jwt` (deixe vazio)
  - Clique em "Save"
  - Selecione "AV2-Local" no dropdown

### 2. Executar o teste 1 PRIMEIRO
- Execute "1. Login - Obter JWT"
- Verifique se retornou 200 OK
- Verifique no console (View → Show Postman Console) se o token foi salvo

### 3. Verificar se o token foi salvo
- Abra o environment "AV2-Local"
- Verifique se a variável `jwt` tem um valor (deve ser um token longo)
- Se estiver vazia, execute o teste 1 novamente

### 4. Verificar o header do teste 2
- Abra o teste "2. GET /aluno - Listar com Token"
- Vá na aba "Headers"
- Deve ter: `Authorization: Bearer {{jwt}}`
- O `{{jwt}}` será substituído automaticamente pelo valor da variável

## Ordem Correta de Execução:

1. **1. Login - Obter JWT** → Execute PRIMEIRO!
2. **2. GET /aluno - Listar com Token** → Deve retornar 200 OK
3. **3. GET /aluno - Sem Token** → Deve retornar 401
4. **4. POST /aluno - Criar** → Deve retornar 201
5. **5. POST /aluno - CPF Duplicado** → Deve retornar 409
6. **6. GET /aluno/999** → Deve retornar 404
7. **7. POST /auth/login - Credenciais Erradas** → Deve retornar 401
8. **8. CORS Test** → Deve retornar 200 com headers CORS

## Verificar Token Manualmente:

Se o token não estiver sendo salvo automaticamente:

1. Execute o teste 1 (Login)
2. Copie o token da resposta JSON
3. Vá em Environments → AV2-Local
4. Cole o token na variável `jwt`
5. Salve
6. Execute o teste 2 novamente

## Troubleshooting:

- **401 em todos os testes protegidos**: Token não está sendo enviado ou está inválido
- **Token não é salvo**: Verifique se o script do teste 1 está executando
- **Variável {{jwt}} não funciona**: Verifique se o environment está selecionado

