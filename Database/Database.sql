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

INSERT INTO sql_files SELECT * FROM CSVREAD('C:\\Users\\04sam\\OneDrive\\Documents\\Summer project\\summer-project\\Datasets\\sql-data.csv')