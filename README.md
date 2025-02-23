# Profiling of SQL Code Snippets

## Introduction

This repo contains all the code used in my summer project focused on gaining insights into the quality of SQL code in open source Apache Java projects.
This involved scraping SQL code and other metadata that was embedded into commit logs for these projects, converting each entry into its own <code>.sql</code> file, and running each file through three SQL checker tools.
  - SQLint: Assessed how reliable the SQL code was and how well it conformed to programming rules & conventions.
  - SQLFluff: Assessed the Readability of the SQL code.
  - SQLCheck: Assessed the Performance and Security of the SQL code.

The next sections describe the general purpose of the code in each folder.

## SQLDataCollection

This folder contains the code used to scrape the SQL code. The input for this code are the commit log files for each project. These were stored in the subdirectory <code>ProjectCommitLogs</code> which contained 12 text files.
These text files were the commit logs and were obtained by cloning the Apache projects repository and running <code>git log -p > [project_name].txt</code>. 

The list of classes and their purpose is detailed below:
  - <code>SQLDataExtractor.java</code>: Contains the main method, loops through each project and contains the data structures to store the result.
  - <code>CommitLogParser.java</code>: Contains the SQL regex used to identify SQL statements. Given a text file it will remove all commits that don't contain SQL using this regex.
  - <code>CommitFormatter.java</code>: The most important file as it contains the logic to scrape an entry in the dataset. It takes a commit (which contains SQL) and will scrape all the relavent metadata and actual SQL String.
  - <code>CSVFileWriter.java</code>: Class to append data to a csv file. Whenever a project is finished parsing, its data will be added to this file. The resulting file (<code>sql-data.csv</code> is the full dataset that will go into the database).

## Database
This folder contains the code used to add to and query the database. 
  - <code>Shema.sql</code>: This SQL file contained all the SQL statement used to establish tables and upload them to the database.
  - <coee>Queries.sql</code>: This file contained all the SQL statements I used to query the database and produce summary data which was used in my analysis and final report.
  - <code>ConnectToH2.java</code>: This file contained the logic to connect to and query the database programatically. This class was used by otherclasses in the SATools folder to establish a database connection. It also contained the logic to put all the SQL statements from the dataset into their own seperate <code>.sql</code> file. After running this class, a new subdirectory is created inside the <code>datasets</code> directory which contains all the SQL entries in the database (including identifying information - commit hash and filename) inside their own <code>.sql</code> file.

## SATools
This folder was reponsible for running the SQL entries through the Static Analysis tools and summarising the results.
  - <code>SQLint.py</code>: Makes use of <code>subprocess</code> library to run each <code>.sql</code> file through the SQLint tool to assess the reliability of each SQL entry. For each files adds an entry to the resulting <code>sqlint-analysis.csv</code> file - this file will form a table in the database. Each entry includes information to identify each file (commit hash and filename), the raw output of the tool, and a summary of the tools output (see <code>ToolOutputParser.py</code>).
  - <code>SQLFluff.py</code>: Same as <code>SQLint.py</code> except using the SQLFluff tool to assess readability.
  - <code>SQLCheck.py</code>: Same as <code>SQLint.py</code> except using the SQL Check tool to assess performance and security.
  - <code>ToolOutputParser</code>: Helper file which contains one method for each of the three tools which extracts a summary dictionary (which maps code violation to frequency that it occured in the file). This is extremely useful as the output of the tools is different and so these summary dictionaries allow for the results to be easily tallied.
  - <code>ErrorCounter.java</code>: Queries the database for the tool summary columns and prints the violation and frequency for each project and overall.
  - <code>ErrorRow.java</code>: Added towards the end of the project. It is much more elegant to store each violation and its frequency as a seperate row in a database instead of the summary dictionaries which cannot easily be manipulated. This class creates these tables which allowed for easier querying. These tables were used to produce the full results tables in the report.  

## Datasets
Subdirectory for storing the <code>.csv</code> and <code>.sql</code>. These were not uploaded to GitHub.
