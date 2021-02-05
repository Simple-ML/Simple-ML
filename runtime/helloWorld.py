# Workflow steps ---------------------------------------------------------------
from runtimeBridge import save_placeHolder

def hello():
    message = 'Hello, world!'
    return message

# Workflows --------------------------------------------------------------------

def main():
    message = hello()
    save_placeHolder('message', message)