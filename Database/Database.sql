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

-- Data summary
SELECT count(*) FROM sql_files; -- Total number of entries
SELECT Project_Name, count(*) FROM sql_files GROUP BY(Project_Name); -- Count of files per project
SELECT Project_Name, AVG(Additions), AVG(Deletions) FROM sql_files GROUP BY(Project_Name); -- Average additions and deletions per project
SELECT Project_Name, COUNT(DISTINCT(Author)) FROM sql_files GROUP BY(Project_Name); -- Count of unique authors per project

SELECT COUNT(DISTINCT(sql)) FROM sql_files GROUP BY(Project_name); -- Count of unique SQL queries per project
SELECT count(SQL_Change) FROM sql_files WHERE SQL_Change = TRUE GROUP BY(Project_Name); -- Count of files with SQL changes per project


-- Joins
SELECT project_name, sql_files.commit_hash, sql_files.file_name, fluff_summary 
FROM sql_files 
INNER JOIN sql_fluff ON sql_files.commit_hash = sql_fluff.commit_hash AND sql_files.file_name = sql_fluff.file_name; -- Join sql files with sqlfluff

SELECT project_name, sql_files.commit_hash, sql_files.file_name, lint_summary 
FROM sql_files 
INNER JOIN sql_lint ON sql_files.commit_hash = sql_lint.commit_hash AND sql_files.file_name = sql_lint.file_name; -- Join sql files with sqlint

SELECT project_name, sql_files.commit_hash, sql_files.file_name, check_summary
FROM sql_files 
INNER JOIN sql_check ON sql_files.commit_hash = sql_check.commit_hash AND sql_files.file_name = sql_check.file_name; -- Join sql files with sqlcheck