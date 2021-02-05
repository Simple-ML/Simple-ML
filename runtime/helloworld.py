from runtimeBridge import save_placeHolder



def helloworld():
    message="Hello World"
    save_placeHolder("message",message)
    return message