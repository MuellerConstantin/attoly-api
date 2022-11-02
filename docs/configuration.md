# Configuration

The configuration can be done in different ways,
see [Spring Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
for details. Usually via environment variables or via so-called property files, the latter should be found in the
current working directory under the filename *application.yml*. Basically, the entire application can be personalized
via external configuration. Settings relevant to the application are listed below, for further options please see
[Common Application Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
.

| Property                                | Environment Variable                    | Description                                                                        | Required |
|-----------------------------------------|-----------------------------------------|------------------------------------------------------------------------------------|----------|
| server.port                             | SERVER_PORT                             | Port of the internal application server. By default it listens on port 8080.       | false    |
| spring.datasource.url                   | SPRING_DATASOURCE_URL                   | The URL to the relational database to use.¹                                        | true     |
| spring.datasource.driver-class-name     | SPRING_DATASOURCE_DRIVERCLASSNAME       | The database driver used to control database.                                      | true     |
| spring.datasource.username              | SPRING_DATASOURCE_USERNAME              | The database user with which access is made.                                       | false    |
| spring.datasource.password              | SPRING_DATASOURCE_PASSWORD              | An optional password associated with the database user.                            | false    |
| spring.redis.host                       | SPRING_REDIS_HOST                       | The host of the Redis database to use. This is mainly required for temporary data. | true     |
| spring.redis.port                       | SPRING_REDIS_PORT                       | The port of the Redis database.                                                    | true     |
| spring.redis.username                   | SPRING_REDIS_USERNAME                   | An optional user to authenticate with redis.                                       | false    |
| spring.redis.password                   | SPRING_REDIS_PASSWORD                   | If authentication is required, the password for the user.                          | false    |
| spring.redis.database                   | SPRING_REDIS_DATABASE                   | The Redis Server database to use. By default, database 0 is used.                  | false    |
| spring.mail.host                        | SPRING_MAIL_HOST                        | Host of the SMTP server used by Java Mail.                                         | true     |
| spring.mail.port                        | SPRING_MAIL_PORT                        | Port of the SMTP server used by Java Mail.                                         | true     |
| spring.mail.username                    | SPRING_MAIL_USERNAME                    | Name of user to authenticate with the SMTP server.                                 | false    |
| spring.mail.password                    | SPRING_MAIL_PASSWORD                    | Password of the user to authenticate with the SMTP server.                         | false    |
| attoly.security.token.access.secret     | ATTOLY_SECURITY_TOKEN_ACCESS_SECRET     | The secret used to sign the JWT access tokens.                                     | true     |
| attoly.security.token.access.expiresIn  | ATTOLY_SECURITY_TOKEN_ACCESS_EXPIRESIN  | The duration in milliseconds after which the access token expires.                 | false    |
| attoly.security.token.refresh.length    | ATTOLY_SECURITY_TOKEN_REFRESH_LENGTH    | The length of the opaque refresh token.                                            | false    |
| attoly.security.token.refresh.expiresIn | ATTOLY_SECURITY_TOKEN_REFRESH_EXPIRESIN | The duration in milliseconds after which the refresh token expires.                | false    |

<small>¹In principle, any JPA/Hibernate capable relational database can be used. For this, however, the application must
also
have the corresponding drivers as a dependency in the Java Classpath. By default, only the MySQL drivers are included
with the application.</small>