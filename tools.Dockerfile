ARG java_version=17

FROM amazoncorretto:${java_version}-alpine3.23-jdk

ARG maven_version

RUN apk add --no-cache \
    curl \
    unzip \
    ca-certificates \
    maven=${maven_version} \
    yq