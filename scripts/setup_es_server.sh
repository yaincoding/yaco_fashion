# 4GB swap 공간 생성
echo "스왑 생성중.."
sudo dd if=/dev/zero of=/swapfile bs=128M count=32
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo swapon -s
echo "/swapfile swap swap defaults 0 0" | sudo tee -a /etc/fstab

# docker 설치
echo "도커 설치중.."
sudo amazon-linux-extras install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user

# docker-compose 설치
echo "도커 컴포즈 설치중.."
sudo curl -L https://github.com/docker/compose/releases/download/1.22.0/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

#es plugin 다운로드
echo "hanhinsam 다운로드중.."
wget https://github.com/yaincoding/hanhinsam/raw/master/zip/elasticsearch-8.3.3/hanhinsam-0.1.zip -O ../elasticsearch/hanhinsam.zip