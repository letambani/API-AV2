# Como Verificar DTO e Projection no Postman

## Objetivo
Demonstrar que:
1. `GET /aluno/1` retorna apenas campos necessários (AlunoDTO)
2. Listas usam `AlunoDTO` ou `@Projection`
3. **NÃO retorna** campos sensíveis como senha

---

## Teste 6: GET /aluno/1 - Retorna AlunoDTO

### Endpoint:
```
GET http://localhost:8081/aluno/1
Header: Authorization: Bearer {{jwt}}
```

### O que fazer:
1. Execute primeiro o teste "1. Login - Obter JWT" para obter o token
2. Use o teste "6. GET /aluno/1 - Retorna AlunoDTO"
3. Ou crie um novo request:
   - Método: `GET`
   - URL: `http://localhost:8081/aluno/1`
   - Header: `Authorization: Bearer {{jwt}}`
4. Clique em "Send"

### Resposta esperada:
```json
{
  "id": 1,
  "nome": "João Silva",
  "idade": 25,
  "cpf": "12345678900"
}
```

### O que observar:
✅ **Campos presentes** (apenas 4):
- `id`: Long
- `nome`: String
- `idade`: Integer
- `cpf`: String

❌ **Campos ausentes** (não retornados):
- `senha` - ❌ Não existe
- `senhaHash` - ❌ Não existe
- `password` - ❌ Não existe
- `createdAt` - ❌ Não existe
- `updatedAt` - ❌ Não existe
- Qualquer outro campo que não seja necessário

### Validação automática:
O teste verifica:
- ✅ Status 200
- ✅ Tem exatamente 4 campos: `id`, `nome`, `idade`, `cpf`
- ✅ NÃO tem `senha`
- ✅ NÃO tem `senhaHash`
- ✅ NÃO tem `password`
- ✅ NÃO tem `createdAt` ou `updatedAt`

### Conclusão:
`GET /aluno/1` retorna apenas campos necessários usando `AlunoDTO`, sem expor dados sensíveis.

---

## Teste 2: GET /aluno - Lista usa AlunoDTO

### Endpoint:
```
GET http://localhost:8081/aluno
Header: Authorization: Bearer {{jwt}}
```

### O que fazer:
1. Use o teste "2. GET /aluno - Listar com Token"
2. Clique em "Send"

### Resposta esperada:
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "idade": 25,
    "cpf": "12345678900"
  },
  {
    "id": 2,
    "nome": "Maria Santos",
    "idade": 22,
    "cpf": "98765432100"
  }
]
```

### O que observar:
✅ **Cada item** na lista tem apenas 4 campos:
- `id`, `nome`, `idade`, `cpf`

❌ **Nenhum item** tem:
- `senha`
- `senhaHash`
- `password`
- Campos extras

### Validação automática:
O teste verifica:
- ✅ Status 200
- ✅ Resposta é um array
- ✅ Cada aluno tem apenas campos necessários
- ✅ NÃO tem `senha`, `senhaHash`, `password`

### Conclusão:
A lista `GET /aluno` usa `AlunoDTO` para cada item, garantindo que apenas campos necessários sejam retornados.

---

## Comparação: Entity vs DTO

### Entity `Aluno` (no banco):
```java
@Entity
public class Aluno {
    private Long id;
    private String nome;
    private Integer idade;
    private String cpf;
    // Pode ter outros campos internos
}
```

### DTO `AlunoDTO` (retornado pela API):
```java
public class AlunoDTO {
    private Long id;      // ✅ Exposto
    private String nome;  // ✅ Exposto
    private Integer idade; // ✅ Exposto
    private String cpf;   // ✅ Exposto
    // Apenas campos necessários
}
```

### O que NÃO está no DTO:
- ❌ Senhas
- ❌ Hashes
- ❌ Campos internos
- ❌ Dados sensíveis
- ❌ Timestamps (se não forem necessários)

---

## Implementação no Código

### Controller:
```java
@RestController
@RequestMapping("/aluno")
public class AlunoController {
    
    @GetMapping("/{id}")
    public ResponseEntity<AlunoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<AlunoDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
```

### Service:
```java
@Service
public class AlunoService {
    
    public AlunoDTO findById(Long id) {
        Aluno aluno = repository.findById(id)
            .orElseThrow(() -> new AlunoNotFoundException(id));
        return AlunoDTO.from(aluno); // Converte Entity → DTO
    }
    
    public List<AlunoDTO> findAll() {
        return repository.findAll().stream()
            .map(AlunoDTO::from) // Converte cada Entity → DTO
            .collect(Collectors.toList());
    }
}
```

### DTO:
```java
public class AlunoDTO {
    private Long id;
    private String nome;
    private Integer idade;
    private String cpf;
    
    public static AlunoDTO from(Aluno a) {
        return new AlunoDTO(
            a.getId(),
            a.getNome(),
            a.getIdade(),
            a.getCpf()
        );
    }
}
```

---

## Alternativa: Usando @Projection

Se você quiser usar Spring Data Projections em vez de DTOs:

### Interface Projection:
```java
package br.fmp.av2.repository.projection;

public interface AlunoView {
    Long getId();
    String getNome();
    Integer getIdade();
    String getCpf();
}
```

### Repository:
```java
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    List<AlunoView> findAllProjectedBy();
    AlunoView findProjectedById(Long id);
}
```

### Controller:
```java
@GetMapping
public ResponseEntity<List<AlunoView>> findAll() {
    return ResponseEntity.ok(repository.findAllProjectedBy());
}
```

**Nota**: No projeto atual, estamos usando `AlunoDTO` em vez de `@Projection`, mas ambos servem ao mesmo propósito: retornar apenas campos necessários.

---

## Resumo dos Testes no Postman

### Teste 6: GET /aluno/1
- **URL**: `GET /aluno/1`
- **Com autenticação**: `Authorization: Bearer {{jwt}}`
- **Valida**: Retorna apenas 4 campos (id, nome, idade, cpf)
- **Valida**: NÃO retorna senha ou campos sensíveis

### Teste 2: GET /aluno
- **URL**: `GET /aluno`
- **Com autenticação**: `Authorization: Bearer {{jwt}}`
- **Valida**: Lista usa AlunoDTO
- **Valida**: Cada item tem apenas campos necessários

---

## Evidências de Uso de DTO

1. **Resposta limitada**: Apenas 4 campos (`id`, `nome`, `idade`, `cpf`)
2. **Sem senha**: Nenhum campo relacionado a senha é retornado
3. **Estrutura consistente**: Todos os endpoints retornam o mesmo formato
4. **Segurança**: Dados sensíveis não são expostos

---

## Pronto!

Agora você pode demonstrar no Postman que:
- ✅ `GET /aluno/1` retorna apenas campos necessários (AlunoDTO)
- ✅ Listas usam AlunoDTO (ou @Projection)
- ✅ NÃO retorna senha ou campos sensíveis
- ✅ Estrutura de resposta é consistente e segura


