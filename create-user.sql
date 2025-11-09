-- Criar usuário admin@fmp.br com senha 123456 (BCrypt)
-- Hash BCrypt para "123456": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- Mas vamos usar um hash mais recente gerado pelo Spring

-- Primeiro, vamos verificar a estrutura da tabela
DESCRIBE users;

-- Inserir usuário (o hash será gerado pela aplicação, mas podemos usar um temporário)
-- NOTA: O hash correto será gerado quando a aplicação iniciar com o DataSeeder
