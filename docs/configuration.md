# Configuration

The configuration is based on the used Spring Boot framework. For the various configuration options that Spring Boot
offers,
see [Spring Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
.
Usually property files or environment variables are used, the latter should be found in the current working directory.
Basically, the entire application can be personalized via external configuration. Settings relevant to the application
are listed below, for further options please
see [Common Application Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
.

Each of the listed properties below can also be configured via environment variables. The name of the environment
variable corresponds to the name of the property in upper case, with the period separator being replaced by an
underscore and dashes are removed. Please
see [Binding From Environment Variables](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.external-config.typesafe-configuration-properties.relaxed-binding.environment-variables)
for details.

| Property                                                       | Description                                                                                                                | Required |
|----------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------|----------|
| server.port                                                    | Port of the internal application server. By default it listens on port 8080.                                               | false    |
| spring.datasource.url                                          | The URL to the relational database to use.¹                                                                                | true     |
| spring.datasource.driver-class-name                            | The database driver used to control database.                                                                              | true     |
| spring.datasource.username                                     | The database user with which access is made.                                                                               | false    |
| spring.datasource.password                                     | An optional password associated with the database user.                                                                    | false    |
| spring.data.redis.host                                         | The host of the Redis database to use. This is mainly required for temporary data.                                         | true     |
| spring.data.redis.port                                         | The port of the Redis database.                                                                                            | true     |
| spring.data.redis.username                                     | An optional user to authenticate with redis.                                                                               | false    |
| spring.data.redis.password                                     | If authentication is required, the password for the user.                                                                  | false    |
| spring.data.redis.database                                     | The Redis Server database to use. By default, database 0 is used.                                                          | false    |
| spring.mail.host                                               | Host of the SMTP server used by Java Mail.                                                                                 | true     |
| spring.mail.port                                               | Port of the SMTP server used by Java Mail.                                                                                 | true     |
| spring.mail.username                                           | Name of user to authenticate with the SMTP server.                                                                         | false    |
| spring.mail.password                                           | Password of the user to authenticate with the SMTP server.                                                                 | false    |
| spring.security.oauth2.client.registration.github.clientId     | The unique identifier for the registered GitHub OAuth2 application.                                                        | true     |
| spring.security.oauth2.client.registration.github.clientSecret | The secret of the registered GitHub OAuth2 application.                                                                    | true     |
| attoly.security.token.access.secret                            | The secret used to sign the JWT access tokens. It must be at least 256 bits (32 Characters) long.                          | true     |
| attoly.security.token.access.expiresIn                         | The duration in milliseconds after which the access token expires.                                                         | false    |
| attoly.security.token.refresh.length                           | The length of the opaque refresh token.                                                                                    | false    |
| attoly.security.token.refresh.expiresIn                        | The duration in milliseconds after which the refresh token expires.                                                        | false    |
| attoly.web.verify-user-uri                                     | The URL of the user verification page of the 1st party client. This URL enables a single click forwarding from the e-mail. | true     |
| attoly.web.reset-password-uri                                  | The URL of the 1st party client's password reset page. This URL enables a single click forwarding from the e-mail.         | true     |

<small>¹In principle, any JPA/Hibernate capable relational database can be used. For this, however, the application must
also have the corresponding drivers as a dependency in the Java Classpath. By default, only the PostgreSQL drivers are
included
with the application.</small>

In addition, the application uses various cron and start up jobs to set up the database and environment. Below are the
options to enable/disable and configure these jobs.

| Property                                                      | Description                                                                                                                                                                                      | Required |
|---------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| attoly.scheduling.jobs.anonymous-shortcut-clean-up.enabled    | Activates or deactivates the cron job, which removes anonymous shortcuts after a certain period of time.                                                                                         | false    |
| attoly.scheduling.jobs.anonymous-shortcut-clean-up.cron       | Sets the time at which the job should run cyclically. Must be a cron expression.                                                                                                                 | false    |
| attoly.scheduling.jobs.anonymous-shortcut-clean-up.expires-in | Sets the duration in milliseconds after which an anonymous shortcut expires.                                                                                                                     | false    |
| attoly.scheduling.jobs.role-seeding.enabled                   | Enables/disables the job that creates the security roles when the application starts, if they don't already exist. Alternatively, the roles must be created manually in the database.            | false    |
| attoly.scheduling.jobs.initial-admin-creation.enabled         | Activates/deactivates the job which allows to create the default admin in the database. Alternatively, this must be created manually, since an administrator is required to manage the platform. | false    |
| attoly.scheduling.jobs.initial-admin-creation.email           | E-mail address of the default admin account to be created.                                                                                                                                       | false    |
| attoly.scheduling.jobs.initial-admin-creation.password        | Password of the default admin account to be created.                                                                                                                                             | false    |
