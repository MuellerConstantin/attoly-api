ALTER TABLE public.users
    ADD COLUMN IF NOT EXISTS payment_current_period_end        TIMESTAMPTZ(6),
    ADD COLUMN IF NOT EXISTS payment_current_period_start      TIMESTAMPTZ(6),
    ADD COLUMN IF NOT EXISTS payment_customer_id               VARCHAR(255),
    ADD COLUMN IF NOT EXISTS payment_last_event_id             VARCHAR(255),
    ADD COLUMN IF NOT EXISTS payment_subscription_status       VARCHAR(255),
    ADD COLUMN IF NOT EXISTS payment_subscription_id           VARCHAR(255);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'users_payment_subscription_status_check'
    ) THEN
ALTER TABLE public.users
    ADD CONSTRAINT users_payment_subscription_status_check
        CHECK (
            payment_subscription_status::text = ANY (
    ARRAY['ACTIVE', 'PAST_DUE', 'CANCELED']::text[]
    )
    );
END IF;
END$$;
