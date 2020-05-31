FROM centos:7
RUN yum -y update && yum install -y wget
RUN wget -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.tar.gz -q -P / \
&& tar -xzf jdk-8u131-linux-x64.tar.gz -C /opt \
&& rm -f /jdk-8u131-linux-x64.tar.gz
ENV JAVA_HOME /opt/jdk1.8.0_131
ENV PATH $PATH:$JAVA_HOME/bin
ADD ./target/ArMED-1.0.jar /home/
ADD ar_workspace /home/ar_workspace
CMD [ "java", "-jar", "/home/ArMED-1.0.jar"]
