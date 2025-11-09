# Estrutura e Fluxo do Token JWT

## Diagrama de Fluxo

```mermaid
sequenceDiagram
    participant Cliente
    participant AuthController
    participant AuthenticationManager
    participant UserDetailsService
    participant PasswordEncoder
    participant JwtUtil
    participant SecurityFilterChain
    participant AlunoController

    Note over Cliente,AlunoController: 1. PROCESSO DE LOGIN E AUTENTICAÇÃO

    Cliente->>AuthController: POST /auth/login<br/>{email, senha}
    AuthController->>AuthenticationManager: authenticate(email, senha)
    AuthenticationManager->>UserDetailsService: loadUserByUsername(email)
    UserDetailsService->>UserDetailsService: Busca usuário no banco
    UserDetailsService-->>AuthenticationManager: UserDetails (email, senhaHash, roles)
    AuthenticationManager->>PasswordEncoder: matches(senha, senhaHash)
    PasswordEncoder-->>AuthenticationManager: true/false
    AuthenticationManager-->>AuthController: Authentication (sucesso)
    AuthController->>JwtUtil: generateToken(email)
    JwtUtil->>JwtUtil: Cria JWT com:<br/>- Subject: email<br/>- IssuedAt: agora<br/>- Expiration: agora + 3600s<br/>- Signature: HS256
    JwtUtil-->>AuthController: Token JWT (string)
    AuthController-->>Cliente: {type: "Bearer", token: "...", expiresIn: 3600}

    Note over Cliente,AlunoController: 2. USO DO TOKEN EM REQUISIÇÕES

    Cliente->>AlunoController: GET /aluno<br/>Header: Authorization: Bearer {token}
    AlunoController->>SecurityFilterChain: Requisição interceptada
    SecurityFilterChain->>JwtAuthFilter: doFilterInternal()
    JwtAuthFilter->>JwtAuthFilter: Extrai token do header
    JwtAuthFilter->>JwtUtil: isValid(token)
    JwtUtil->>JwtUtil: Verifica:<br/>- Assinatura válida<br/>- Não expirado<br/>- Formato correto
    JwtUtil-->>JwtAuthFilter: true
    JwtAuthFilter->>JwtUtil: getSubject(token)
    JwtUtil-->>JwtAuthFilter: email
    JwtAuthFilter->>UserDetailsService: loadUserByUsername(email)
    UserDetailsService-->>JwtAuthFilter: UserDetails
    JwtAuthFilter->>SecurityContextHolder: setAuthentication()
    JwtAuthFilter-->>SecurityFilterChain: Continua filtro
    SecurityFilterChain-->>AlunoController: Requisição autorizada
    AlunoController-->>Cliente: Lista de alunos (JSON)
```

## Estrutura do Token JWT

```mermaid
graph TB
    subgraph "JWT Token Structure"
        JWT[JWT Token]
        JWT --> Header[Header]
        JWT --> Payload[Payload]
        JWT --> Signature[Signature]
        
        Header --> H1["alg: HS256<br/>typ: JWT"]
        Payload --> P1["sub: email<br/>iat: timestamp<br/>exp: timestamp"]
        Signature --> S1["HMACSHA256(<br/>base64UrlEncode(header) + '.' +<br/>base64UrlEncode(payload),<br/>secret_key)"]
    end
    
    subgraph "Token Components"
        TokenString["eyJhbGciOiJIUzI1NiJ9.<br/>eyJzdWIiOiJhZG1pbkBmbXAuYnIiLCJpYXQiOjE2Nz...<br/>.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"]
        TokenString --> Part1[Parte 1: Header<br/>Base64URL Encoded]
        TokenString --> Part2[Parte 2: Payload<br/>Base64URL Encoded]
        TokenString --> Part3[Parte 3: Signature<br/>HMAC SHA256]
    end
    
    style JWT fill:#e1f5ff
    style Header fill:#fff4e1
    style Payload fill:#fff4e1
    style Signature fill:#ffe1e1
```

