ARG java_version=17

FROM amazoncorretto:${java_version}-alpine3.23-jdk

ARG maven_version

RUN apk add --no-cache \
    curl \
    unzip \
    ca-certificates \
    maven=${maven_version}

ENV MULE_HOME=/opt/mule
ENV MULE_VERSION=4.4.0

RUN set -x && \
    cd /opt && \
    curl -o mule.tar.gz https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/${MULE_VERSION}/mule-standalone-${MULE_VERSION}.tar.gz && \
    tar -xf mule.tar.gz && \
    mv mule-standalone-${MULE_VERSION} mule && \
    rm mule.tar.gz

COPY target/*.jar /opt/mule/apps
COPY fetch_secrets.sh .
COPY fetch_secrets_enterprise.sh .

EXPOSE 9093
ENTRYPOINT ["/opt/mule/apps/"]
