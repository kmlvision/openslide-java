FROM kmlvision/openslide
LABEL maintainer="KML VISION, devops@kmlvision.com"

RUN apt-get update -qq && \
  apt-get install -y \
    software-properties-common \
    ant \
    wget

# install java 11
RUN wget https://download.java.net/java/GA/jdk11/28/GPL/openjdk-11+28_linux-x64_bin.tar.gz -O /tmp/openjdk-11+28_linux-x64_bin.tar.gz && tar xfvz /tmp/openjdk-11+28_linux-x64_bin.tar.gz --directory /usr/lib/jvm && rm -f /tmp/openjdk-11+28_linux-x64_bin.tar.gz

# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/jdk-11
RUN sh -c 'for bin in /usr/lib/jvm/jdk-11/bin/*; do update-alternatives --install /usr/bin/$(basename $bin) $(basename $bin) $bin 100; done' && sh -c 'for bin in /usr/lib/jvm/jdk-11/bin/*; do update-alternatives --set $(basename $bin) $bin; done' && java --version

# build the application
RUN mkdir -p /opt/openslide-java
COPY . /opt/openslide-java
WORKDIR /opt/openslide-java

# build openslide-java
RUN autoreconf -i && ./configure && make && make install
# rename file
RUN mv /opt/openslide-java/openslide.jar /opt/openslide-java/openslide-java-6.jar

# wait with a shell
CMD ["/bin/bash"]