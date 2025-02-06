import subprocess
import os
import glob
import ToolOutputParser as op

sql_files_directory = "Datasets/SQLFiles"  # Replace with your SQL files directory
output_filename = "Datasets/sqlint_analysis.csv"  # Replace with your desired output file name
sql_lint_path = "C:\\Users\\04sam\AppData\\Roaming\\npm\\sql-lint.cmd"
error_options = "--ignore-errors my-sql-invalid-create-option"
annoying_text = "must be one of \'\'[\"\"algorithm\"\",\"\"database\"\",\"\"definer\"\",\"\"event\"\",\"\"function\"\",\"\"index\"\",\"\"or\"\",\"\"procedure\"\",\"\"role\"\",\"\"server\"\",\"\"schema\"\",\"\"table\"\",\"\"tablespace\"\",\"\"temporary\"\",\"\"trigger\"\",\"\"user\"\",\"\"unique\"\",\"\"view\"\"]\'\'"
#error_options = ""

def analyze_sql_files(sql_files_dir, output_file):
    """Analyzes SQL files using sql-lint and stores the output in a text file."""

    try:
        with open(output_file, "w") as outfile:  # Open file for writing (overwrites existing)

            outfile.write("commit_hash,file_name,lint_output,lint_summary\n")  # Write header to output file

            sql_files = glob.glob(os.path.join(sql_files_dir, "*.sql"))
            sql_files.sort(key=os.path.getmtime) # Sort based on modification time          
            
            prev_project = ""
            for sql_file in sql_files:

                if(get_project(sql_file) != prev_project):
                   print(f"Analyzing {get_project(sql_file)}...")
                   prev_project = get_project(sql_file) 

                project_name = sql_file.split("\\")[1][:sql_file.rfind("-")] # Get project name from file name
                with open(sql_file, "r") as f: # Get commit hash and filename from file
                        lines = f.readlines()
                        metadata = lines[0].strip().removeprefix("--").split("   ")
                        hash = make_quotation(metadata[0].strip())
                        filename = make_quotation(metadata[1].strip())

                try: # Run SQLint on the file
                    result = subprocess.run(
                        [sql_lint_path, sql_file],
                        capture_output=True,
                        text=True,  # Decode output as text
                        check=False,  # Don't raise exception on non-zero exit code
                    )

                    result_std = result.stdout.replace("\"", "\"\"").replace("\'", "\'\'").replace(annoying_text, "")

                    if result_std != "" and not result_std.__contains__("sql-lint was unable to lint"):
                        result_std = make_quotation(result_std)
                        lint_summary = op.parse_lint_output(result_std)
                        lint_summary = lint_summary.replace("\"", "\"\"").replace("\'", "\'\'")
                        lint_summary = make_quotation(lint_summary)
                    else:
                        result_std = "NULL"
                        lint_summary = "NULL"

                    outfile.write(f"{hash},{filename},{result_std},{lint_summary}\n")

                except FileNotFoundError:
                    print("Error: sql-lint not found. Please install sql-lint using 'npm install -g sql-lint'.")
                    return
                
                except subprocess.CalledProcessError as e:

                    outfile.write(f"Error analyzing {sql_file}:\n")
                    outfile.write(e.stderr)
                    print(f"Error: {e}")

                except Exception as e:
                    print(f"An unexpected error occurred: {e}")
                    return

        print(f"Analysis complete. Results stored in {output_file}")

    except OSError as e:
        print(f"Error opening output file: {e}")


def get_project(file_name : str) -> str:
    """Get the project name from the file name."""
    return file_name.split("\\")[1].split("-")[0]

def make_quotation(input : str) -> str:
    """Add quotation marks around the input string."""
    return "\"" + input + "\""

analyze_sql_files(sql_files_directory, output_filename)
    