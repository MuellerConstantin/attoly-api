DROP TABLE `complaints`;
DROP TABLE `shortcuts`;
DROP TABLE `user_roles`;
DROP TABLE `roles`;
DROP TABLE `users`;

CREATE TABLE `users`
(
    `id`                   varchar(255) NOT NULL,
    `created_at`           datetime(6) NOT NULL,
    `deleted`              bit(1)       NOT NULL,
    `last_modified_at`     datetime(6) NOT NULL,
    `version`              bigint       NOT NULL,
    `email`                varchar(255) DEFAULT NULL,
    `password`             varchar(255) DEFAULT NULL,
    `email_verified`       bit(1)       NOT NULL,
    `deleted_at`           datetime(6) DEFAULT NULL,
    `locked`               bit(1)       NOT NULL,
    `identity_provider`    varchar(255) DEFAULT NULL,
    `identity_provider_id` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `roles`
(
    `id`               varchar(255) NOT NULL,
    `created_at`       datetime(6) NOT NULL,
    `deleted`          bit(1)       NOT NULL,
    `last_modified_at` datetime(6) NOT NULL,
    `version`          bigint       NOT NULL,
    `name`             varchar(255) DEFAULT NULL,
    `deleted_at`       datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_roles`
(
    `user_id` varchar(255) NOT NULL,
    `role_id` varchar(255) NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    KEY       `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
    CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
    CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `shortcuts`
(
    `id`               varchar(255)  NOT NULL,
    `created_at`       datetime(6) NOT NULL,
    `deleted`          bit(1)        NOT NULL,
    `last_modified_at` datetime(6) NOT NULL,
    `version`          bigint        NOT NULL,
    `tag`              varchar(255)  NOT NULL,
    `url`              varchar(2000) NOT NULL,
    `created_by`       varchar(255) DEFAULT NULL,
    `deleted_at`       datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_axwnm57aj9yf8c8nrif299nup` (`tag`),
    UNIQUE KEY `UKaxwnm57aj9yf8c8nrif299nup` (`tag`),
    KEY                `FK7xm061c3vtpp2jm4e1vsxvrnr` (`created_by`),
    CONSTRAINT `FK7xm061c3vtpp2jm4e1vsxvrnr` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `complaints`
(
    `id`               varchar(255) NOT NULL,
    `created_at`       datetime(6) NOT NULL,
    `deleted`          bit(1)       NOT NULL,
    `deleted_at`       datetime(6) DEFAULT NULL,
    `last_modified_at` datetime(6) NOT NULL,
    `version`          bigint       NOT NULL,
    `comment`          varchar(2000) DEFAULT NULL,
    `reason`           varchar(255) NOT NULL,
    `shortcut`         varchar(255)  DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY                `FK14hq5wtrjihneahfpnk1dls9x` (`shortcut`),
    CONSTRAINT `FK14hq5wtrjihneahfpnk1dls9x` FOREIGN KEY (`shortcut`) REFERENCES `shortcuts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
