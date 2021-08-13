function loadEditor(reactInitCallback) {
    require.config({
        baseUrl: '',
        paths: {
            "jquery": "xtext/webjars/jquery.min",
            "ace/ext/language_tools": "xtext/webjars/ext-language_tools"
        }
    });

    require(["xtext/webjars/ace"], function () {
        require(["xtext/xtext-ace"], function (xtextEditor) {
            reactInitCallback(xtextEditor);
        })
    });
};


