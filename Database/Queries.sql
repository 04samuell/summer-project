-- Data summary
SELECT count(*) FROM sql_files; -- Total number of entries
SELECT Project_Name, count(*) FROM sql_files GROUP BY(Project_Name); -- Count of files per project
SELECT Project_Name, AVG(Additions), AVG(Deletions) FROM sql_files GROUP BY(Project_Name); -- Average additions and deletions per project
SELECT Project_Name, COUNT(DISTINCT(Author)) FROM sql_files GROUP BY(Project_Name); -- Count of unique authors per project

SELECT COUNT(DISTINCT(sql)) FROM sql_files GROUP BY(Project_name); -- Count of unique SQL queries per project
SELECT count(SQL_Change) FROM sql_files WHERE SQL_Change = TRUE GROUP BY(Project_Name); -- Count of files with SQL changes per project


-- Joins
SELECT project_name, sql_files.commit_hash, sql_files.file_name, lint_summary 
FROM sql_files 
INNER JOIN sql_lint ON sql_files.commit_hash = sql_lint.commit_hash AND sql_files.file_name = sql_lint.file_name; -- Join sql files with sqlint

SELECT project_name, sql_files.commit_hash, sql_files.file_name, fluff_summary 
FROM sql_files 
INNER JOIN sql_fluff ON sql_files.commit_hash = sql_fluff.commit_hash AND sql_files.file_name = sql_fluff.file_name; -- Join sql files with sqlfluff

SELECT project_name, sql_files.commit_hash, sql_files.file_name, check_summary
FROM sql_files 
INNER JOIN sql_check ON sql_files.commit_hash = sql_check.commit_hash AND sql_files.file_name = sql_check.file_name; -- Join sql files with sqlcheck

-- Queries for particular violations
SELECT project_name, sql, lint_summary FROM (
    SELECT project_name, sql_files.sql, sql_files.commit_hash, sql_files.file_name, lint_summary 
    FROM sql_files 
    INNER JOIN sql_lint ON sql_files.commit_hash = sql_lint.commit_hash AND sql_files.file_name = sql_lint.file_name 
)
WHERE Lint_Summary LIKE '%invalid-create-option%' AND Project_Name NOT LIKE '%asterixdb%'; -- SQLLint

SELECT project_name, sql, Fluff_summary FROM (
    SELECT project_name, sql_files.sql, sql_files.commit_hash, sql_files.file_name, fluff_summary 
    FROM sql_files 
    INNER JOIN sql_fluff ON sql_files.commit_hash = sql_fluff.commit_hash AND sql_files.file_name = sql_fluff.file_name 
)
WHERE Fluff_Summary LIKE '%LT05%' AND Project_Name NOT LIKE '%asterixdb%'; -- SQLFluff

SELECT project_name, sql, Check_summary FROM (
    SELECT project_name, sql_files.sql, sql_files.commit_hash, sql_files.file_name, check_summary 
    FROM sql_files 
    INNER JOIN sql_check ON sql_files.commit_hash = sql_check.commit_hash AND sql_files.file_name = sql_check.file_name 
)
WHERE Check_Summary LIKE '%Index Attribute Order%' AND Project_Name NOT LIKE '%asterixdb%'; -- SQLCheck


-- Select all SQL and error summaries from all three tools
SELECT project_name, sql, Lint_Summary, Fluff_Summary, Check_summary, FROM (
    SELECT project_name, sql_files.sql, sql_files.commit_hash, sql_files.file_name, check_summary, lint_summary
    FROM sql_files 
    INNER JOIN sql_lint ON sql_files.commit_hash = sql_lint.commit_hash AND sql_files.file_name = sql_lint.file_name
    INNER JOIN sql_fluff ON sql_files.commit_hash = sql_fluff.commit_hash AND sql_files.file_name = sql_fluff.file_name
    INNER JOIN sql_check ON sql_files.commit_hash = sql_check.commit_hash AND sql_files.file_name = sql_check.file_name 
)

-- Error Summary (Used to create tables of full results, repeat for each project then perform pivot detailed below)
SELECT project_name, error, sum(count) FROM LINT_ERROR_ROWS 
WHERE project_name LIKE '%asterixdb%'
GROUP BY error -- SQLLint

SELECT project_name, error, sum(count) FROM FLUFF_ERROR_ROWS 
WHERE project_name LIKE '%asterixdb%'
GROUP BY error -- SQLFluff

SELECT project_name, error, sum(count) FROM CHECK_ERROR_ROWS 
WHERE project_name LIKE '%asterixdb%' 
GROUP BY error -- SQLCheck

-- copy + paste into excel (project by project)
-- highlight table then
-- From table/range -> select error col -> Transform tab -> pivot column -> values from sum(count) --> Summary table complete
