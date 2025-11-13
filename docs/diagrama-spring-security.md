# Fluxo de Autenticação e Segurança com Spring Security

## Visão Geral do Spring Security

```mermaid
graph TB
    subgraph "Cliente"
        C[Requisição HTTP]
    end
    
    subgraph "Spring Security Filter Chain"
        SC[SecurityFilterChain]
        SC --> F1[SecurityContextPersistenceFilter]
        F1 --> F2[HeaderWriterFilter]
        F2 --> F3[CorsFilter]
        F3 --> F4[CsrfFilter]
        F4 --> F5[LogoutFilter]
        F5 --> F6[JwtAuthFilter - Custom]
        F6 --> F7[UsernamePasswordAuthenticationFilter]
        F7 --> F8[ExceptionTranslationFilter]
        F8 --> F9[FilterSecurityInterceptor]
    end
    
    subgraph "Authentication Manager"
        AM[AuthenticationManager]
        AM --> AP[DaoAuthenticationProvider]
        AP --> UDS[UserDetailsService]
        AP --> PE[PasswordEncoder]
    end
    
    subgraph "Security Context"
        SEC[SecurityContextHolder]
        SEC --> AUTH[Authentication Object]
    end
    
    subgraph "Authorization"
        AUTHZ[Authorization Manager]
        AUTHZ --> ROLES[Role-Based Access Control]
    end
    
    subgraph "Application"
        APP[Controllers]
    end
    
    C --> SC
    F6 --> AM
    AM --> SEC
    SEC --> AUTHZ
    AUTHZ --> APP
    
    style SC fill:#e1f5ff
    style AM fill:#fff4e1
    style SEC fill:#ffe1e1
    style AUTHZ fill:#e1ffe1
```

## Fluxo Completo de Autenticação

```mermaid
sequenceDiagram
    participant C as Cliente
    participant JF as JwtAuthFilter
    participant SC as SecurityContext
    participant AM as AuthenticationManager
    participant UDS as UserDetailsService
    participant DB as Database
    participant JU as JwtUtil
    participant AC as AuthController
    participant PE as PasswordEncoder

    Note over C,PE: 1. REQUISIÇÃO SEM TOKEN (Primeira vez)

    C->>JF: GET /aluno<br/>(sem Authorization header)
    JF->>JF: Extrai token do header
    JF->>JF: Token não encontrado
    JF->>SC: SecurityContext vazio
    JF->>C: Continua filtro (sem autenticação)
    Note over C,PE: SecurityFilterChain verifica autorização
    Note over C,PE: Requisição requer autenticação
    C-->>C: 401 Unauthorized<br/>{status: 401, message: "Token necessário"}

    Note over C,PE: 2. PROCESSO DE LOGIN

    C->>AC: POST /auth/login<br/>{email: "admin@fmp.br", senha: "123456"}
    AC->>AM: authenticate(UsernamePasswordAuthenticationToken)
    AM->>UDS: loadUserByUsername(email)
    UDS->>DB: SELECT * FROM users WHERE email = ?
    DB-->>UDS: UserAccount (id, email, senhaHash, role)
    UDS->>UDS: Cria UserDetails<br/>(email, senhaHash, authorities)
    UDS-->>AM: UserDetails
    AM->>PE: matches(senha, senhaHash)
    PE->>PE: BCrypt.compare()
    PE-->>AM: true (senha válida)
    AM->>AM: Cria Authentication<br/>(principal, credentials, authorities)
    AM-->>AC: Authentication (autenticado)
    AC->>JU: generateToken(email)
    JU->>JU: Cria JWT:<br/>Header: {alg: HS256, typ: JWT}<br/>Payload: {sub: email, iat, exp}<br/>Signature: HMAC SHA256
    JU-->>AC: Token JWT (string)
    AC-->>C: 200 OK<br/>{type: "Bearer", token: "...", expiresIn: 3600}

    Note over C,PE: 3. REQUISIÇÃO COM TOKEN

    C->>JF: GET /aluno<br/>Authorization: Bearer {token}
    JF->>JF: Extrai token do header<br/>"Bearer {token}" → token
    JF->>JU: isValid(token)
    JU->>JU: Verifica:<br/>- Formato (3 partes)<br/>- Algoritmo (HS256)<br/>- Assinatura<br/>- Expiração
    JU-->>JF: true (token válido)
    JF->>JU: getSubject(token)
    JU-->>JF: email ("admin@fmp.br")
    JF->>UDS: loadUserByUsername(email)
    UDS->>DB: SELECT * FROM users WHERE email = ?
    DB-->>UDS: UserAccount
    UDS-->>JF: UserDetails
    JF->>JF: Cria Authentication<br/>UsernamePasswordAuthenticationToken<br/>(principal, null, authorities)
    JF->>SC: SecurityContextHolder.getContext()<br/>.setAuthentication(auth)
    SC-->>SC: Authentication armazenado
    JF->>JF: filterChain.doFilter()
    Note over C,PE: Requisição continua para Controller
    JF->>AC: Requisição autorizada
    AC->>AC: Processa requisição
    AC-->>C: 200 OK<br/>List<AlunoDTO>
```

