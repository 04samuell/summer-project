# Profiling of SQL Code Snippets

## Introduction

This repo contains all the code used in my summer project focused on gaining insights into the quality of SQL code in open source Apache Java projects.
This involved scraping SQL code and other metadata that was embedded into commit logs for these projects, converting each entry into its own <code>.sql</code> file, and running each file through three SQL checker tools.
The tools and their purpose is detailed below:
  - SQLint: Assessed how reliable the SQL code was and how well it conformed to programming rules & conventions.
  - SQLFluff: Assessed the Readability of the SQL code.
  - SQLCheck: Assessed the Performance and Security of the SQL code.

The next sections describe the general purpose of the code in each folder.

## SQLDataCollection

This folder contains the code used to scrape the SQL code. The input for this code are the commit log files for each project. These were stored in the subdirectory <code>ProjectCommitLogs</code> which contained 12 text files.
These text files were the commit logs and were obtained by cloning the Apache projects repository and running <code>git log -p > [project_name].txt</code>. The list of classes and their purpose is detailed below:
  - <code>SQLDataExtractor.java</code>: Contains the main method, loops through each project and contains the data structures to store the result.
  - <code>CommitLogParser.java</code>: Contains the SQL regex used to identify SQL statements. Given a text file it will remove all commits that don't contain SQL using this regex.
  - <code>CommitFormatter.java</code>: The most important file as it contains the logic to scrape an entry in the dataset. It takes a commit (which contains SQL) and will scrape all the relavent metadata and actual SQL String.
  - <code>CSVFileWriter.java</code>: Class to append data to a csv file. Whenever a project is finished parsing, its data will be added to this file. The resulting file (<code>sql-data.csv</code> is the full dataset that will go into the database).

## Database

## SATools

## Results
Subdirectory for storing the <code>.csv</code> and <code>.sql</code>. These were not uploaded to GitHub.
