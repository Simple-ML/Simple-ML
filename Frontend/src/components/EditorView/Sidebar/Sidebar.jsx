import React from 'react';
import { connect } from 'react-redux';
import store from '../../../reduxStore';
import './sidebar.scss'
import PropTypes from 'prop-types';
import Accordion from '@material-ui/core/Accordion';
import AccordionSummary from '@material-ui/core/AccordionSummary';
import AccordionDetails from '@material-ui/core/AccordionDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import Autocomplete from '@material-ui/lab/Autocomplete';
import TextField from '@material-ui/core/TextField';
import XtextServices from '../../../serverConnection/XtextServices';
import EmfModelHelper from '../../../helper/EmfModelHelper';
import Button from '@mui/material/Button';
import { showDataViewBackdrop } from '../../../reducers/graphicalEditor';
import Icons from '../../../stories/Icons';
import { event } from 'jquery';
import RuntimeService from '../../../serverConnection/RuntimeService'

class Sidebar extends React.Component {
    constructor(props) {
      super(props);

      this.onStoreChange = this.onStoreChange.bind(this);

      this.unsubscribe = store.subscribe(() => {
          this.setState(this.onStoreChange(store.getState()));
      });

      this.state = this.onStoreChange(store.getState());
    }

    onStoreChange = (state) => {

      return {
          selectedEntity: state.graphicalEditor.entitySelected,
          selectedEntityType: this.getEntityType(state.graphicalEditor.entitySelected?.data?.className),
          selectedEntityName: this.getEntityName(state.graphicalEditor.entitySelected),
          selectedEntityDataset: this.getDataset(state.graphicalEditor.entitySelected, state.runtime?.placeholder),
          placeholders: this.getPlaceholders(state.runtime?.placeholder),
          allAvailableDatasets: this.getAllDatasets(state.runtime?.placeholder.datasets)
      };
    }    
    
    componentDidMount() {
        RuntimeService.getAvailableDatasets("sessionId");
    }

    componentWillUnmount() {
        this.unsubscribe();
    }

    getEntityType = (entitySelectedClassName) => {
        switch(entitySelectedClassName) {
          case 'de.unibonn.simpleml.simpleML.SmlPlaceholder':
            return 'Dataset';
          case 'de.unibonn.simpleml.simpleML.SmlCall':
            return 'Load Dataset';
          default:
            return '';
        }
    }

    getEntityName = (entitySelected) => {
        if (entitySelected?.data?.name !== undefined) {
            return entitySelected.data.name;
        } else if (entitySelected?.data?.className !== undefined) {
            return entitySelected.data.className;
        } else {
            return '';
        }
    }

    getPlaceholders = (placeholders) => {
        if (placeholders !== undefined) {
            return placeholders;
        } else {
            return '';
        }
      
    }    
    
    getAllDatasets = (datasets) => {
        if (datasets !== undefined) { 
            const availableDatasets = [];
            for (let dataset of datasets) {
                availableDatasets.push(dataset.id);
            }
            return availableDatasets;
        } else {
            return [];
        }
      
    }

    getDataset = (selectedEntity, placeholders) => {
        for (const [key, value] of Object.entries(placeholders)) {
            if(key === selectedEntity?.data?.name) {
                return value;
            }
        }
        return '';
    }

    setDataset = (selectedEntity, dataset) => {
        const editProcessParameterDTO =  {
            entityPath: EmfModelHelper.getFullHierarchy(selectedEntity),
            parameterType: "string",
            parameterIndex: 0,
            value: dataset
        }
        console.log(editProcessParameterDTO)
        XtextServices.editProcessParameter(editProcessParameterDTO);
    }

    handleOpen= () => {
        this.props.showDataViewBackdrop();
    }

    handleSelection = (selectedItems) => {
        this.setState({ selectedFilter: selectedItems })
    };

    render() {
        const { selectedEntityName, selectedEntityType, allAvailableDatasets, selectedEntityDataset, selectedEntity, searchValue } = this.state;
            return(
                <div style= {{backgroundColor: 'white', width: '362px', height:'100vh', overflow: 'scroll'}}>
                    <header style= {{backgroundColor: 'white'}}>
                        <h1>{selectedEntityType ? selectedEntityType : ''}</h1>
                    </header>
                    { selectedEntityType !== undefined && selectedEntityType === 'Load Dataset' ?
                        <Autocomplete
                            style= {{paddingLeft: '5px', paddingRight: '5px', paddingBottom: '20px'}}
                            freeSolo
                            id="free-solo-2-demo"
                            disableClearable
                            options={allAvailableDatasets}
                            onSelect={event => {
                                const { value } = event.target;
                                this.setState({ searchValue: value });
                            }}
                            renderInput={(params) => (
                                <TextField
                                    {...params}
                                    label="Search input"
                                    InputProps={{
                                        ...params.InputProps,
                                        type: 'search',
                                    }}        
                                    onChange={event => {
                                        const { value } = event.target;
                                        this.setState({ searchValue: value });
                                    }}
                                />
                            )}
                        />
                    : <div></div>}
                    {
                        (() => {
                            switch(selectedEntityType) {
                                case 'Dataset':
                                    return (
                                        <Accordion>
                                            <AccordionSummary
                                                expandIcon={<ExpandMoreIcon />}
                                                aria-controls="panel1a-content"
                                                id="panel1a-header"
                                            >
                                                <Typography>{selectedEntityName}</Typography>
                                            </AccordionSummary>
                                            {selectedEntityDataset?
                                                <AccordionDetails>
                                                    <Typography>{selectedEntityDataset.id}</Typography>
                                                    <Button style= {{marginLeft: 'auto', marginRight: '0'}}onClick={this.handleOpen}>
                                                        <Icons icons="tableChart"/>
                                                    </Button>
                                                </AccordionDetails>
                                            :<div></div>}
                                        </Accordion>
                                    )
                                case 'Load Dataset':
                                    return (
                                        <Accordion>
                                            <AccordionSummary
                                                expandIcon={<ExpandMoreIcon />}
                                                aria-controls="panel1a-content"
                                                id="panel1a-header"
                                            >
                                                <Typography>{'Available Datasets'}</Typography>
                                            </AccordionSummary>
                                            {allAvailableDatasets.length !== 0 ? (
                                                allAvailableDatasets).map((dataset) => (
                                                    dataset && (searchValue !== undefined ? dataset.toLowerCase().includes(searchValue.toLowerCase()) : true) ? (
                                                        <AccordionDetails>
                                                            <Typography style= {{cursor: 'pointer'}} /*onClick={this.handleOpen}*/>{dataset}</Typography>
                                                            {<Button 
                                                                style= {{marginLeft: 'auto', marginRight: '0'}}
                                                                onClick={() => {this.setDataset(selectedEntity, dataset)}}>
                                                                <Icons icons="checkCircle"/>
                                                            </Button>}
                                                        </AccordionDetails>
                                                    ): <div></div>
                                                )
                                            ): <div></div>
                                            }
                                        </Accordion>
                                    )
                                default:
                                    break;
                            }
                        })()
                    }
                </div>
            )
    }
}

Sidebar.propTypes = {
};

const mapStateToProps = state => {
    return {
        showDataViewBackdrop: PropTypes.func.isRequired
    }
};

const mapDispatchToProps = dispatch => {
    return {
        showDataViewBackdrop: () => dispatch(showDataViewBackdrop())
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Sidebar);
