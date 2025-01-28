import subprocess
import os
import glob

sql_files_directory = "Datasets/SQLFiles"  # Replace with your SQL files directory
output_filename = "SATools/sqlfluff_analysis.txt"  # Replace with your desired output file name

def analyze_sql_files(sql_files_dir, output_file):
    """Analyzes SQL files using sqlfluff and stores the output in a text file."""

    try:
        with open(output_file, "w") as outfile:  # Open file for writing (overwrites existing)
            sql_files = glob.glob(os.path.join(sql_files_dir, "*.sql"))
            sql_files.sort(key=os.path.getmtime) # Sort based on modification time          
            for sql_file in sql_files: 
                print(f"Analyzing: {sql_file}")  # Progress indicator

                try:
                    # Run sqlfluff command (adjust dialect if needed)
                    result = subprocess.run(
                        ["sqlfluff", "lint", "--dialect", "mysql", sql_file],
                        capture_output=True,
                        text=True,  # Decode output as text
                        check=False,  # Don't raise exception on non-zero exit code
                    )
                    
                    outfile.write(f"File: {sql_file}\n")
                    outfile.write(result.stdout) # Write the sqlfluff results to the output file
                    outfile.write("-" * 50 + "\n") # Separator between files

                except subprocess.CalledProcessError as e:

                    outfile.write(f"Error analyzing {sql_file}:\n")
                    outfile.write(e.stderr)
                    outfile.write("-" * 50 + "\n")
                    print(f"Error: {e}")

                except Exception as e:
                    print(f"An unexpected error occurred: {e}")
                    return

        print(f"Analysis complete. Results stored in {output_file}")

    except OSError as e:
        print(f"Error opening output file: {e}")


analyze_sql_files(sql_files_directory, output_filename)
    