## Filtros do Spring Security (Filter Chain)

```mermaid
graph LR
    subgraph "Spring Security Filter Chain"
        direction TB
        R[Requisição HTTP] --> F1[1. SecurityContextPersistenceFilter<br/>Carrega SecurityContext]
        F1 --> F2[2. HeaderWriterFilter<br/>Adiciona headers de segurança]
        F2 --> F3[3. CorsFilter<br/>Processa CORS]
        F3 --> F4[4. CsrfFilter<br/>Valida CSRF token]
        F4 --> F5[5. LogoutFilter<br/>Processa logout]
        F5 --> F6[6. JwtAuthFilter ⭐<br/>Valida JWT e autentica]
        F6 --> F7[7. UsernamePasswordAuthenticationFilter<br/>Processa login form]
        F7 --> F8[8. ExceptionTranslationFilter<br/>Trata exceções de segurança]
        F8 --> F9[9. FilterSecurityInterceptor<br/>Verifica autorização]
    end
    
    F9 --> |Autorizado| APP[Controllers]
    F9 --> |Não autorizado| ERR[401/403]
    
    style F6 fill:#ffe1e1
    style F9 fill:#fff4e1
    style APP fill:#ccffcc
    style ERR fill:#ffcccc
```

## JwtAuthFilter - Detalhamento

```mermaid
flowchart TD
    Start([Requisição HTTP]) --> Extract[Extrair token do header<br/>Authorization: Bearer {token}]
    Extract --> HasToken{Token<br/>presente?}
    
    HasToken -->|Não| Skip[Pular autenticação<br/>Continua filtro]
    Skip --> End1[Requisição continua<br/>sem autenticação]
    
    HasToken -->|Sim| Validate[Validar token<br/>JwtUtil.isValid]
    Validate --> Valid{Token<br/>válido?}
    
    Valid -->|Não| Clear[Limpar SecurityContext]
    Clear --> End2[Requisição continua<br/>sem autenticação]
    
    Valid -->|Sim| GetSubject[Extrair email<br/>JwtUtil.getSubject]
    GetSubject --> LoadUser[Carregar UserDetails<br/>UserDetailsService]
    LoadUser --> CreateAuth[Criar Authentication<br/>UsernamePasswordAuthenticationToken]
    CreateAuth --> SetContext[Definir no SecurityContext<br/>SecurityContextHolder.getContext()<br/>.setAuthentication]
    SetContext --> Continue[filterChain.doFilter<br/>Continua para próximo filtro]
    Continue --> End3[Requisição autenticada]
    
    style Validate fill:#fff4e1
    style LoadUser fill:#ffe1e1
    style SetContext fill:#e1ffe1
    style End3 fill:#ccffcc
```

## SecurityConfig - Configuração de Segurança

