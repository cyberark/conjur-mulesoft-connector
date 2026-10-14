ARG java_version=17

FROM amazoncorretto:${java_version}-alpine3.24-jdk

ARG maven_version

RUN apk add --no-cache \
    curl \
    gnupg \
    ca-certificates \
    'zlib>=1.3.2-r1' \
    maven=${maven_version}