import sys
from io import StringIO

def main(code):
    # Capture stdout
    old_stdout = sys.stdout
    sys.stdout = captured_output = StringIO()

    # Execute the provided Python code
    try:
        exec(code)
    except Exception as e:
        # Handle exceptions and return error message
        return f"Error: {e}"
    finally:
        # Restore stdout
        sys.stdout = old_stdout

    # Return the captured output
    return captured_output.getvalue()