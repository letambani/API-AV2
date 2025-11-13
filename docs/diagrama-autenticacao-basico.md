# Fluxo Básico de Autenticação

## Fluxo Simples de Login e Uso do Token

```mermaid
sequenceDiagram
    participant U as Usuário
    participant F as Frontend
    participant API as API Spring Boot
    participant DB as Banco de Dados

    Note over U,DB: 1. PROCESSO DE LOGIN

    U->>F: Digita email e senha
    F->>API: POST /auth/login<br/>{email, senha}
    API->>DB: Busca usuário pelo email
    DB-->>API: Retorna usuário (senhaHash)
    API->>API: Compara senha com BCrypt
    alt Senha correta
        API->>API: Gera token JWT
        API-->>F: 200 OK<br/>{token: "eyJhbGc..."}
        F->>F: Salva token no localStorage
        F-->>U: Login realizado com sucesso
    else Senha incorreta
        API-->>F: 401 Unauthorized<br/>{message: "Credenciais inválidas"}
        F-->>U: Erro: Credenciais inválidas
    end

    Note over U,DB: 2. USO DO TOKEN

    U->>F: Solicita lista de alunos
    F->>F: Recupera token do localStorage
    F->>API: GET /aluno<br/>Header: Authorization: Bearer {token}
    API->>API: Valida token JWT
    alt Token válido
        API->>DB: Busca alunos
        DB-->>API: Lista de alunos
        API-->>F: 200 OK<br/>[{id, nome, idade, cpf}, ...]
        F-->>U: Exibe lista de alunos
    else Token inválido/expirado
        API-->>F: 401 Unauthorized<br/>{message: "Token inválido"}
        F->>F: Remove token do localStorage
        F-->>U: Erro: Faça login novamente
    end
```

## Fluxo Visual Simplificado

```mermaid
flowchart TD
    Start([Usuário acessa sistema]) --> Login[Login: Email + Senha]
    Login --> Validate{Valida<br/>credenciais}
    
    Validate -->|Senha incorreta| Error1[❌ 401 Unauthorized<br/>Credenciais inválidas]
    Validate -->|Senha correta| Generate[Gera Token JWT]
    
    Generate --> Save[Salva token no<br/>localStorage/Frontend]
    Save --> Success[✅ Login realizado]
    
    Success --> Request[Faz requisição<br/>com token]
    Request --> CheckToken{Token<br/>válido?}
    
    CheckToken -->|Token expirado| Error2[❌ 401 Unauthorized<br/>Token expirado]
    CheckToken -->|Token inválido| Error3[❌ 401 Unauthorized<br/>Token inválido]
    CheckToken -->|Token válido| Authorize[✅ Requisição autorizada]
    
    Authorize --> Process[Processa requisição]
    Process --> Response[Retorna dados]
    Response --> End([Usuário recebe dados])
    
    Error1 --> Login
    Error2 --> Login
    Error3 --> Login
    
    style Generate fill:#fff4e1
    style Success fill:#ccffcc
    style Authorize fill:#ccffcc
    style Error1 fill:#ffcccc
    style Error2 fill:#ffcccc
    style Error3 fill:#ffcccc
```

## Componentes Básicos

```mermaid
graph LR
    subgraph "1. LOGIN"
        L1[Usuário] --> L2[AuthController]
        L2 --> L3[AuthenticationManager]
        L3 --> L4[UserDetailsService]
        L4 --> L5[PasswordEncoder]
        L5 --> L6[JwtUtil]
        L6 --> L7[Token JWT]
    end
    
    subgraph "2. AUTENTICAÇÃO"
        A1[Requisição com Token] --> A2[JwtAuthFilter]
        A2 --> A3[Valida Token]
        A3 --> A4[SecurityContext]
        A4 --> A5[Controller]
    end
    
    subgraph "3. AUTORIZAÇÃO"
        A5 --> AZ1{Verifica<br/>permissões}
        AZ1 -->|Permitido| AZ2[✅ Processa]
        AZ1 -->|Negado| AZ3[❌ 403 Forbidden]
    end
    
    style L7 fill:#fff4e1
    style A4 fill:#ffe1e1
    style AZ2 fill:#ccffcc
    style AZ3 fill:#ffcccc
```

## Passo a Passo do Fluxo

