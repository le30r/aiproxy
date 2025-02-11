Simple proxy for AI services.

Includes metrics and prometheus. Based on spring cloud gateway.


## Usage

```bash
docker-compose up
```

## Grafana

http://localhost:3000/

username: admin
password: admin

Import dashboard:
https://github.com/spring-cloud/spring-cloud-gateway/blob/main/docs/src/main/asciidoc/gateway-grafana-dashboard.json


## Prometheus

http://localhost:9090/


## Metrics

http://localhost:8080/actuator/prometheus
