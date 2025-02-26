import asyncio
from aiohttp import web, ClientSession

TARGET_SERVER = "http://localhost:8888"

async def proxy_handler(request):
    target_url = TARGET_SERVER + request.path_qs
    headers = dict(request.headers)
    headers.pop('Host', None)

    try:
        async with ClientSession() as session:
            # 支持所有HTTP方法
            method = getattr(session, request.method.lower(), None)
            if not method:
                return web.Response(status=405, text="Method Not Allowed")

            # 处理请求体
            data = await request.read() if request.method in ('POST', 'PUT') else None

            async with method(target_url, headers=headers, data=data) as resp:
                content = await resp.read()
                response_headers = dict(resp.headers)

                # 修复分块传输问题
                response_headers.pop('Transfer-Encoding', None)
                response_headers['Content-Length'] = str(len(content))

                return web.Response(
                    body=content,
                    status=resp.status,
                    headers=response_headers
                )
    except Exception as e:
        print(f"Error: {e}")
        return web.Response(status=500, text=str(e))

app = web.Application()
app.router.add_route('*', '/{path:.*}', proxy_handler)

if __name__ == '__main__':
    web.run_app(app, port=8080)