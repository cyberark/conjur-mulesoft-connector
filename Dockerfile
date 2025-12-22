ARG java_version=17

# Use Temurin Alpine as the base
FROM eclipse-temurin:${java_version}-jdk-alpine-3.23

ARG maven_version

# Install required packages
RUN apk add --no-cache \
    curl \
    unzip \
    ca-certificates \
    maven=${maven_version}

ENV MULE_HOME=/opt/mule
ENV MULE_VERSION=4.4.0

# Download and extract Mule
RUN set -x && \
    cd /opt && \
    curl -o mule.tar.gz https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/${MULE_VERSION}/mule-standalone-${MULE_VERSION}.tar.gz && \
    tar -xf mule.tar.gz && \
    mv mule-standalone-${MULE_VERSION} mule && \
    rm mule.tar.gz

#Copy jar file from build
COPY target/*.jar /opt/mule/apps
COPY fetch_secrets.sh .
COPY fetch_secrets_enterprise.sh .

# Default http port
EXPOSE 9093
ENTRYPOINT ["/opt/mule/apps/"]
