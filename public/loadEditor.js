(function loadEditor() {
    require.config({
        baseUrl: window.location.pathname,
        paths: {
            "jquery": "xtext/webjars/jquery.min",
            "ace/ext/language_tools": "xtext/webjars/ext-language_tools"
        }
    });

    require(["xtext/webjars/ace"], function () {
        require(["xtext/xtext-ace"], function (xtext) {
            var endpoint = {
                hash: "",
                host: "localhost:8080",
                hostname: "localhost",
                href: "http://localhost:8080/",
                origin: "http://localhost:8080",
                pathname: "/",
                port: "8080",
                protocol: "http:"
            };

            let xtextDiv = document.createElement("div");
            xtextDiv.id = "xtext-text";

            window.editorViewer = xtext.createEditor({
                baseUrl: window.location.pathname,
                syntaxDefinition: "xtext-resources/generated/mode-mydsl",
                xtextLang: "mydsl",
                parent: xtextDiv,
                endpoint: endpoint,
                enableCors: true
            });
        })
    });
})();


