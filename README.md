# itrum

A high-performance wallet management demo.

## Setup

### Cloning

```bash
git clone https://github.com/ryadovoyy/itrum.git
cd itrum
```

### Environment variables

Copy the env example file and change variables if you want:

```bash
cp .env.example .env
```

## Run

```bash
docker compose up
```

## Endpoints

API will be available at `http://localhost:8080`

- `POST /api/v1/wallets` - deposit/withdraw funds
- `GET /api/v1/wallets/{id}` - check balance
