.PHONY: help build test clean run-auth run-system docker-build docker-up docker-down

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build the project
	mvn clean compile

test: ## Run tests
	mvn clean test

test-coverage: ## Run tests with coverage report
	mvn clean test jacoco:report

package: ## Package the application
	mvn clean package

clean: ## Clean build artifacts
	mvn clean

run-auth: ## Run auth service
	cd auth && mvn spring-boot:run

run-system: ## Run system service
	cd system && mvn spring-boot:run

docker-build: ## Build Docker images
	docker build -t auth-service:latest ./auth
	docker build -t system-service:latest ./system

docker-up: ## Start services with Docker Compose
	docker-compose up -d

docker-down: ## Stop services with Docker Compose
	docker-compose down

docker-logs: ## View Docker Compose logs
	docker-compose logs -f

setup-db: ## Set up PostgreSQL database
	docker run --name postgres-dev -e POSTGRES_PASSWORD=1234 -e POSTGRES_DB=Blog -p 5432:5432 -d postgres:15

setup-redis: ## Set up Redis cache
	docker run --name redis-dev -p 6379:6379 -d redis:7-alpine

dev-setup: setup-db setup-redis ## Set up development environment
	@echo "Development environment is ready!"

install: ## Install dependencies
	mvn dependency:resolve

format: ## Format code (if you add a formatter plugin)
	mvn spotless:apply

lint: ## Run linting (if you add a linter plugin)
	mvn checkstyle:check