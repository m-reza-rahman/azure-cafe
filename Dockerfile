FROM jboss/wildfly

RUN mkdir -p /opt/jboss/wildfly/modules/org/postgresql/main
COPY server/postgresql-42.2.4.jar /opt/jboss/wildfly/modules/org/postgresql/main/
COPY server/module.xml /opt/jboss/wildfly/modules/org/postgresql/main/
COPY server/standalone.xml /opt/jboss/wildfly/standalone/configuration/
ADD target/azure-cafe.war /opt/jboss/wildfly/standalone/deployments/