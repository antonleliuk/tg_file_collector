# Telegram File Collector

A Spring Boot application that collects files sent to a Telegram bot and saves them to a local directory. It is designed to work with a local Telegram Bot API server to handle large files efficiently.

## Features

- Collects documents and photos from Telegram.
- Supports local Telegram Bot API server for downloading files without size limitations.
- Restricted access: Only allowed users can interact with the bot.
- Kubernetes-ready with Helm charts.
- Configurable storage path.

## Prerequisites

- A Kubernetes cluster (e.g., k3s, minikube).
- Helm 3.x installed.
- Telegram API Credentials (API ID and API Hash) from [my.telegram.org](https://my.telegram.org).
- A Telegram Bot Token from [@BotFather](https://t.me/BotFather).

## Preparation: Secrets

Before installing the Helm chart, you need to prepare the secrets. You can either provide them directly in `values.yaml` (not recommended for production) or create Kubernetes secrets manually.

### 1. Telegram API Credentials
These are required for the `telegram-bot-api` sidecar/service.

If you want to create the secret manually:
```bash
kubectl create secret generic tg-file-collector-api-credentials \
  --from-literal=TELEGRAM_API_ID=your_api_id \
  --from-literal=TELEGRAM_API_HASH=your_api_hash \
  -n family-files-space
```
Then set `telegramBotApi.secret.existingSecret: tg-file-collector-api-credentials` in your `values.yaml`.

### 2. Bot Token
This is required for the bot application to authenticate with Telegram.

If you want to create the secret manually:
```bash
kubectl create secret generic tg-file-collector-bot-token \
  --from-literal=token=your_bot_token \
  -n family-files-space
```
Then set `config.telegrambots.security.existingSecret: tg-file-collector-bot-token` in your `values.yaml`.

### 3. Image Pull Secret (GHCR)
If the Docker image is private (default for GHCR), you need to create an image pull secret.

1. Create a Personal Access Token (PAT) with `read:packages` scope.
2. Create the secret in your namespace:
```bash
kubectl create secret docker-registry ghcr-pull-secret \
  --docker-server=ghcr.io \
  --docker-username=your_github_username \
  --docker-password=your_pat \
  -n family-files-space
```
3. Set `imagePullSecrets` in your `values.yaml`:
```yaml
imagePullSecrets:
  - name: ghcr-pull-secret
```

### 4. Telegram Bot API URL
By default, the application is configured to connect to the `telegram-bot-api` sidecar service included in the Helm chart. If you are using an external Telegram Bot API server, you can override the URL settings in your `values.yaml`:

```yaml
config:
  telegrambots:
    telegramUrl:
      schema: "https"
      host: "api.telegram.org"
      port: "443"
```

## Installation

### GitHub Actions (CI/CD)

The project includes several GitHub Actions workflows that automatically:
1. **Linting**: Checks `Dockerfile`, Helm charts.
2. **Maven CI**: Builds the Spring Boot application and runs tests.
3. **Docker Build**: Builds and pushes the Docker image to GitHub Container Registry (GHCR).
4. **Helm Package**: Packages and pushes the Helm chart to GHCR as an OCI artifact.

The workflow is triggered on every push to the `main` branch and when a new tag `v*` is created.

### Method 1: Local Installation (from source)

1. Clone the repository:
   ```bash
   git clone https://github.com/antonleliuk/tg-file-collector.git
   cd tg-file-collector
   ```

2. Create a `my-values.yaml` file with your configuration:
   ```yaml
   config:
     telegrambots:
       security:
         token: "YOUR_BOT_TOKEN"
         allowedUsers: "user1,user2" # GitHub or Telegram usernames/IDs depending on implementation
   
   telegramBotApi:
     secret:
       apiId: "YOUR_API_ID"
       apiHash: "YOUR_API_HASH"
   
   persistence:
     storageClassName: "local-path" # Or your preferred storage class
   ```

3. Install the chart:
   ```bash
   helm install tg-file-collector ./helm -f my-values.yaml -n family-files-space --create-namespace
   ```

### Method 2: Installing from GitHub (OCI Registry)

```bash
helm install tg-file-collector oci://ghcr.io/antonleliuk/charts/tg-file-collector \
  -f my-values.yaml \
  -n family-files-space --create-namespace
```

## Configuration

| Parameter                                   | Description                                       | Default                           |
|---------------------------------------------|---------------------------------------------------|-----------------------------------|
| `imagePullSecrets`                         | List of secrets to pull images from private registries | `[]`                              |
| `config.telegrambots.security.token`        | Your Telegram Bot Token                           | `""`                              |
| `config.telegrambots.security.allowedUsers` | Comma-separated list of allowed Telegram user IDs | `""`                              |
| `telegramBotApi.secret.apiId`               | Telegram API ID                                   | `""`                              |
| `telegramBotApi.secret.apiHash`             | Telegram API Hash                                 | `""`                              |
| `persistence.enabled`                       | Enable persistent storage for downloaded files    | `true`                            |
| `persistence.size`                          | Size of the persistent volume                     | `10Gi`                            |
| `persistence.storageClassName`              | Storage class for the PVC                         | `local-tg-file-collector-storage` |
