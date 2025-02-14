package org.example;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateHandler;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class HttpProxy {
    // 添加常量定义
    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
        "connection",
        "keep-alive",
        "proxy-authentication",
        "proxy-authorization",
        "te",
        "trailer",
        "transfer-encoding",
        "upgrade"
    );

    private final HttpClient client;
    private final HttpServer server;

    public HttpProxy(int port) {
        ConnectionProvider provider = ConnectionProvider.builder("proxy-pool")
                .maxConnections(1024)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .build();

        LoopResources loop = LoopResources.create("proxy-loop", 1, 1, true);

        this.client = HttpClient.create(provider)
                .runOn(loop)
                .responseTimeout(Duration.ofSeconds(30))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)));

        this.server = HttpServer.create()
                .runOn(loop)
                .port(port)
                .idleTimeout(Duration.ofSeconds(120))
                .handle(this::handleRequest);
    }


    public void start() {
        try {
            server.bindNow().onDispose().block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start proxy server", e);
        }
    }

    private Flux<Void> handleRequest(HttpServerRequest request,
                                     HttpServerResponse response) {
        String path = request.uri();
        String targetUrl = "localhost:3000" + path;

        return client
                .headers(getHeadersConsumer(request))
                .request(request.method())
                .uri(targetUrl)
                .send(request.receive().retain())
                .responseConnection((clientResponse, connection) -> {
                    HOP_BY_HOP_HEADERS.forEach(clientResponse.responseHeaders()::remove);
                    clientResponse.responseHeaders().set("x-proxy-by", "proxy");
                    return response.status(clientResponse.status())
                            .headers(clientResponse.responseHeaders())
                            .send(connection.inbound().receive().retain());
                })
                .onErrorResume(error ->
                    response.status(502)
                           .sendString(Mono.just("Proxy Error: " + error.getMessage()))
                );
    }

    private Consumer<HttpHeaders> getHeadersConsumer(HttpServerRequest request) {
        return headers -> request.requestHeaders().entries().forEach(entry -> {
            String name = entry.getKey().toString();
            if (!HOP_BY_HOP_HEADERS.contains(name.toLowerCase())) {
                headers.set(name, entry.getValue());
            }
        });
    }

    public static void main(String[] args) {
        new HttpProxy(8080).start();
    }
}
