#!/bin/sh

function checkError
{
  code=$1
  if [ $code -ne 0 ]
  then
    echo -e "Return code is NON-ZERO: $code"
    exit 1
  fi
}

# disable iptables, since it will block all the awesome stuff we're doing here
sudo chkconfig iptables off
sudo chkconfig ip6tables off
sudo service iptables stop
sudo service ip6tables stop

# disable ipv6
echo '# Disable ipv6' | sudo tee -a /etc/sysctl.conf
echo 'net.ipv6.conf.all.disable_ipv6 = 1' | sudo tee -a /etc/sysctl.conf
echo 'net.ipv6.conf.default.disable_ipv6 = 1' | sudo tee -a /etc/sysctl.conf

sudo mkdir -p /var/cache/wget
sudo chmod 777 /var/cache/wget
sudo sysctl -w net.ipv6.conf.all.disable_ipv6=1
sudo sysctl -w net.ipv6.conf.default.disable_ipv6=1

########## JAVA

# Cookie hack from here: http://stackoverflow.com/questions/10268583/how-to-automate-download-and-instalation-of-java-jdk-on-linux
pushd /var/cache/wget
  # Use curl so the auth param doesn't get appended to the filename, since that would mess up our caching
  curl -L -C - -b "oraclelicense=accept-securebackup-cookie" -O http://download.oracle.com/otn-pub/java/jdk/8u31-b13/jdk-8u31-linux-x64.rpm
  code=$?
  checkError $code
popd

sudo rpm -ivh /var/cache/wget/jdk-8u31-linux-x64.rpm
echo 'export JAVA_HOME=/usr/java/default' | tee -a ~/.bash_profile
source ~/.bash_profile

sudo yum -y install openssl098e
sudo yum -y install nc

# Install Zookeeper
wget -c -P /var/cache/wget 'http://archive.apache.org/dist/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz'
code=$?
checkError $code
tar xzf /var/cache/wget/zookeeper-3.4.6.tar.gz
sudo mv zookeeper-3.4.6 /opt/
sudo ln -s /opt/zookeeper-3.4.6 /opt/zookeeper
cd /opt/zookeeper
mv conf/zoo_sample.cfg conf/zoo.cfg
sudo bin/zkServer.sh start

# start storm
mkdir -p ~/storm

cd ~/storm

wget -c -P /var/cache/wget http://mirror.sdunix.com/apache/storm/apache-storm-0.9.4/apache-storm-0.9.4.tar.gz

tar -xzf /var/cache/wget/apache-storm-0.9.4.tar.gz

sudo mv apache-storm-0.9.4 /opt/
sudo ln -s /opt/apache-storm-0.9.4 /opt/storm
echo ui.port: 8081 >> /opt/storm/conf/storm.yaml
cd /opt/storm
sudo bin/storm supervisor &
sudo bin/storm nimbus &
sudo bin/storm ui &
while ! nc -vz localhost 8081; do sleep 1; done

# Run the storm topology for jobs
#/vagrant/setup_storm.sh

########## KAFKA

# Install Kafka
wget -c -P /var/cache/wget 'http://archive.apache.org/dist/kafka/0.8.2.0/kafka_2.11-0.8.2.0.tgz'
code=$?
checkError $code
tar xzf /var/cache/wget/kafka_2.11-0.8.2.0.tgz
sudo mv kafka_2.11-0.8.2.0 /opt/
sudo ln -s /opt/kafka_2.11-0.8.2.0 /opt/kafka

sudo mkdir /var/log/kafka
sudo chmod -u=rwx /var/log/kafka
sudo chmod -u=rwx /opt/kafka/bin/kafka-server-start.sh

cd /opt/kafka
sudo bin/kafka-server-start.sh config/server.properties &