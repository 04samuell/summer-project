import re

def parse_fluff_output(output: str) -> str:
    """Parse the output of the SQLFluff tool."""
    """Return the error codes and their counts as a dictionary."""
    error_codes_count = {}
    error_count = 0
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
        error_count += 1

    error_codes_count["TOTAL"] = error_count
        
    return str(error_codes_count)

def parse_lint_output(output: str) -> str:
    """Parse the ouput of the SQLint tool."""
    """Return the error codes and their counts as a dictionary."""
    error_codes_count = {}
    error_count = 0
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
        error_count += 1
    
    error_codes_count["TOTAL"] = error_count

    return str(error_codes_count)

def parse_check_output(output: str) -> str:
    """Parse the output of the SQLCheck tool."""
    """Return the error codes and their counts as a dictionary."""
    error_codes_count = {}
    error_count = 0
    results_index = output.find("==================== Results ===================")
    output = output[results_index:]
    
    if "No issues found" in output:
        return str(error_codes_count)
    
    lines = output.split("\n")
    for line in lines:
        if line.startswith("[Datasets/SQLFiles"):
            error_code = line.split(")")[2].strip()
            if error_code in error_codes_count:
                error_codes_count[error_code] += 1
            else:
                error_codes_count[error_code] = 1
            error_count += 1

    error_codes_count["TOTAL"] = error_count

    return str(error_codes_count)

def remove_unncessary_check_info(output: str) -> str:
    """Remove the unnecessary information from the output of the SQLCheck tool."""
    result_str = "==================== Results ==================="
    results_index = output.find(result_str) + len(result_str)
    output = output[results_index:]
    if "No issues found" in output:
        return "No issues found"
    
    result = ""
    error_split = output.split("-------------------------------------------------")
    error_split = error_split[1:]
    for error in error_split:
        lines = error.split("\n")
        for line in lines:
            result += line + "\n"
            if line.startswith("[Datasets/SQLFiles"):
                break
    
    return result
