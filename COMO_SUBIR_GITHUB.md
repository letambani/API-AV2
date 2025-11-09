# Como Subir o Projeto para o GitHub

## Passo a Passo

### 1. Criar Repositório no GitHub

1. Acesse: https://github.com
2. Clique no botão **"+"** no canto superior direito
3. Selecione **"New repository"**
4. Preencha:
   - **Repository name**: `av2-springboot-api` (ou o nome que preferir)
   - **Description**: "API REST Spring Boot com JWT para AV2"
   - **Visibility**: Escolha **Public** ou **Private**
   - **NÃO marque** "Initialize this repository with a README" (já temos um)
5. Clique em **"Create repository"**

### 2. Conectar Repositório Local ao GitHub

Após criar o repositório no GitHub, você verá uma página com instruções. Execute os comandos abaixo no terminal:

```bash
cd /Users/leticiatambani/Documents/API/av2-api

# Adicionar o repositório remoto (substitua SEU_USUARIO pelo seu username do GitHub)
git remote add origin https://github.com/SEU_USUARIO/av2-springboot-api.git

# Ou se preferir usar SSH:
# git remote add origin git@github.com:SEU_USUARIO/av2-springboot-api.git

# Verificar se foi adicionado corretamente
git remote -v
```

### 3. Fazer Push para o GitHub

```bash
# Renomear branch principal para 'main' (se necessário)
git branch -M main

# Fazer push do código
git push -u origin main
```

Se for a primeira vez usando Git, pode pedir suas credenciais do GitHub.

### 4. Verificar no GitHub

1. Acesse seu repositório no GitHub
2. Você deve ver todos os arquivos do projeto
3. O README.md será exibido automaticamente na página principal

## 🔐 Autenticação no GitHub

### Opção 1: Personal Access Token (Recomendado)

1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token (classic)
3. Dê um nome e selecione escopos: `repo` (acesso completo a repositórios)
4. Copie o token gerado
5. Use o token como senha quando o Git pedir credenciais

### Opção 2: GitHub CLI

```bash
# Instalar GitHub CLI (se não tiver)
brew install gh

# Autenticar
gh auth login
```

### Opção 3: SSH Keys

1. Gerar chave SSH:
   ```bash
   ssh-keygen -t ed25519 -C "seu_email@example.com"
   ```

2. Adicionar chave ao GitHub:
   - Copie o conteúdo de `~/.ssh/id_ed25519.pub`
   - GitHub → Settings → SSH and GPG keys → New SSH key
   - Cole a chave pública

## 📝 Comandos Úteis

### Verificar status
```bash
git status
```

### Adicionar mudanças
```bash
git add .
```

### Fazer commit
```bash
git commit -m "Descrição das mudanças"
```

### Fazer push
```bash
git push
```

### Ver histórico
```bash
git log --oneline
```

## ⚠️ Arquivos que NÃO serão enviados

O arquivo `.gitignore` já está configurado para ignorar:
- Arquivos compilados (`target/`)
- Logs (`*.log`)
- Configurações locais
- Arquivos temporários
- Node modules (se houver)

## 🎯 Próximos Passos

Após subir o projeto:
1. Adicione uma descrição no repositório
2. Configure as **Topics** (tags): `spring-boot`, `java`, `jwt`, `rest-api`
3. Adicione uma **License** se desejar
4. Configure **Actions** para CI/CD (opcional)

