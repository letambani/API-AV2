#!/bin/bash

# Script para subir o projeto para o GitHub
# Uso: ./subir-github.sh SEU_USUARIO NOME_DO_REPOSITORIO

if [ -z "$1" ] || [ -z "$2" ]; then
    echo "❌ Erro: Informe seu usuário do GitHub e o nome do repositório"
    echo ""
    echo "Uso: ./subir-github.sh SEU_USUARIO NOME_DO_REPOSITORIO"
    echo ""
    echo "Exemplo:"
    echo "  ./subir-github.sh leticiatambani av2-springboot-api"
    echo ""
    exit 1
fi

USUARIO=$1
REPOSITORIO=$2

echo "=== CONFIGURANDO REPOSITÓRIO GIT ==="
echo ""

# Verificar se já existe remote
if git remote | grep -q "origin"; then
    echo "⚠️  Remote 'origin' já existe. Removendo..."
    git remote remove origin
fi

# Adicionar remote
echo "📡 Adicionando repositório remoto..."
git remote add origin https://github.com/$USUARIO/$REPOSITORIO.git

# Renomear branch para main (se necessário)
echo "🌿 Configurando branch principal..."
git branch -M main

# Verificar configuração
echo ""
echo "✅ Configuração concluída!"
echo ""
echo "Remote configurado:"
git remote -v
echo ""

# Perguntar se deseja fazer push
read -p "Deseja fazer push agora? (s/n) " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Ss]$ ]]; then
    echo ""
    echo "📤 Fazendo push para o GitHub..."
    git push -u origin main
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅✅✅ SUCESSO! ✅✅✅"
        echo ""
        echo "Seu projeto está disponível em:"
        echo "https://github.com/$USUARIO/$REPOSITORIO"
    else
        echo ""
        echo "❌ Erro ao fazer push."
        echo "Verifique se:"
        echo "  1. O repositório foi criado no GitHub"
        echo "  2. Você tem permissão para fazer push"
        echo "  3. Suas credenciais estão corretas"
    fi
else
    echo ""
    echo "Para fazer push manualmente, execute:"
    echo "  git push -u origin main"
fi

