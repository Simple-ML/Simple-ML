def override_defaults(defaults, user_defined):
    if user_defined is None:
        user_defined = {}
    return {**defaults, **user_defined}