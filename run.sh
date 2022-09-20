docker-compose -f docker-compose-prod.yml down -v
docker system prune -y
docker-compose -f docker-compose-prod.yml up -d --build