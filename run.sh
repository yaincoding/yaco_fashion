docker-compose -f docker-compose-prod.yml down -v
docker system prune
docker-compose -f docker-compose-prod.yml up -d --build