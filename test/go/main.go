package main

import (
	"io"
	"log"
	"net/http"
	"net/url"
	"runtime"
)

func main() {
	runtime.GOMAXPROCS(1)

	// 设置后端服务器地址
	backendURL, err := url.Parse("http://127.0.0.1:8888")
	if err != nil {
		log.Fatal("后端服务器地址解析错误:", err)
	}

	proxy := &http.Server{
		Addr: ":8080",
		Handler: http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			// 修改请求URL，指向后端服务器
			r.URL.Host = backendURL.Host
			r.URL.Scheme = backendURL.Scheme
			r.Host = backendURL.Host

			// 创建新的请求
			proxyReq, err := http.NewRequest(r.Method, r.URL.String(), r.Body)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}

			// 复制原始请求的 header
			for key, values := range r.Header {
				for _, value := range values {
					proxyReq.Header.Add(key, value)
				}
			}

			// 发送代理请求
			client := &http.Client{}
			resp, err := client.Do(proxyReq)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			defer resp.Body.Close()

			// 复制响应 header
			for key, values := range resp.Header {
				for _, value := range values {
					w.Header().Add(key, value)
				}
			}

			// 设置状态码
			w.WriteHeader(resp.StatusCode)

			// 复制响应体
			io.Copy(w, resp.Body)
		}),
	}

	log.Printf("代理服务器启动在 :8080，后端服务器地址: %s", backendURL)
	if err := proxy.ListenAndServe(); err != nil {
		log.Fatal("代理服务器错误:", err)
	}
}
