// Servidor simples para testar o frontend
// Execute: node servidor-teste-frontend.js

const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;

const server = http.createServer((req, res) => {
    const url = req.url.split('?')[0];
    
    if (url === '/' || url === '/index.html') {
        const filePath = path.join(__dirname, 'teste-frontend.html');
        fs.readFile(filePath, (err, data) => {
            if (err) {
                res.writeHead(500);
                res.end('Erro ao ler arquivo');
                return;
            }
            res.writeHead(200, { 'Content-Type': 'text/html' });
            res.end(data);
        });
    } else {
        res.writeHead(404);
        res.end('Not Found');
    }
});

server.listen(PORT, () => {
    console.log(`Servidor de teste rodando em http://localhost:${PORT}`);
    console.log(`Abra no navegador: http://localhost:${PORT}`);
    console.log(`Para parar: Ctrl+C`);
});