```mermaid
graph TB
    subgraph "SecurityConfig @Configuration"
        SC[SecurityFilterChain Bean]
        
        SC --> CSRF[CSRF Configuration<br/>.csrf(AbstractHttpConfigurer::disable)]
        SC --> CORS[CORS Configuration<br/>.cors(corsConfig)]
        SC --> AUTH[Authorization Rules<br/>.authorizeHttpRequests]
        SC --> SESSION[Session Management<br/>.sessionManagement<br/>STATELESS]
        SC --> ENTRY[Exception Handling<br/>.exceptionHandling<br/>authenticationEntryPoint]
        SC --> FILTER[Add Custom Filter<br/>.addFilterBefore<br/>JwtAuthFilter]
    end
    
    subgraph "Authorization Rules"
        AUTH --> PUBLIC["/health, /error<br/>/auth/login<br/>/admin/generate-hash<br/>→ permitAll()"]
        AUTH --> PROTECTED["/aluno/**<br/>→ authenticated()"]
        AUTH --> ADMIN["/admin/**<br/>→ hasRole('ADMIN')"]
    end
    
    subgraph "Beans"
        SC --> BEAN1[PasswordEncoder<br/>BCryptPasswordEncoder]
        SC --> BEAN2[DaoAuthenticationProvider<br/>UserDetailsService + PasswordEncoder]
        SC --> BEAN3[AuthenticationManager<br/>ProviderManager]
    end
    
    style SC fill:#e1f5ff
    style PUBLIC fill:#ccffcc
    style PROTECTED fill:#fff4e1
    style ADMIN fill:#ffe1e1
```

## AuthenticationManager e Providers

```mermaid
graph TB
    subgraph "AuthenticationManager"
        AM[ProviderManager]
        AM --> P1[DaoAuthenticationProvider]
        AM --> P2[Outros Providers...]
    end
    
    subgraph "DaoAuthenticationProvider"
        P1 --> UDS[UserDetailsService<br/>loadUserByUsername]
        P1 --> PE[PasswordEncoder<br/>matches]
    end
    
    subgraph "UserDetailsService"
        UDS --> DB1[(Database)]
        DB1 --> USER[UserAccount Entity]
        USER --> UD[UserDetails<br/>getUsername<br/>getPassword<br/>getAuthorities]
    end
    
    subgraph "PasswordEncoder"
        PE --> BC[BCryptPasswordEncoder]
        BC --> HASH[BCrypt Hash<br/>$2a$10$...]
    end
    
    subgraph "Fluxo de Autenticação"
        REQ[Authentication Request] --> AM
        AM --> P1
        P1 --> UDS
        UDS --> UD
        P1 --> PE
        PE --> MATCH{Senha<br/>válida?}
        MATCH -->|Sim| SUCCESS[Authentication<br/>autenticado]
        MATCH -->|Não| FAIL[BadCredentialsException]
    end
    
    style AM fill:#e1f5ff
    style P1 fill:#fff4e1
    style SUCCESS fill:#ccffcc
    style FAIL fill:#ffcccc
```

## SecurityContext e Thread-Local Storage

```mermaid
graph TB
    subgraph "SecurityContextHolder"
        SCH[SecurityContextHolder<br/>ThreadLocal<SecurityContext>]
        SCH --> SC[SecurityContext]
        SC --> AUTH[Authentication Object]
    end
    
    subgraph "Authentication Object"
        AUTH --> PRINCIPAL[Principal<br/>UserDetails]
        AUTH --> CREDENTIALS[Credentials<br/>null após autenticação]
        AUTH --> AUTHORITIES[Authorities<br/>Collection<GrantedAuthority>]
        AUTH --> AUTHENTICATED[authenticated: true]
    end
    
    subgraph "UserDetails"
        PRINCIPAL --> UD[UserDetails Interface]
        UD --> USERNAME[getUsername: String]
        UD --> PASSWORD[getPassword: String]
        UD --> AUTHORITIES2[getAuthorities: Collection]
        UD --> ENABLED[isEnabled: boolean]
    end
    
    subgraph "GrantedAuthority"
        AUTHORITIES --> GA[GrantedAuthority]
        GA --> ROLE1[ROLE_ADMIN]
        GA --> ROLE2[ROLE_USER]
    end
    
    subgraph "Acesso no Controller"
        CONTROLLER[Controller] --> GETAUTH[SecurityContextHolder<br/>.getContext()<br/>.getAuthentication()]
        GETAUTH --> PRINCIPAL2[getPrincipal()]
        PRINCIPAL2 --> USERINFO[UserDetails<br/>Informações do usuário]
    end
    
    style SCH fill:#e1f5ff
    style AUTH fill:#fff4e1
    style UD fill:#ffe1e1
    style CONTROLLER fill:#e1ffe1
```

## Fluxo de Autorização (Authorization)

