import sys
import signal
from io import StringIO

class TimeoutExpired(Exception):
    """Custom exception to signal that the execution timeout has been reached."""
    pass

def alarm_handler(signum, frame):
    """
    Signal handler function. Raises TimeoutExpired when the alarm goes off.

    Args:
        signum: The signal number (not used in this case).
        frame: The current stack frame (not used in this case).
    """
    raise TimeoutExpired

def main(code, timeout=2):
    """
    Executes the provided Python code with a timeout to prevent infinite loops.

    Args:
        code (str): The Python code to execute.
        timeout (int): The maximum execution time in seconds.

    Returns:
        str: The captured output or an error message.
    """
    # Capture stdout: Redirect standard output to a StringIO object.
    old_stdout = sys.stdout
    sys.stdout = captured_output = StringIO()

    # Set up the signal handler and alarm:
    # - signal.signal(): Registers the alarm_handler function to be called when SIGALRM is received.
    # - signal.alarm(): Sets an alarm that will send SIGALRM after 'timeout' seconds.
    signal.signal(signal.SIGALRM, alarm_handler)
    signal.alarm(timeout)

    try:
        # Execute the provided Python code:
        # - exec(): Executes the code string as Python code.
        exec(code)
    except TimeoutExpired:
        # Handle timeout: If the alarm goes off, this block is executed.
        return "Error: Execution timed out (infinite loop or too long execution)"
    except Exception as e:
        # Handle other exceptions: If any other error occurs during execution, this block is executed.
        return f"Error: {e}"
    finally:
        # Restore stdout: Regardless of whether an error occurred or not, restore the original stdout.
        sys.stdout = old_stdout
        # Disable the alarm: Ensure the alarm is turned off after execution.
        signal.alarm(0)

    # Return the captured output: Get the content from the StringIO object and return it.
    return captured_output.getvalue()