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

ENV MULE_HOME=/opt/mule
ENV MULE_VERSION=4.4.0

# Download and extract Mule
RUN set -x && \
    cd /opt && \
    curl -o mule.tar.gz https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/${MULE_VERSION}/mule-standalone-${MULE_VERSION}.tar.gz && \
    tar -xf mule.tar.gz && \
    mv mule-standalone-${MULE_VERSION} mule && \
    rm mule.tar.gz

#cpoying jar file from build
COPY target/*.jar /opt/mule/apps
COPY fetch_secrets.sh .
COPY fetch_secrets_enterprise.sh .

# Default http port
EXPOSE 9093
ENTRYPOINT ["/opt/mule/apps/"]
