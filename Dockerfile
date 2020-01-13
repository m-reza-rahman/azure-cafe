FROM jboss/wildfly

EXPOSE 9990


RUN mkdir -p /opt/jboss/wildfly/modules/org/postgresql/main
COPY server/postgresql-42.2.4.jar /opt/jboss/wildfly/modules/org/postgresql/main/
COPY server/module.xml /opt/jboss/wildfly/modules/org/postgresql/main/
COPY server/standalone.xml /opt/jboss/wildfly/standalone/configuration/
# ADD target/${project.build.finalName}.war /opt/jboss/wildfly/standalone/deployments/
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
