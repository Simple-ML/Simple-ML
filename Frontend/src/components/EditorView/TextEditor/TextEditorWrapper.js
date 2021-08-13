import xtextEndpoint from '../../../serverConnection/xtextEndpoint';


//TODO: bessere Lösung finden als static-class
class TextEditorWrapper {

    static editor;
    static editorDiv;

    static create(xtextEditor) {
        TextEditorWrapper.editorDiv = window.jQuery('<div id="xtext-editor"></div>', document);

        TextEditorWrapper.editor = xtextEditor.createEditor({
            baseUrl: window.location.pathname,
            syntaxDefinition: xtextEndpoint.syntaxDefinition,
            xtextLang: xtextEndpoint.xtextLang,
            enableFormattingAction: true,
            parent: TextEditorWrapper.editorDiv,
            endpoint: xtextEndpoint,
            enableCors: true
        });
    }

    static setText(text) {
        TextEditorWrapper.editor.xtextServices.editorContext.setText(text);
    }
}

export default TextEditorWrapper;