```mermaid
graph TB
    subgraph "ETAPA 1: LOGIN"
        S1[1. Usuário envia<br/>email e senha] --> S2[2. API busca usuário<br/>no banco de dados]
        S2 --> S3[3. Compara senha<br/>com BCrypt]
        S3 --> S4[4. Gera token JWT<br/>com email e expiração]
        S4 --> S5[5. Retorna token<br/>para o frontend]
    end
    
    subgraph "ETAPA 2: ARMAZENAMENTO"
        S5 --> S6[6. Frontend salva<br/>token no localStorage]
    end
    
    subgraph "ETAPA 3: REQUISIÇÃO"
        S6 --> S7[7. Frontend envia<br/>requisição com token<br/>no header Authorization]
        S7 --> S8[8. JwtAuthFilter<br/>intercepta requisição]
        S8 --> S9[9. Extrai e valida<br/>token JWT]
    end
    
    subgraph "ETAPA 4: AUTENTICAÇÃO"
        S9 --> S10{Token<br/>válido?}
        S10 -->|Sim| S11[10. Carrega UserDetails<br/>do banco]
        S10 -->|Não| S12[❌ Retorna 401]
        S11 --> S13[11. Define Authentication<br/>no SecurityContext]
    end
    
    subgraph "ETAPA 5: AUTORIZAÇÃO"
        S13 --> S14[12. Verifica permissões<br/>SecurityConfig]
        S14 --> S15{Autorizado?}
        S15 -->|Sim| S16[13. Processa requisição<br/>no Controller]
        S15 -->|Não| S17[❌ Retorna 403]
        S16 --> S18[14. Retorna dados<br/>para o frontend]
    end
    
    style S4 fill:#fff4e1
    style S9 fill:#ffe1e1
    style S13 fill:#e1ffe1
    style S16 fill:#ccffcc
    style S12 fill:#ffcccc
    style S17 fill:#ffcccc
```

## Comparação: Com e Sem Token

```mermaid
graph TB
    subgraph "SEM TOKEN (Não Autenticado)"
        NS1[Requisição] --> NS2[❌ 401 Unauthorized<br/>Token necessário]
    end
    
    subgraph "COM TOKEN (Autenticado)"
        CS1[Requisição + Token] --> CS2[Valida Token]
        CS2 --> CS3{Token válido?}
        CS3 -->|Sim| CS4[✅ 200 OK<br/>Dados retornados]
        CS3 -->|Não| CS5[❌ 401 Unauthorized<br/>Token inválido]
    end
    
    style NS2 fill:#ffcccc
    style CS4 fill:#ccffcc
    style CS5 fill:#ffcccc
```

## Exemplo Prático: Login e Listar Alunos

```mermaid
sequenceDiagram
    participant U as Usuário
    participant F as Frontend<br/>localhost:3000
    participant API as API<br/>localhost:8081
    participant DB as MySQL

    Note over U,DB: LOGIN

    U->>F: Clica em "Login"
    F->>API: POST /auth/login<br/>Body: {email: "admin@fmp.br", senha: "123456"}
    API->>DB: SELECT * FROM users WHERE email = 'admin@fmp.br'
    DB-->>API: UserAccount (senhaHash: "$2a$10$...")
    API->>API: BCrypt.matches("123456", senhaHash)
    API->>API: Gera JWT token
    API-->>F: {token: "eyJhbGciOiJIUzI1NiJ9..."}
    F->>F: localStorage.setItem("jwt", token)
    F-->>U: ✅ Login realizado!

    Note over U,DB: LISTAR ALUNOS

    U->>F: Clica em "Listar Alunos"
    F->>F: token = localStorage.getItem("jwt")
    F->>API: GET /aluno<br/>Header: Authorization: Bearer {token}
    API->>API: JwtAuthFilter valida token
    API->>API: Token válido, extrai email
    API->>DB: SELECT * FROM aluno
    DB-->>API: Lista de alunos
    API-->>F: [{id: 1, nome: "João", ...}, ...]
    F-->>U: ✅ Lista de alunos exibida!
```

## Resumo em 3 Passos

```mermaid
graph LR
    subgraph "1️⃣ LOGIN"
        L[Email + Senha] --> T[Token JWT]
    end
    
    subgraph "2️⃣ ARMAZENAR"
        T --> S[Salvar Token]
    end
    
    subgraph "3️⃣ USAR"
        S --> R[Enviar Token<br/>em cada requisição]
    end
    
    style T fill:#fff4e1
    style S fill:#ffe1e1
    style R fill:#e1ffe1
```

## Estados do Token

```mermaid
stateDiagram-v2
    [*] --> SemToken: Usuário não logado
    SemToken --> ComToken: Login realizado
    ComToken --> ComToken: Requisições com token válido
    ComToken --> TokenExpirado: Token expirou
    TokenExpirado --> SemToken: Fazer login novamente
    ComToken --> TokenInvalido: Token inválido
    TokenInvalido --> SemToken: Fazer login novamente
    
    note right of ComToken
        Token válido e não expirado
        Requisições funcionam normalmente
    end note
    
    note right of TokenExpirado
        Token passou do tempo de expiração
        Precisa fazer login novamente
    end note
```

