# Docker Setup for Verification System

This document explains how to run the Verification System using Docker.

## Prerequisites

- Docker (version 20.10 or higher)
- Docker Compose (version 2.0 or higher)

## Quick Start

### Production Build

1. **Build and run the application:**
   ```bash
   docker compose up --build
   ```

2. **Access the application:**
   - Main application: http://localhost:8080
   - Login credentials: `admin` / `admin123`

3. **Stop the application:**
   ```bash
   docker compose down
   ```

### Development Build (with hot reloading)

1. **Build and run in development mode:**
   ```bash
   docker-compose -f docker-compose.dev.yml up --build
   ```

2. **Access the application:**
   - Main application: http://localhost:8080
   - Changes to source code will automatically reload

3. **Stop the development environment:**
   ```bash
   docker-compose -f docker-compose.dev.yml down
   ```

## Production Deployment

### With Nginx Reverse Proxy

1. **Run with production profile:**
   ```bash
   docker-compose --profile production up --build
   ```

2. **Access the application:**
   - Main application: http://localhost (port 80)
   - The application is served through Nginx with optimized settings

### Standalone Application

1. **Run only the application:**
   ```bash
   docker-compose up verification-app --build
   ```

2. **Access the application:**
   - Main application: http://localhost:8080

## Docker Commands

### Building Images

```bash
# Build production image
docker build -t verification-system:latest .

# Build development image
docker build -f Dockerfile.dev -t verification-system:dev .
```

### Running Containers

```bash
# Run production container
docker run -p 8080:8080 verification-system:latest

# Run development container
docker run -p 8080:8080 -v $(pwd)/src:/app/src verification-system:dev
```

### Managing Containers

```bash
# View running containers
docker ps

# View logs
docker-compose logs -f verification-app

# Execute commands in running container
docker-compose exec verification-app bash

# Stop all services
docker-compose down

# Remove all containers and volumes
docker-compose down -v
```

## Configuration

### Environment Variables

You can customize the application by setting environment variables:

```bash
# In docker-compose.yml
environment:
  - JAVA_OPTS=-Xmx1g -Xms512m
  - KTOR_ENV=production
```

### Volume Mounts

The application uses the following volume mounts:

- `./logs:/app/logs` - Application logs
- `./src:/app/src` - Source code (development only)

### Ports

- `8080` - Application port
- `80` - Nginx port (production only)

## Health Checks

The application includes health checks that verify the service is running:

```bash
# Check health status
docker-compose ps

# View health check logs
docker-compose logs verification-app
```

## Troubleshooting

### Common Issues

1. **Port already in use:**
   ```bash
   # Change the port in docker-compose.yml
   ports:
     - "8081:8080"  # Use port 8081 instead of 8080
   ```

2. **Permission issues:**
   ```bash
   # Fix file permissions
   chmod +x gradlew
   ```

3. **Build failures:**
   ```bash
   # Clean and rebuild
   docker-compose down
   docker system prune -f
   docker-compose up --build
   ```

4. **Memory issues:**
   ```bash
   # Increase memory allocation
   environment:
     - JAVA_OPTS=-Xmx2g -Xms1g
   ```

### Logs

```bash
# View application logs
docker-compose logs verification-app

# Follow logs in real-time
docker-compose logs -f verification-app

# View logs for specific service
docker-compose logs nginx
```

### Debugging

```bash
# Access container shell
docker-compose exec verification-app bash

# Check container resources
docker stats

# Inspect container
docker inspect verification-app
```

## Security Considerations

1. **Non-root user:** The application runs as a non-root user (`appuser`)
2. **Security headers:** Nginx configuration includes security headers
3. **Resource limits:** Memory and CPU limits can be configured
4. **Network isolation:** Services are isolated in Docker networks

## Performance Optimization

1. **Multi-stage builds:** Production image is optimized for size
2. **Layer caching:** Gradle dependencies are cached separately
3. **Gzip compression:** Nginx provides compression for static files
4. **Resource limits:** Configurable memory and CPU limits

## Monitoring

```bash
# Monitor resource usage
docker stats

# Check container health
docker-compose ps

# View application metrics
curl http://localhost:8080/health
```

## Backup and Restore

```bash
# Backup application data
docker-compose exec verification-app tar -czf /app/backup.tar.gz /app/logs

# Restore from backup
docker cp backup.tar.gz verification-app:/app/
docker-compose exec verification-app tar -xzf /app/backup.tar.gz
```
