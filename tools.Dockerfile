ARG java_version

# Use an OpenJDK image as the base
FROM openjdk:${java_version}-slim

ARG maven_version

# Install required packages
RUN apt-get update -y && \
    apt-get install -y curl unzip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Download and extract Maven
RUN curl -o /tmp/maven.tar.gz https://downloads.apache.org/maven/maven-3/${maven_version}/binaries/apache-maven-${maven_version}-bin.tar.gz && \
    mkdir -p /opt/maven && \
    tar -xzf /tmp/maven.tar.gz -C /opt/maven --strip-components=1 && \
    rm /tmp/maven.tar.gz

# Set Maven environment variables
ENV MAVEN_HOME=/opt/maven
ENV PATH="$MAVEN_HOME/bin:$PATH"

RUN curl -L  https://github.com/mikefarah/yq/releases/download/v4.18.1/yq_linux_amd64 -o /usr/bin/yq &&\
    chmod +x /usr/bin/yq

RUN apt-get update && apt-get install -y gpg
