# Attoly API

> Data management and programming interface for the Attoly URL shortener platform.

![Logo](docs/images/banner.svg)

![](https://img.shields.io/badge/dynamic/xml?color=red&label=Java&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27properties%27%5D%2F%2A%5Blocal-name%28%29%3D%27java.version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2FMuellerConstantin%2Fattoly-api%2Fmaster%2Fpom.xml&logo=openjdk)
![](https://img.shields.io/badge/dynamic/xml?color=green&label=Spring%20Boot&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2FMuellerConstantin%2Fattoly-api%2Fmaster%2Fpom.xml&logo=spring-boot)
![](https://img.shields.io/badge/ORM-Hibernate-blue?logo=hibernate)
![](https://img.shields.io/badge/Database-PostgreSQL-blue?logo=postgresql)
![](https://img.shields.io/badge/Database-Redis-red?logo=redis)
![](https://img.shields.io/badge/OpenAPI-3.0.1-green?logo=openapi-initiative)

## Table of contents

- [Introduction](#introduction)
- [Architecture](#architecture)
- [Deployment](#deployment)
- [License](#license)
  - [Forbidden](#forbidden)

## Introduction

This is the backend of the Attoly URL shortener platform, responsible for data storage and exchange.
Attoly allows creating short links as aliases for long URLs, which are easier to embed in websites,
chats and documents. Originally, the Attoly Platform is a learning project to gain experience in
distributed systems, user management, and cloud deployment. Therefore there are no claims to maintenance,
freedom from errors or further development. Further development only occurs as part of the learning process.

## Architecture

The Attoly platform is a distributed system. The Attoly API Service therefore only provides the backend
of the platform. It is a monolithic 3-layer architecture which is geared towards the horizontal scaling
of the service. The application is divided into the layers of presentation, business logic and data
management. The presentation layer essentially consists of an HTTP RESTful interface for machine-to-machine
interaction with the outsourced frontend. This interface is documented in OpenAPI format and stored
[here](src/main/resources/public/docs/v1/openapi.yml). Alternatively, the documentation can also be viewed
as a webpage via the self-service. The middle layer is the business layer, which essentially contains the
business logic in the form of services. The third and last layer is the data access layer, which is
responsible for data storage and event sourcing. The databases are accessed via repositories or data access
objects (DAO).

![Architecture](docs/images/architecture.svg)

The Attoly API also uses various third-party services, including but not limited to databases. Attoly uses a
relational database, by default a PostgreSQL database, for storing entities and a NoSQL Redis database for storing
temporary information like tokens. For individual processes, such as verifying user accounts, it is necessary
to send e-mails. An SMTP server must be configured for this, which Attoly uses for this purpose.

## Deployment

Deployment can be done via Docker or standalone. Information on this can be found under
[deployment](docs/deployment.md). The configuration options of the application are also important for deployment
and development. You can find it under [configuration](docs/configuration.md).

## License

Copyright (c) 2025 Constantin Müller

[GNU AFFERO GENERAL PUBLIC LICENSE](https://www.gnu.org/licenses/) or [LICENSE](LICENSE.md) for
more details.

### Forbidden

**Hold Liable**: Software is provided without warranty and the software
author/license owner cannot be held liable for damages.
