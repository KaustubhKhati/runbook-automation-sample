CREATE TABLE IF NOT EXISTS runbooks (
    id TEXT PRIMARY KEY,
    markdown TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS processed_tickets (
    id TEXT PRIMARY KEY,
    processed_at TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_processed_at
    ON processed_tickets(processed_at);