```mermaid
flowchart TD
    Start([Requisição Autenticada]) --> CheckAuth{Autenticado?}
    
    CheckAuth -->|Não| Reject1[401 Unauthorized<br/>AuthenticationEntryPoint]
    CheckAuth -->|Sim| GetAuth[Obter Authentication<br/>do SecurityContext]
    
    GetAuth --> GetRoles[Obter Roles/Authorities<br/>getAuthorities()]
    GetRoles --> CheckRoute{Rota?}
    
    CheckRoute -->|/health, /auth/login| Allow1[✅ permitAll<br/>Permitido]
    CheckRoute -->|/aluno/**| CheckAuth2{authenticated?}
    CheckRoute -->|/admin/**| CheckRole{hasRole<br/>'ADMIN'?}
    
    CheckAuth2 -->|Sim| Allow2[✅ authenticated<br/>Permitido]
    CheckAuth2 -->|Não| Reject2[403 Forbidden]
    
    CheckRole -->|Sim| Allow3[✅ hasRole ADMIN<br/>Permitido]
    CheckRole -->|Não| Reject3[403 Forbidden<br/>Access Denied]
    
    Allow1 --> Process[Processar Requisição]
    Allow2 --> Process
    Allow3 --> Process
    
    Reject1 --> End1[Retornar JSON Error]
    Reject2 --> End2[Retornar JSON Error]
    Reject3 --> End3[Retornar JSON Error]
    
    style Allow1 fill:#ccffcc
    style Allow2 fill:#ccffcc
    style Allow3 fill:#ccffcc
    style Reject1 fill:#ffcccc
    style Reject2 fill:#ffcccc
    style Reject3 fill:#ffcccc
```

## Exception Handling no Spring Security

```mermaid
sequenceDiagram
    participant C as Cliente
    participant F as FilterSecurityInterceptor
    participant E as ExceptionTranslationFilter
    participant EP as AuthenticationEntryPoint
    participant AH as AccessDeniedHandler
    participant GEH as GlobalExceptionHandler

    Note over C,GEH: Cenário 1: Não Autenticado

    C->>F: GET /aluno<br/>(sem token)
    F->>F: Verifica autenticação
    F->>E: AuthenticationException
    E->>EP: commence()
    EP->>EP: Gera JSON 401
    EP-->>C: 401 Unauthorized<br/>{status: 401, message: "Token necessário"}

    Note over C,GEH: Cenário 2: Não Autorizado

    C->>F: GET /admin/users<br/>(token válido, mas sem ROLE_ADMIN)
    F->>F: Verifica autorização
    F->>E: AccessDeniedException
    E->>AH: handle()
    AH->>AH: Gera JSON 403
    AH-->>C: 403 Forbidden<br/>{status: 403, message: "Acesso negado"}

    Note over C,GEH: Cenário 3: Exceção de Aplicação

    C->>F: GET /aluno/999<br/>(token válido, ID inexistente)
    F->>F: Requisição autorizada
    F->>C: Continua para Controller
    C->>GEH: AlunoNotFoundException
    GEH->>GEH: @ExceptionHandler
    GEH-->>C: 404 Not Found<br/>{status: 404, message: "Aluno não encontrado"}
```

## CORS Configuration

```mermaid
graph TB
    subgraph "CorsConfig"
        CC[CorsConfigurationSource]
        CC --> ALLOWED[Allowed Origins<br/>http://localhost:3000]
        CC --> ALLOWED_METHODS[Allowed Methods<br/>GET, POST, PUT, DELETE, OPTIONS]
        CC --> ALLOWED_HEADERS[Allowed Headers<br/>Authorization, Content-Type]
        CC --> EXPOSED_HEADERS[Exposed Headers<br/>Authorization]
        CC --> ALLOW_CREDENTIALS[Allow Credentials<br/>true]
        CC --> MAX_AGE[Max Age<br/>3600]
    end
    
    subgraph "Requisição CORS"
        REQ[Requisição do Frontend<br/>localhost:3000] --> PREFLIGHT{Preflight?<br/>OPTIONS}
        PREFLIGHT -->|Sim| OPTIONS[OPTIONS /aluno<br/>CORS headers]
        PREFLIGHT -->|Não| ACTUAL[GET /aluno<br/>CORS headers]
        OPTIONS --> RESPONSE1[200 OK<br/>CORS headers]
        ACTUAL --> RESPONSE2[200 OK<br/>CORS headers + dados]
    end
    
    subgraph "Headers CORS"
        RESPONSE1 --> H1[Access-Control-Allow-Origin:<br/>http://localhost:3000]
        RESPONSE1 --> H2[Access-Control-Allow-Methods:<br/>GET, POST, PUT, DELETE]
        RESPONSE1 --> H3[Access-Control-Allow-Headers:<br/>Authorization, Content-Type]
        RESPONSE1 --> H4[Access-Control-Allow-Credentials:<br/>true]
    end
    
    style CC fill:#e1f5ff
    style RESPONSE1 fill:#ccffcc
    style RESPONSE2 fill:#ccffcc
```

