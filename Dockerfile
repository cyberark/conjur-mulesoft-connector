#FROM adoptopenjdk/openjdk8

ARG java_version
ARG maven_version
FROM maven:${maven_version}-openjdk-${java_version}-slim

ENV MULE_HOME=/opt/mule

ENV MULE_VERSION=4.4.0

#Linux: please uncomment this section and comment below windows section
RUN set -x \
&& cd /opt \
&& apt-get update -y && apt-get install unzip -y \
&& curl -o mule.tar.gz https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/4.4.0/mule-standalone-4.4.0.tar.gz \
&& tar -xf mule.tar.gz \
&& mv mule-standalone-$MULE_VERSION mule \
&& rm mule.tar.gz*

#Windows: Please use either Linux section or Windows section
#RUN set -x \
#&& cd /opt \
#&& curl -o mule.zip https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/$#{MULE_VERSION}/mule-standalone-${MULE_VERSION}.zip \
#&& unzip mule.zip \
#&& mv mule-standalone-$MULE_VERSION mule \
#&& rm mule.tar.zip*

# WORKDIR $MULE_HOME
# VOLUME ["$MULE_HOME/logs", "$MULE_HOME/conf", "$MULE_HOME/apps", "$MULE_HOME/domains"]

#cpoying jar file from build
COPY target/*.jar /opt/mule/apps
COPY fetch_secrets.sh .
COPY fetch_secrets_enterprise.sh .

# Copy and install license
#CMD echo "------ Copy and install license --------"
#COPY muleLicenseKey.lic $MULE_HOME/conf/
#RUN $MULE_HOME/bin/mule -installLicense $MULE_HOME/conf/muleLicenseKey.lic

# Default http port
EXPOSE 9093
#ENTRYPOINT ["./bin/mule"]
ENTRYPOINT ["/opt/mule/apps/"]
