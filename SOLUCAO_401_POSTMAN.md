# Solução: Teste 2 retorna 401 Unauthorized

## Problema
O teste "2. GET /aluno - Listar com Token" está retornando 401, mesmo que o teste 1 tenha funcionado.

## Causa
O token não está sendo enviado no header Authorization porque:
- O environment não está criado/selecionado, OU
- A variável {{jwt}} não está sendo resolvida

## Soluções

### Solução 1: Criar e Selecionar Environment (Recomendado)

1. **Criar Environment:**
   - Clique no ícone de "Environments" (⚙️) no canto superior direito
   - Clique em "+" para criar novo
   - Nome: `AV2-Local`
   - Adicione variável:
     - **Variable:** `jwt`
     - **Initial Value:** (deixe vazio)
     - **Current Value:** (deixe vazio)
   - Clique em "Save"

2. **Selecionar Environment:**
   - No dropdown (canto superior direito), selecione "AV2-Local"

3. **Executar Teste 1:**
   - Execute "1. Login - Obter JWT"
   - O token será salvo automaticamente na variável `jwt`

4. **Verificar Token:**
   - Abra o environment "AV2-Local"
   - Verifique se a variável `jwt` tem um valor (token longo)
   - Se estiver vazia, execute o teste 1 novamente

5. **Executar Teste 2:**
   - Agora deve funcionar e retornar 200 OK

### Solução 2: Usar Collection Variables (Alternativa)

A coleção também salva o token em collection variables como fallback.

1. Execute o teste 1 (Login)
2. O token será salvo automaticamente
3. Execute o teste 2

### Solução 3: Verificar Header Manualmente

1. Abra o teste "2. GET /aluno - Listar com Token"
2. Vá na aba "Headers"
3. Verifique se tem: `Authorization: Bearer {{jwt}}`
4. Se não tiver, adicione manualmente:
   - Key: `Authorization`
   - Value: `Bearer {{jwt}}`

### Solução 4: Copiar Token Manualmente

Se nada funcionar:

1. Execute o teste 1 (Login)
2. Copie o token da resposta JSON
3. No teste 2, vá em "Headers"
4. Altere o valor de `Bearer {{jwt}}` para `Bearer <cole_o_token_aqui>`
5. Execute o teste

## Verificar se está funcionando

Após executar o teste 2, você deve ver:
- Status: **200 OK**
- Body: Array de alunos em JSON
- Testes passando (verde)

## Ordem Correta

SEMPRE execute na ordem:
1. **1. Login - Obter JWT** → PRIMEIRO!
2. **2. GET /aluno - Listar com Token** → Deve retornar 200
3. Depois os outros testes...

