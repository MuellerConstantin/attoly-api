CREATE TABLE public.roles
(
    id               uuid        NOT NULL,
    created_at       timestamptz NOT NULL,
    last_modified_at timestamptz NOT NULL,
    deleted          boolean     NOT NULL,
    deleted_at       timestamptz,
    version          bigint      NOT NULL,
    name             varchar(255),

    CONSTRAINT pk_roles
        PRIMARY KEY (id),
    CONSTRAINT uq_roles_name
        UNIQUE (name),
    CONSTRAINT chk_roles_name
        CHECK (
            name IN (
                     'ROLE_ADMIN',
                     'ROLE_MODERATOR',
                     'ROLE_USER'
                )
            )
);

CREATE TABLE public.users
(
    id                   uuid         NOT NULL,
    created_at           timestamptz  NOT NULL,
    last_modified_at     timestamptz  NOT NULL,
    deleted              boolean      NOT NULL,
    deleted_at           timestamptz,
    version              bigint       NOT NULL,
    email                varchar(255) NOT NULL,
    email_verified       boolean      NOT NULL,
    identity_provider    varchar(255),
    identity_provider_id varchar(255),
    locked               boolean      NOT NULL,
    password             varchar(255),

    CONSTRAINT pk_users
        PRIMARY KEY (id),
    CONSTRAINT uq_users_email
        UNIQUE (email),
    CONSTRAINT chk_users_identity_provider
        CHECK (identity_provider = 'GITHUB')
);

CREATE INDEX idx_users_identity_provider_id
    ON public.users (identity_provider, identity_provider_id);

CREATE TABLE public.shortcuts
(
    id               uuid          NOT NULL,
    created_at       timestamptz   NOT NULL,
    last_modified_at timestamptz   NOT NULL,
    deleted          boolean       NOT NULL,
    deleted_at       timestamptz,
    version          bigint        NOT NULL,
    tag              varchar(255)  NOT NULL,
    url              varchar(2000) NOT NULL,
    created_by       uuid,

    CONSTRAINT pk_shortcuts
        PRIMARY KEY (id),
    CONSTRAINT uq_shortcuts_tag
        UNIQUE (tag),
    CONSTRAINT fk_shortcuts_created_by_user
        FOREIGN KEY (created_by)
            REFERENCES public.users (id)
);

CREATE INDEX idx_shortcuts_created_by
    ON public.shortcuts (created_by);

CREATE INDEX idx_shortcuts_active
    ON public.shortcuts (tag) WHERE deleted = false;

CREATE TABLE public.user_roles
(
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,

    CONSTRAINT pk_user_roles
        PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
            REFERENCES public.users (id),
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
            REFERENCES public.roles (id)
);

CREATE INDEX idx_user_roles_user_id
    ON public.user_roles (user_id);

CREATE INDEX idx_user_roles_role_id
    ON public.user_roles (role_id);

CREATE TABLE public.complaints
(
    id               uuid         NOT NULL,
    created_at       timestamptz  NOT NULL,
    last_modified_at timestamptz  NOT NULL,
    deleted          boolean      NOT NULL,
    deleted_at       timestamptz,
    version          bigint       NOT NULL,
    comment          varchar(2000),
    reason           varchar(255) NOT NULL,
    shortcut         uuid,

    CONSTRAINT pk_complaints
        PRIMARY KEY (id),
    CONSTRAINT chk_complaints_reason
        CHECK (
            reason IN (
                       'SPAM',
                       'PHISHING',
                       'MALWARE',
                       'DEFACEMENT'
                )
            ),
    CONSTRAINT fk_complaints_shortcut
        FOREIGN KEY (shortcut)
            REFERENCES public.shortcuts (id)
);

CREATE INDEX idx_complaints_shortcut
    ON public.complaints (shortcut);
