-- Table Creation and Data Insertion for SQL Files
DROP TABLE IF EXISTS sql_files;

CREATE TABLE sql_files (
    project_name VARCHAR(40),
    commit_hash VARCHAR(100),
    author VARCHAR(40),
    date VARCHAR(50),
    time TIME,
    file_name VARCHAR(500),
    additions INTEGER,
    deletions INTEGER,
    sql CLOB,
    sql_context BOOLEAN,
    sql_change BOOLEAN,
    PRIMARY KEY(commit_hash, file_name)
);

INSERT INTO sql_files SELECT * FROM CSVREAD('C:\\Users\\04sam\\OneDrive\\Documents\\Summer project\\summer-project\\Datasets\\sql-data.csv');

-- Table Createion and Data Insertion for sql-lint
DROP TABLE IF EXISTS sql_lint;

CREATE TABLE sql_lint (
    commit_hash VARCHAR(100),
    file_name VARCHAR(500),
    lint_output CLOB,
    lint_summary CLOB,
    PRIMARY KEY(commit_hash, file_name)
);

INSERT INTO sql_lint SELECT * FROM CSVREAD('C:\\Users\\04sam\\OneDrive\\Documents\\Summer project\\summer-project\\Datasets\\sqlint_analysis.csv');

-- Table Createion and Data Insertion for SQLFluff
DROP TABLE IF EXISTS sql_fluff;

CREATE TABLE sql_fluff (
    commit_hash VARCHAR(100),
    file_name VARCHAR(500),
    fluff_output CLOB,
    fluff_summary CLOB,
    PRIMARY KEY(commit_hash, file_name)
);

INSERT INTO sql_fluff SELECT * FROM CSVREAD('C:\\Users\\04sam\\OneDrive\\Documents\\Summer project\\summer-project\\Datasets\\sqlfluff_analysis.csv');

-- Table Createion and Data Insertion for sql check
DROP TABLE IF EXISTS sql_check;

CREATE TABLE sql_check (
    commit_hash VARCHAR(100),
    file_name VARCHAR(500),
    check_output CLOB,
    check_summary CLOB,
    PRIMARY KEY(commit_hash, file_name)
);

INSERT INTO sql_check SELECT * FROM CSVREAD('C:\\Users\\04sam\\OneDrive\\Documents\\Summer project\\summer-project\\Datasets\\sqlcheck_analysis.csv');
