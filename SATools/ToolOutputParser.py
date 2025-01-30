def parse_fluff_output(output: str) -> str:
    """Parse the output of the SQLFluff tool."""
    """Return the error codes and their counts as a dictionary."""
    error_codes_count = {}
    errors_split = output.split("L:")
    errors_split = errors_split[1:]
    for error in errors_split:
        error = error.strip()
        parts = error.split("|")
        error_code = parts[2].strip()
        
        if error_code in error_codes_count:
            error_codes_count[error_code] += 1
        else:
            error_codes_count[error_code] = 1
        
    return str(error_codes_count)

def parse_lint_output(output: str) -> str:
    """Parse the ouput of the SQLint tool."""
    """Return the error codes and their counts as a dictionary."""
    error_codes_count = {}
    errors_split = output.split("Datasets/SQLFiles")
    errors_split = errors_split[1:]
    for error in errors_split:
        error = error.strip()
        start = error.find("[sql-lint: ") + len("[sql-lint: ")
        end = error.find("]")
        error_code = error[start:end].strip()
        if error_code in error_codes_count:
            error_codes_count[error_code] += 1
        else:
            error_codes_count[error_code] = 1

    return str(error_codes_count)

#test = "Datasets/SQLFiles\\asterixdb-3.sql:2 [sql-lint: my-sql-invalid-create-option] Option ''dataverse'' is not a valid option"
#print(parse_lint_output(test))