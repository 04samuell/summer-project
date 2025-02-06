import subprocess
import os
import glob
import ToolOutputParser as op

sql_files_directory = "Datasets/SQLFiles"  # Replace with your SQL files directory
output_filename = "Datasets/sqlfluff_analysis.csv"  # Replace with your desired output file name

def analyze_sql_files(sql_files_dir, output_file):
    """Analyzes SQL files using sqlfluff and stores the output in a text file."""

    try:
        with open(output_file, "w") as outfile:  # Open file for writing (overwrites existing)

            outfile.write("commit_hash,file_name,fluff_output,fluff_summary\n")  # Write header to output file

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

                try: # Run SQLFluff on the file
                    result = subprocess.run(
                        ["sqlfluff", "lint", "--dialect", "mysql", sql_file],
                        capture_output=True,
                        text=True,  # Decode output as text
                        check=False,  # Don't raise exception on non-zero exit code
                    )

                    result_std = result.stdout.replace("\"", "\"\"").replace("\'", "\'\'")

                    if result_std.__contains__("PASS"):
                        result_std = "NULL"
                        fluff_summary = "NULL"
                    else:
                        result_std = make_quotation(result_std)
                        fluff_summary = op.parse_fluff_output(result_std)
                        fluff_summary = fluff_summary.replace("\"", "\"\"").replace("\'", "\'\'")
                        fluff_summary = make_quotation(fluff_summary)

                    #fluff_summary = op.parse_fluff_output(result_std)
                    outfile.write(f"{hash},{filename},{result_std},{fluff_summary}\n")  # Write commit hash, file name, and sqlfluff output to output file

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
    