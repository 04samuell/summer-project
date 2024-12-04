CREATE TABLE IF NOT EXISTS 'sql_commits' (
    'project_name' VARCHAR(100) PRIMARY KEY,
    'commit_hash' INTEGER,
    'date_time' DATETIME,
    'author' VARCHAR(100),
    'sql' TEXT,
    'sql_context' BOOLEAN,
    'sql_update' BOOLEAN
)