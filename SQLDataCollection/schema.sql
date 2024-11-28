CREATE TABLE IF NOT EXISTS 'sql_commits' (
    'commit_id' VARCHAR(100) PRIMARY KEY,
    'project_number' INTEGER,
    'date' DATETIME,
    'author' VARCHAR(100),
    'patch' TEXT,
    'sql' TEXT,
    'sql_context' BOOLEAN,
    'sql_update' BOOLEAN
)