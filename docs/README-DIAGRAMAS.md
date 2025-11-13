# Diagramas do Sistema

Este diretório contém diagramas da arquitetura e fluxos do sistema.

## 📊 Diagramas Disponíveis

### 1. Estrutura e Fluxo do Token JWT
**Arquivo:** `diagrama-jwt-fluxo.md`

Contém:
- Fluxo completo de autenticação JWT
- Estrutura do token JWT
- Validação do token
- Componentes do sistema de segurança

### 2. Arquitetura MVC do Sistema
**Arquivo:** `diagrama-arquitetura-mvc.md`

Contém:
- Visão geral da arquitetura
- Fluxo de requisição completo
- Camadas da arquitetura MVC
- Fluxo de dados (CRUD)
- Estrutura de pacotes
- Responsabilidades por camada

### 3. Fluxo Básico de Autenticação
**Arquivo:** `diagrama-autenticacao-basico.md`

Contém:
- Fluxo simples de login e uso do token
- Fluxo visual simplificado
- Componentes básicos
- Passo a passo do fluxo
- Comparação com e sem token
- Exemplo prático: Login e Listar Alunos
- Resumo em 3 passos
- Estados do token

### 4. Fluxo de Autenticação e Segurança com Spring Security
**Arquivo:** `diagrama-spring-security.md`

Contém:
- Visão geral do Spring Security
- Fluxo completo de autenticação
- Filtros do Spring Security (Filter Chain)
- JwtAuthFilter detalhado
- SecurityConfig e configurações
- AuthenticationManager e Providers
- SecurityContext e Thread-Local Storage
- Fluxo de autorização
- Exception Handling
- CORS Configuration
- Comparação Autenticação vs Autorização
- Resumo do fluxo completo

## 🖼️ Como Visualizar os Diagramas

### Opção 1: GitHub (Recomendado)
Os diagramas Mermaid são renderizados automaticamente no GitHub quando você visualiza os arquivos `.md`.

1. Acesse o repositório no GitHub
2. Navegue até a pasta `docs/`
3. Clique nos arquivos `.md`
4. Os diagramas serão exibidos automaticamente

### Opção 2: Visual Studio Code
1. Instale a extensão "Markdown Preview Mermaid Support"
2. Abra o arquivo `.md`
3. Use `Cmd+Shift+V` (Mac) ou `Ctrl+Shift+V` (Windows/Linux) para visualizar

### Opção 3: Online (Mermaid Live Editor)
1. Acesse: https://mermaid.live/
2. Copie o código do diagrama (entre ```mermaid e ```)
3. Cole no editor
4. Exporte como PNG ou SVG

### Opção 4: Gerar Imagens
Use ferramentas como:
- **Mermaid CLI**: `npm install -g @mermaid-js/mermaid-cli`
- **Pandoc**: Converte markdown para PDF/HTML
- **Typora**: Editor markdown com suporte a Mermaid

## 📝 Exemplo de Uso

Para incluir os diagramas na documentação do projeto, adicione referências no README principal:

```markdown
## Arquitetura

Veja os diagramas detalhados em [docs/diagrama-arquitetura-mvc.md](docs/diagrama-arquitetura-mvc.md)

## Autenticação JWT

Veja o fluxo completo em [docs/diagrama-jwt-fluxo.md](docs/diagrama-jwt-fluxo.md)

## Spring Security

Veja o fluxo detalhado de autenticação e segurança em [docs/diagrama-spring-security.md](docs/diagrama-spring-security.md)
```

## 🔧 Gerar Imagens PNG/SVG

### Usando Mermaid CLI

```bash
# Instalar
npm install -g @mermaid-js/mermaid-cli

# Gerar PNG
mmdc -i docs/diagrama-jwt-fluxo.md -o docs/jwt-fluxo.png

# Gerar SVG
mmdc -i docs/diagrama-arquitetura-mvc.md -o docs/arquitetura-mvc.svg
```

### Usando Docker

```bash
docker run --rm -v $(pwd)/docs:/data minlag/mermaid-cli \
  -i /data/diagrama-jwt-fluxo.md -o /data/jwt-fluxo.png
```



