FROM jboss/wildfly:21.0.2.Final

RUN mkdir -p /opt/jboss/wildfly/modules/org/postgresql/main
COPY server/postgresql-42.2.19.jar /opt/jboss/wildfly/modules/org/postgresql/main/
COPY server/module.xml /opt/jboss/wildfly/modules/org/postgresql/main/

RUN mkdir -p /opt/jboss/wildfly/redisson
COPY redisson.yml /opt/jboss/wildfly/redisson

COPY server/standalone.xml /opt/jboss/wildfly/standalone/configuration/

ADD target/azure-cafe.war /opt/jboss/wildfly/standalone/deployments/

# Run with the management interface.
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]