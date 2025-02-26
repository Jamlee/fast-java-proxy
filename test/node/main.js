const http = require('http');
// 创建一个启用 keepAlive 的 Agent 实例
const keepAliveAgent = new http.Agent({
    keepAlive: true,
    keepAliveMsecs: 1000,
    maxSockets: Infinity,
    maxFreeSockets: 256
});

http.createServer((clientReq, clientRes) => {
    // 创建代理请求
    const proxyReq = http.request({
        host: '127.0.0.1',
        port: 8888,
        path: '/',
        method: clientReq.method,
        headers: clientReq.headers,
        agent: keepAliveAgent,
    }, (proxyRes) => {
        clientRes.writeHead(proxyRes.statusCode, proxyRes.headers);
        proxyRes.pipe(clientRes);
    });

    proxyReq.on('error', (e) => {
        clientRes.writeHead(500, {'Content-Type': 'text/plain'});
        clientRes.end('Proxy Error: ' + e.message);
    });

    clientReq.pipe(proxyReq);
}).listen(3000, () => console.log('Proxy running on port 3000'));