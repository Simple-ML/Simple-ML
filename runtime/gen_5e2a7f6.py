# Imports ----------------------------------------------------------------------

from runtimeBridge import save_placeHolder

# Workflow steps ---------------------------------------------------------------

def hello():
    message = 'Hello, world!'
    return message

# Workflows --------------------------------------------------------------------

def main():
    message = hello()
    save_placeHolder('message', message)
