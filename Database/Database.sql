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

-- Data summary
SELECT count(*) FROM sql_files; -- Total number of entries
SELECT Project_Name, count(*) FROM sql_files GROUP BY(Project_Name); -- Count of files per project
SELECT Project_Name, AVG(Additions), AVG(Deletions) FROM sql_files GROUP BY(Project_Name); -- Average additions and deletions per project
SELECT Project_Name, COUNT(DISTINCT(Author)) FROM sql_files GROUP BY(Project_Name); -- Count of unique authors per project

SELECT COUNT(DISTINCT(sql)) FROM sql_files GROUP BY(Project_name); -- Count of unique SQL queries per project
SELECT count(SQL_Change) FROM sql_files WHERE SQL_Change = TRUE GROUP BY(Project_Name); -- Count of files with SQL changes per project