## Fluxo de Validação

```mermaid
flowchart TD
    Start([Cliente envia requisição]) --> HasToken{Header Authorization<br/>presente?}
    HasToken -->|Não| Reject1[❌ 401 Unauthorized]
    HasToken -->|Sim| ExtractToken[Extrair token do header<br/>Bearer {token}]
    ExtractToken --> ValidFormat{Formato válido?<br/>3 partes separadas por '.'}
    ValidFormat -->|Não| Reject2[❌ Token inválido]
    ValidFormat -->|Sim| DecodeHeader[Decodificar Header<br/>Base64URL]
    DecodeHeader --> CheckAlg{Algoritmo = HS256?}
    CheckAlg -->|Não| Reject3[❌ Algoritmo inválido]
    CheckAlg -->|Sim| DecodePayload[Decodificar Payload<br/>Base64URL]
    DecodePayload --> CheckExp{Token expirado?<br/>exp < agora}
    CheckExp -->|Sim| Reject4[❌ Token expirado]
    CheckExp -->|Não| VerifySignature[Verificar assinatura<br/>HMAC SHA256]
    VerifySignature --> SigValid{Assinatura válida?}
    SigValid -->|Não| Reject5[❌ Assinatura inválida]
    SigValid -->|Sim| ExtractEmail[Extrair email do<br/>subject do payload]
    ExtractEmail --> LoadUser[Carregar UserDetails<br/>do banco]
    LoadUser --> SetAuth[Definir Authentication<br/>no SecurityContext]
    SetAuth --> Allow[✅ Requisição autorizada]
    Allow --> Process[Processar requisição]
    
    style Reject1 fill:#ffcccc
    style Reject2 fill:#ffcccc
    style Reject3 fill:#ffcccc
    style Reject4 fill:#ffcccc
    style Reject5 fill:#ffcccc
    style Allow fill:#ccffcc
    style Process fill:#ccffcc
```

## Componentes do Sistema JWT

```mermaid
graph LR
    subgraph "Spring Security"
        SC[SecurityConfig]
        JAF[JwtAuthFilter]
        AM[AuthenticationManager]
        UDS[UserDetailsService]
        PE[PasswordEncoder]
    end
    
    subgraph "JWT"
        JU[JwtUtil]
        AC[AuthController]
    end
    
    subgraph "Banco de Dados"
        DB[(MySQL)]
        Users[users table]
    end
    
    subgraph "Controllers"
        AC2[AlunoController]
        HC[HealthController]
    end
    
    SC --> JAF
    SC --> AM
    AM --> UDS
    AM --> PE
    UDS --> DB
    DB --> Users
    
    AC --> AM
    AC --> JU
    JAF --> JU
    JAF --> UDS
    
    JAF --> AC2
    JAF --> HC
    
    style SC fill:#e1f5ff
    style JU fill:#fff4e1
    style AC fill:#ffe1e1
    style DB fill:#e1ffe1
```

## Configuração de Segurança

```mermaid
graph TB
    subgraph "SecurityConfig"
        SC[SecurityFilterChain]
        SC --> CSRF[CSRF: Disabled]
        SC --> CORS[CORS: Enabled<br/>localhost:3000]
        SC --> Auth[Authorization Rules]
        SC --> Session[Session: STATELESS]
        SC --> EntryPoint[AuthenticationEntryPoint<br/>401 JSON]
    end
    
    Auth --> Public["/health, /auth/login<br/>/admin/generate-hash<br/>→ permitAll()"]
    Auth --> Protected["/aluno/**<br/>→ authenticated()"]
    Auth --> Admin["/admin/**<br/>→ hasRole('ADMIN')"]
    
    SC --> JWTFilter[JwtAuthFilter<br/>Before UsernamePasswordFilter]
    
    style SC fill:#e1f5ff
    style Public fill:#ccffcc
    style Protected fill:#fff4e1
    style Admin fill:#ffe1e1
```

