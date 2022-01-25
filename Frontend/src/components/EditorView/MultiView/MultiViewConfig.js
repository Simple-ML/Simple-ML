
// React.Components
import GraphicalEditor from '../GraphicalEditor/GraphicalEditor';
import DetailsEditor from "../DetailsEditor/DetailsEditor";
import TextEditor from '../TextEditor/TextEditor';

// Icons
import graphicalEditorIcon from '../../../images/toolbar/graph-viewbar.svg';
import textEditorIcon from '../../../images/toolbar/text-viewbar.svg';
import detailViewIcon from '../../../images/toolbar/chart-viewbar.svg';

class MultiViewConfig {

    componentConfigList = [];

    addComponentConfig = (config, component) => {
        this.componentConfigList.push({
            config,
            component
        });
    }

    getComponentConfigByName = (name) => {
        return this.componentConfigList.filter((item) => {
            return item.config.component === name;
        })[0];
    }

    getComponentConfigList = () => {
        return this.componentConfigList;
    }

    getPureConfigList = () => {
        return this.componentConfigList.map((item) => {
            return item.config;
        });
    }
}

const multiViewConfig = new MultiViewConfig();

multiViewConfig.addComponentConfig(
    {
        title: "Graphical Editor",
        type: "react-component",
        component: "graphicalEditor",
        icon: graphicalEditorIcon
    },
    GraphicalEditor
);

multiViewConfig.addComponentConfig(
    {
        title: "Text-Editor",
        type: "react-component",
        component: "textEditor",
        icon: textEditorIcon
    },
    TextEditor
);

multiViewConfig.addComponentConfig(
    {
        title: "Details",
        type: "react-component",
        component: "detailsEditor",
        icon: detailViewIcon
    },
    DetailsEditor
);

export default multiViewConfig;