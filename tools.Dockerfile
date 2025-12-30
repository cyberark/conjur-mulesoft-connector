ARG java_version=17

# Use Temurin Alpine as the base
FROM eclipse-temurin:${java_version}-jdk-alpine-3.23

ARG maven_version

# Install required packages
RUN apk add --no-cache \
    curl \
    unzip \
    ca-certificates \
    maven=${maven_version} \
    yq