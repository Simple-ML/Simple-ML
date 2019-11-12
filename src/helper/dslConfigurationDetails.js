export default [
    {
        className: "de.unibonn.simpleml.simpleML.Workflow",
        getValue:function(){
            return "Workflow"
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.Expression",
        getValue:function(){
            return "Expression"
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.ProcessCall",
        getValue:function(){
            return this.data.ref
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.Assignment",
        getValue:function(){
            return this.data.name
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.Reference",
        getValue:function(){
            let ref = this.children[0].data['$ref']
            if(ref !== undefined){
                ref = ref.replace("//@", "");
                var refArrayAndIndex = ref.split(".");
                let name = refArrayAndIndex[0] + "[" + refArrayAndIndex[1] + "]";
                let workflow = this.parent;
                while(workflow.parent !== undefined){
                    workflow = workflow.parent
                }
                var value = workflow.children.filter(entity => entity["self"] === name)[0].getValue()
                return value;
            } else return "";
 
        }
    },
    //arraylike classes
    {
        className: "de.unibonn.simpleml.simpleML.DictionaryLiteral",
        getValue:function(){
            let dictionaryValue = "{ \n";
            for (let i in this.children){
                let childEntity = this.children[i];
                let childKey = childEntity.data.key;
                dictionaryValue+=childKey + ": ";
                let childValue = childEntity.children[0].getValue();
                dictionaryValue+=childValue + ";\n";
            }
            dictionaryValue+="}";
            return dictionaryValue;
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.ArrayLiteral",
        getValue:function(){
            let arrayValue = "[ ";
            for (let i in this.children){
                let childEntity = this.children[i];
                let childValue = childEntity.getValue();
                arrayValue+=childValue + " ";
            }
            arrayValue+="]"
            return arrayValue
        }
    },
    //elementary classes
    {
        className: "de.unibonn.simpleml.simpleML.IntegerLiteral",
        getValue:function(){
            return this.data.value
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.FloatLiteral",
        getValue:function(){
            return this.data.value
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.StringLiteral",
        getValue:function(){
            let stringValue = '"';
            stringValue+=this.data.value
            stringValue+=  '"';
            return stringValue;
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.BooleanLiteral",
        getValue:function(){
            return this.data.true
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.DateLiteral",
        getValue:function(){
            let dateString = this.data.year + "-" +this.data.month + "-" + this.data.day;
            return dateString
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.TimeLiteral",
        getValue:function(){
            return "Function not supported yet"
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.DateTimeLiteral",
        getValue:function(){
            return "Function not supported yet"
        }
    },
    {
        className: "de.unibonn.simpleml.simpleML.Seconds",
        getValue:function(){
            return "Function not supported yet"
        }
    },

]