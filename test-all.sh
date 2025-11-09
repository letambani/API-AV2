#!/bin/bash

# Script de teste completo para AV2
# Executa todos os testes e mostra os resultados

echo "=========================================="
echo "  TESTES AV2 - Spring Boot API"
echo "=========================================="
echo ""

BASE_URL="http://localhost:8081"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função para testar e mostrar resultado
test_endpoint() {
    local name=$1
    local method=$2
    local url=$3
    local headers=$4
    local body=$5
    local expected_status=$6
    
    echo -e "${YELLOW}Testando: $name${NC}"
    
    if [ -z "$body" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" $headers)
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" $headers -d "$body" -H "Content-Type: application/json")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body_response=$(echo "$response" | sed '$d')
    
    if [ "$http_code" == "$expected_status" ]; then
        echo -e "${GREEN}✅ PASSOU - Status: $http_code${NC}"
        echo "Resposta: $(echo "$body_response" | head -c 100)..."
    else
        echo -e "${RED}❌ FALHOU - Esperado: $expected_status, Recebido: $http_code${NC}"
        echo "Resposta: $body_response"
    fi
    echo ""
}

# Etapa 1: Login
echo "=== ETAPA 1: POST /auth/login ==="
login_response=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@fmp.br","senha":"123456"}')

TOKEN=$(echo "$login_response" | python3 -c "import sys, json; print(json.load(sys.stdin)['token'])" 2>/dev/null)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}❌ ERRO: Não foi possível obter token${NC}"
    echo "Resposta: $login_response"
    exit 1
fi

echo -e "${GREEN}✅ Token obtido: ${TOKEN:0:50}...${NC}"
echo ""

# Etapa 2: GET /aluno com token
test_endpoint "GET /aluno COM TOKEN" "GET" "$BASE_URL/aluno" \
  "-H \"Authorization: Bearer $TOKEN\"" "" "200"

# Etapa 3: GET /aluno sem token
test_endpoint "GET /aluno SEM TOKEN" "GET" "$BASE_URL/aluno" "" "" "401"

# Etapa 4: POST /aluno com token
test_endpoint "POST /aluno COM TOKEN" "POST" "$BASE_URL/aluno" \
  "-H \"Authorization: Bearer $TOKEN\"" \
  '{"nome":"Teste Aluno","idade":20,"cpf":"55566677788"}' "201"

# Etapa 5: POST /aluno CPF duplicado
test_endpoint "POST /aluno CPF DUPLICADO" "POST" "$BASE_URL/aluno" \
  "-H \"Authorization: Bearer $TOKEN\"" \
  '{"nome":"Outro Aluno","idade":25,"cpf":"55566677788"}' "409"

# Etapa 6: GET /aluno/999 ID inexistente
test_endpoint "GET /aluno/999 ID INEXISTENTE" "GET" "$BASE_URL/aluno/999" \
  "-H \"Authorization: Bearer $TOKEN\"" "" "404"

# Etapa 7: POST /auth/login credenciais erradas
test_endpoint "POST /auth/login CREDENCIAIS ERRADAS" "POST" "$BASE_URL/auth/login" \
  "" '{"email":"admin@fmp.br","senha":"senhaerrada"}' "401"

# Etapa 8: CORS
echo "=== ETAPA 8: CORS ==="
cors_response=$(curl -s -i -X OPTIONS "$BASE_URL/aluno" \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET")

if echo "$cors_response" | grep -q "Access-Control-Allow-Origin: http://localhost:3000"; then
    echo -e "${GREEN}✅ CORS configurado corretamente${NC}"
    echo "$cors_response" | grep -i "access-control"
else
    echo -e "${RED}❌ CORS não está configurado${NC}"
fi
echo ""

echo "=========================================="
echo "  TESTES CONCLUÍDOS"
echo "=========================================="

