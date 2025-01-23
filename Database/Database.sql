DROP TABLE IF EXISTS sql_files;

CREATE TABLE sql_files (
    project_name VARCHAR(40),
    commit_hash VARCHAR(40),
    author VARCHAR(40),
    date_time TIMESTAMP,
    file_name VARCHAR(150),
    additions INTEGER,
    deletions INTEGER,
    sql VARCHAR(1000),
    sql_context BOOLEAN,
    sql_change BOOLEAN,
    PRIMARY KEY(commit_hash, file_name)
);

INSERT INTO sql_files SELECT * FROM CSVREAD('C:\\Users\\04sam\\OneDrive\\Documents\\Summer project\\summer-project\\Datasets\\sql-data.csv')