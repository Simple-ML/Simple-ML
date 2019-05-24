function loadEditor(xtextEndpoint, reactInitCallback) {
    require.config({
        baseUrl: window.location.pathname,
        paths: {
            "jquery": "xtext/webjars/jquery.min",
            "ace/ext/language_tools": "xtext/webjars/ext-language_tools"
        }
    });

    require(["xtext/webjars/ace"], function () {
        require(["xtext/xtext-ace"], function (xtext) {

            let xtextDiv = document.createElement("div");
            xtextDiv.id = "xtext-text";

            window.editorViewer = xtext.createEditor({
                baseUrl: window.location.pathname,
                syntaxDefinition: "xtext-resources/generated/mode-mydsl",
                xtextLang: "mydsl",
                parent: xtextDiv,
                endpoint: xtextEndpoint,
                enableCors: true
            });
            window.editorViewer.xtextServices.editorContext.setText("\n" +
                "source db1\n" +
                "source db2\n" +
                "source server12\n" +
                "\n" +
                "method ml1\n" +
                "method ml2\n" +
                "\n" +
                "collection someCollection db1[12] \"some text\" 987\n" +
                "\n" +
                "collection collection2 db2 \"some text\" 334444455\n" +
                "\n" +
                "function combine input db1 server12 ml-method ml1\n");
            window.editorViewer.xtextServices.update();
            reactInitCallback();
        })
    });
};


