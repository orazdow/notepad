import csv
import json
import sys
from datetime import datetime, timezone
from pathlib import Path


def convert_note_csv(input_path):
    output_path = input_path.with_name(f"{input_path.stem}-backup.json")
    if output_path.exists():
        print(f"Conflict: {output_path} already exists")
        return 1

    notes = []
    with input_path.open("r", encoding="utf-8-sig", newline="") as csv_file:
        reader = csv.DictReader(csv_file)
        required_fields = {"date", "title", "content", "password"}
        if reader.fieldnames is None or set(reader.fieldnames) != required_fields:
            print("Invalid CSV: expected fields date,title,content,password")
            return 1

        for row in reader:
            notes.append({
                "id": len(notes) + 1,
                "title": row.get("title") or "",
                "content": row.get("content") or "",
                "date": row.get("date") or "",
                "password": row.get("password") or "",
            })

    backup = {
        "format": "clemm-notepad-backup",
        "version": 1,
        "exportedAt": datetime.now(timezone.utc).isoformat(),
        "notes": notes,
    }

    with output_path.open("w", encoding="utf-8") as json_file:
        json.dump(backup, json_file, ensure_ascii=False, indent=2)
        json_file.write("\n")

    print(f"Wrote {output_path} with {len(notes)} notes")
    return 0


def main():
    if len(sys.argv) != 2:
        print("Usage: python notecsvtojson.py <notes.csv>")
        return 1

    input_path = Path(sys.argv[1])
    if not input_path.exists():
        print(f"Input file not found: {input_path}")
        return 1
    if input_path.is_dir():
        print(f"Input path is a directory: {input_path}")
        return 1

    return convert_note_csv(input_path)


if __name__ == "__main__":
    raise SystemExit(main())
