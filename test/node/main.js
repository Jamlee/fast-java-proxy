const http = require('http');

// 设置后端服务器地址
const backendHost = '127.0.0.1';
const backendPort = 8888;

// 创建代理服务器
const server = http.createServer((clientReq, clientRes) => {
    const options = {
        hostname: backendHost,
        port: backendPort,
        path: clientReq.url,
        method: clientReq.method,
        headers: clientReq.headers
    };

    const proxyReq = http.request(options, (backendRes) => {
        // 复制响应头
        clientRes.writeHead(backendRes.statusCode, backendRes.headers);

        // 复制响应体
        backendRes.pipe(clientRes);
    });

    // 错误处理
    proxyReq.on('error', (err) => {
        console.error('代理请求错误:', err);
        clientRes.writeHead(500);
        clientRes.end('代理服务器错误');
    });

    // 将客户端请求体传递给后端
    clientReq.pipe(proxyReq);
});

// 启动服务器
const PORT = 8080;
server.listen(PORT, () => {
    console.log(`代理服务器启动在 :${PORT}，后端服务器地址: http://${backendHost}:${backendPort}`);
});

// 错误处理
server.on('error', (err) => {
    console.error('服务器错误:', err);
});

// 确保只使用单核
process.env.UV_THREADPOOL_SIZE = '1';