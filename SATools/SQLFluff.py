import subprocess
import os
import glob

sql_files_directory = "Datasets/SQLFiles"  # Replace with your SQL files directory
output_filename = "SATools/sqlfluff_analysis.csv"  # Replace with your desired output file name

def analyze_sql_files(sql_files_dir, output_file):
    """Analyzes SQL files using sqlfluff and stores the output in a text file."""

    try:
        with open(output_file, "w") as outfile:  # Open file for writing (overwrites existing)

            outfile.write("commit_hash,file_name,fluff_output\n")  # Write header to output file

            sql_files = glob.glob(os.path.join(sql_files_dir, "*.sql"))
            sql_files.sort(key=os.path.getmtime) # Sort based on modification time          
            
            for sql_file in sql_files:
                print(f"Analyzing {sql_file}...")
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
                    outfile.write(f"{hash},{filename},{make_quotation(result_std)}\n")  # Write commit hash, file name, and sqlfluff output to output file

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


def make_quotation(input : str) -> str:
    """Add quotation marks around the input string."""
    return "\"" + input + "\""

analyze_sql_files(sql_files_directory, output_filename)
    