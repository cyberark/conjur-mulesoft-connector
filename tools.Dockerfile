# Use Alpine image as the base
FROM alpine:3.19

ARG maven_version

# Install required packages
RUN apk add --no-cache \
    openjdk17-jdk \
    curl \
    unzip \
    ca-certificates \
    yq

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Download and extract Maven
RUN set -eux; \
  MAVEN_TGZ="apache-maven-${maven_version}-bin.tar.gz"; \
  BASE_URL="https://archive.apache.org/dist/maven/maven-3/${maven_version}/binaries"; \
  curl -fsSL "${BASE_URL}/${MAVEN_TGZ}" -o /tmp/maven.tar.gz; \
  curl -fsSL "${BASE_URL}/${MAVEN_TGZ}.sha512" -o /tmp/maven.tar.gz.sha512; \
  echo "$(cat /tmp/maven.tar.gz.sha512)  /tmp/maven.tar.gz" | sha512sum -c -; \
  mkdir -p /opt/maven; \
  tar -xzf /tmp/maven.tar.gz -C /opt/maven --strip-components=1; \
  rm -f /tmp/maven.tar.gz /tmp/maven.tar.gz.sha512

# Set Maven environment variables
ENV MAVEN_HOME=/opt/maven
ENV PATH="$MAVEN_HOME/bin:$PATH"