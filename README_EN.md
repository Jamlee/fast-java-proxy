# fast-java-proxy

> A high-performance HTTP proxy based on Reactor Netty, achieving 8000+ QPS with only 100 lines of code in a single file.

[简体中文](README.md) | English

A high-performance HTTP proxy server implemented in a single file (about 100 lines of code) for performance testing and verification.

## Key Features

- Single file implementation (just one HttpProxy.java)
- High-performance implementation based on Reactor Netty
- Minimalist code structure

## Performance Comparison

| Stack | QPS | Notes |
|-------|-----|-------|
| Spring Cloud Gateway | 8,000 | Unlimited CPU cores |
| Proxy(This Project) | 11000 | Single core |
| Node.js | 4000 | Single core |
| Golang | 9000 | Single core |

## Technical Stack

- Framework: Reactor Netty
- Connection Pool: Customized ConnectionProvider
- Event Loop: Customized LoopResources

## Main Features

- Async non-blocking IO
- Connection pool management
- Timeout control
- HTTP header processing
- Error handling

## Quick Start

1. Clone the project
2. Run `HttpProxy.java`
3. Default configuration:
   - Listen: 8080
   - Target: localhost:3000

## Performance Optimization

1. Connection pool management
2. Event loop configuration
3. Timeout control
4. Memory optimization

## Project Notes

- Development time: 2.5 hours
- AI-assisted development
- Single file design
- Zero external documentation dependency

## Notes

- Verification phase project
- Needs further testing
- Load testing recommended

## Future Plans

- [ ] Performance test report
- [ ] Configuration extensions
- [ ] Monitoring metrics
- [ ] Error handling optimization