## Comparação: Autenticação vs Autorização

```mermaid
graph TB
    subgraph "AUTENTICAÇÃO (Authentication)"
        AUTH1[Quem é você?]
        AUTH1 --> AUTH2[Verifica identidade]
        AUTH2 --> AUTH3[Email + Senha]
        AUTH3 --> AUTH4[JWT Token]
        AUTH4 --> AUTH5[SecurityContext<br/>Authentication Object]
        AUTH5 --> AUTH6[✅ Autenticado]
    end
    
    subgraph "AUTORIZAÇÃO (Authorization)"
        AUTHZ1[O que você pode fazer?]
        AUTHZ1 --> AUTHZ2[Verifica permissões]
        AUTHZ2 --> AUTHZ3[Roles/Authorities]
        AUTHZ3 --> AUTHZ4[hasRole('ADMIN')]
        AUTHZ4 --> AUTHZ5[SecurityConfig<br/>Authorization Rules]
        AUTHZ5 --> AUTHZ6[✅ Autorizado]
    end
    
    AUTH6 --> AUTHZ1
    
    style AUTH1 fill:#fff4e1
    style AUTH6 fill:#ccffcc
    style AUTHZ1 fill:#ffe1e1
    style AUTHZ6 fill:#ccffcc
```

## Resumo do Fluxo Completo

```mermaid
flowchart TD
    Start([Cliente faz requisição]) --> CORS[CORS Filter<br/>Verifica origem]
    CORS --> CSRF[CSRF Filter<br/>Desabilitado]
    CSRF --> JWT[JwtAuthFilter<br/>Extrai e valida token]
    
    JWT --> HasToken{Token<br/>válido?}
    HasToken -->|Não| EntryPoint[AuthenticationEntryPoint<br/>401 Unauthorized]
    HasToken -->|Sim| LoadUser[Carrega UserDetails]
    LoadUser --> SetAuth[Define Authentication<br/>no SecurityContext]
    
    SetAuth --> AuthCheck[FilterSecurityInterceptor<br/>Verifica autorização]
    AuthCheck --> CheckRules{Regras de<br/>autorização}
    
    CheckRules -->|permitAll| Allow1[✅ Permitido]
    CheckRules -->|authenticated| Allow2[✅ Permitido]
    CheckRules -->|hasRole ADMIN| CheckRole{Role<br/>ADMIN?}
    
    CheckRole -->|Sim| Allow3[✅ Permitido]
    CheckRole -->|Não| Denied[403 Forbidden]
    
    Allow1 --> Controller[Controller<br/>Processa requisição]
    Allow2 --> Controller
    Allow3 --> Controller
    
    Controller --> Service[Service Layer]
    Service --> Repository[Repository Layer]
    Repository --> DB[(Database)]
    
    DB --> Repository
    Repository --> Service
    Service --> Controller
    
    Controller --> Exception{Exceção?}
    Exception -->|Sim| ExceptionHandler[GlobalExceptionHandler<br/>Trata exceção]
    Exception -->|Não| Response[Response JSON]
    
    ExceptionHandler --> Response
    Response --> End([Resposta ao Cliente])
    
    EntryPoint --> End
    Denied --> End
    
    style JWT fill:#ffe1e1
    style SetAuth fill:#fff4e1
    style Allow1 fill:#ccffcc
    style Allow2 fill:#ccffcc
    style Allow3 fill:#ccffcc
    style Denied fill:#ffcccc
    style EntryPoint fill:#ffcccc
```

