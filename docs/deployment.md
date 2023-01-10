# Deployment

Deployment consists of two steps, setting up the system environment, which includes the necessary third-party services,
and deploying the actual server application. The deployment of the Attoly API application can be done either using
containerization or in standalone mode.

## System Environment

The application uses a number of third-party services and also requires them to function correctly. It is therefore
necessary to make these services available and to make them known accordingly. All third-party services can be
configured
using the properties described under [configuration](./configuration.md).

- MySQL Database Server
- Redis Database Server
- SMTP Email Server

The Attoly API uses JPA/Hibernate based on a relational database to store system and business information. In principle,
any database supported by JPA/Hibernate can be used for this. By default, however, only the database drivers for a MySQL
database are included with the application. For all other databases, the drivers must be added to the classpath later. A
corresponding table [schema](./scripts/schema.sql) is also expected in the configured database¹. In addition to the
relational database, Attoly uses a faster in-memory key-value database, currently only Redis is supported. Redis is
required as a cache for volatile information and for sessions/tokens. Attoly also requires an SMTP mail server to send
emails. In principle, any email provider and their SMTP server can be used.

<small>¹Alternatively, Attoly can create the database tables independently if they do not already exist (not recommended
in production). For this, however, the corresponding DDL rights must be given and the property
`spring.jpa.hibernate.ddl-auto` must be set to `update`.</small>

## Attoly Deployment

As earlier mentioned, the Attoly API application can be either deployed using containerization or in standalone mode.

### Standalone

In the standalone variant, the application runs directly on the JVM of the target system. This requires a Java Runtime
Environment of version 17 or higher. Basically, no Java EE application server is required for deployment. The
application can be started as a normal Java application and sets up its own application server internally.

```shell
java -jar <NAME>.jar
```

In order for the application to work correctly, it must be configured in addition to the installation. The easiest way
to configure the standalone variant is to use a configuration file in YAML format, called `application.yml` in the
current working directory of the process, usually the directory in which the above command is executed¹. Information on
the application's configurable options can be found under [configuration](./configuration.md).

<small>¹The configuration is based on the Spring Framework configuration mechanism. For more information and further
options to configure the application see
[Spring Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
.</small>

#### Build application

If for any reason a pre-built executable cannot be used, for example during development, it is possible to build the
application manually². The result is an executable Java archive, which can be started in standalone mode as described
above. The artifact can be built with the Maven build system using a local installation. A Java archive (JAR) with all
the
required dependencies and libraries for system-independent deployment is created. To build it, run the following
command.

```shell
mvn clean package
```

The resulting artifact, usually located in the project's target directory (`/target`), can be executed by the above
command.

<small>²If the application is to be built directly from the source code instead of obtaining a pre-built executable, the
[Java Development Kit](https://adoptium.net/) in version 17 or higher and [Apache Maven](https://maven.apache.org/)
are required.</small>

### Docker

If the application is to be started in container mode, this can be done via Docker. This does not require a Java Runtime
Environment installation on the target system, but an installation of the Docker Engine. The release in the form of a
Docker image can be started as follows³:

```shell
docker run -p 8080:8080 --name attoly/api -d attoly/api:latest
```

Even with container deployment, the application still has to be configured. This is basically the same as for standalone
operation. When using a configuration file, however, it must be ensured that this is made accessible to the container,
for example by mounting a volume. Alternatively, the container can be configured using system environment variables.
For configuration details see [configuration](./configuration.md).

<small>³For more information about possible options of the container or when starting the image, see
[Docker Documentation](https://docs.docker.com/engine/reference/commandline/run/).</small>

#### Build image

Should it be necessary in the development phase or for other reasons to build the Docker image directly from the source
code, this is also possible. No Java development tools or installations are required for this either, the image is built
in multi-stage operation on a Docker basis. The provided Dockerfile can be used to build:

```shell
docker build -t attoly/api:latest .
```

The resulting Docker image should be placed in the local image registry and can be executed using the above command.
