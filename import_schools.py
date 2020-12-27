import sys
import os
import csv

MSG_HELP = "python3 import_schools.py <input_file> <output_file>"
INSERT_STR = "INSERT INTO schools (name, address, country) VALUES ('{0}', '{1}', '{2}');\n"

COUNTRY_MAPPING = {
    "Antigua": "ANTIGUA_AND_BARBUDA",
    "Dominica": "DOMINICA",
    "Grenada": "GRENADA",
    "Nevis": "ST_KITTS_AND_NEVIS",
    "St. Kitts": "ST_KITTS_AND_NEVIS",
    "St. Lucia": "ST_LUCIA",
    "St. Vincent and the Grenadines": "ST_VINCENT_AND_THE_GRENADINES",
}


def main():
    if len(sys.argv) == 2:
        arg = sys.argv[1]
        if arg == "-h" or arg == "--help":
            print(MSG_HELP)
            return

    if len(sys.argv) != 3:
        print("Invalid arguments given, expected usage:")
        print(MSG_HELP)
        exit(-1)

    arg_input = sys.argv[1]
    arg_output = sys.argv[2]

    # Check to make sure the given file exists
    if not os.path.exists(arg_input):
        print("Failed to find file:", arg_input)
        exit(-1)

    output_lines = []

    # Open the given file
    with open(arg_input, "r", encoding="utf-8-sig") as file_csv:
        col_school = -1
        col_country = -1

        # Iterate over ever line in the CSV
        for line_split in csv.reader(file_csv):
            # Skip lines with no comma-separation
            if len(line_split) == 0:
                continue

            # Find the columns that list school names and countries
            if col_school == -1 or col_country == -1:
                for col in range(0, len(line_split)):
                    header = line_split[col].lower()
                    if 'school' in header:
                        col_school = col
                    elif 'country' in header:
                        col_country = col
                continue

            country_raw = line_split[col_country].strip()
            if country_raw not in COUNTRY_MAPPING:
                print("Unknown country found:", country_raw)
                exit(1)

            address = ""
            name = line_split[col_school].strip().replace("'", "''")
            country = COUNTRY_MAPPING[country_raw]

            insert_statement = INSERT_STR.format(name, address, country)
            output_lines.append(insert_statement)

        if col_school == -1 or col_country == -1:
            print("Failed to find columns for school and country")
            exit(-1)

    if len(output_lines) == 0:
        print("Failed to process input file for valid data")
        exit(-1)

    # Write the INSERT statements to the output file
    with open(arg_output, "w") as file_csv:
        file_csv.writelines(output_lines)

    print("Wrote {0} lines to '{1}'".format(len(output_lines), arg_output))


main()
