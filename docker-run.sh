#!/bin/bash

# Docker management script for Verification System

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}=== $1 ===${NC}"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to build and run in production mode
production() {
    print_header "Starting Verification System in Production Mode"
    check_docker
    
    print_status "Building and starting the application..."
    docker compose up --build -d
    
    print_status "Application is starting up..."
    sleep 10
    
    if curl -f http://localhost:8080/ > /dev/null 2>&1; then
        print_status "Application is running successfully!"
        print_status "Access the application at: http://localhost:8080"
        print_status "Login credentials: admin / admin123"
    else
        print_warning "Application might still be starting up. Please wait a moment and try accessing http://localhost:8080"
    fi
}

# Function to build and run in development mode
development() {
    print_header "Starting Verification System in Development Mode"
    check_docker
    
    print_status "Building and starting the application in development mode..."
    docker compose -f docker-compose.dev.yml up --build
    
    print_status "Development server is running!"
    print_status "Access the application at: http://localhost:8080"
    print_status "Changes to source code will automatically reload"
}

# Function to run with nginx reverse proxy
production_nginx() {
    print_header "Starting Verification System with Nginx Reverse Proxy"
    check_docker
    
    print_status "Building and starting the application with nginx..."
    docker compose --profile production up --build -d
    
    print_status "Application is starting up..."
    sleep 15
    
    if curl -f http://localhost/ > /dev/null 2>&1; then
        print_status "Application is running successfully with nginx!"
        print_status "Access the application at: http://localhost"
        print_status "Login credentials: admin / admin123"
    else
        print_warning "Application might still be starting up. Please wait a moment and try accessing http://localhost"
    fi
}

# Function to stop the application
stop() {
    print_header "Stopping Verification System"
    check_docker
    
    print_status "Stopping all containers..."
    docker compose down
    docker compose -f docker-compose.dev.yml down
    
    print_status "Application stopped successfully!"
}

# Function to view logs
logs() {
    print_header "Viewing Application Logs"
    check_docker
    
    if [ "$1" = "follow" ]; then
        print_status "Following logs (press Ctrl+C to stop)..."
        docker compose logs -f verification-app
    else
        docker compose logs verification-app
    fi
}

# Function to clean up
clean() {
    print_header "Cleaning Up Docker Resources"
    check_docker
    
    print_warning "This will remove all containers, images, and volumes. Are you sure? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        print_status "Stopping and removing containers..."
        docker compose down -v
        docker compose -f docker-compose.dev.yml down -v
        
        print_status "Removing unused Docker resources..."
        docker system prune -f
        
        print_status "Cleanup completed!"
    else
        print_status "Cleanup cancelled."
    fi
}

# Function to show status
status() {
    print_header "Application Status"
    check_docker
    
    print_status "Running containers:"
    docker compose ps
    
    echo ""
    print_status "Resource usage:"
    docker stats --no-stream
}

# Function to show help
help() {
    print_header "Docker Management Script Help"
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  prod          Start the application in production mode"
    echo "  dev           Start the application in development mode (with hot reloading)"
    echo "  nginx         Start the application with nginx reverse proxy"
    echo "  stop          Stop all running containers"
    echo "  logs          Show application logs"
    echo "  logs follow   Follow application logs in real-time"
    echo "  status        Show application status and resource usage"
    echo "  clean         Clean up all Docker resources (containers, images, volumes)"
    echo "  help          Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 prod       # Start production build"
    echo "  $0 dev        # Start development build"
    echo "  $0 logs       # View logs"
    echo "  $0 stop       # Stop application"
}

# Main script logic
case "${1:-help}" in
    "prod"|"production")
        production
        ;;
    "dev"|"development")
        development
        ;;
    "nginx")
        production_nginx
        ;;
    "stop")
        stop
        ;;
    "logs")
        logs "$2"
        ;;
    "status")
        status
        ;;
    "clean")
        clean
        ;;
    "help"|"--help"|"-h"|"")
        help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        help
        exit 1
        ;;
esac
