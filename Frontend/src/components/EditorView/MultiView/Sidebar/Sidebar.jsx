import React from 'react';
import { connect } from 'react-redux';
import store from '../../../../reduxStore';
import './sidebar.scss'
import PropTypes from 'prop-types';
import Accordion from '@material-ui/core/Accordion';
import AccordionSummary from '@material-ui/core/AccordionSummary';
import AccordionDetails from '@material-ui/core/AccordionDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import Autocomplete from '@material-ui/lab/Autocomplete';
import TextField from '@material-ui/core/TextField';
import XtextServices from '../../../../serverConnection/XtextServices';
import Button from '@mui/material/Button';
import { showDataViewBackdrop } from '../../../../reducers/graphicalEditor';
import Icons from '../../../../stories/Icons';

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
          //selectedEntityType: this.getEntityType(state.graphicalEditor.entitySelected?.data?.className),
          //selectedEntityName: this.getEntityName(state.graphicalEditor.entitySelected),
          placeholders: this.getPlaceholders(state.runtime?.placeholder),
      };
    }    
    
    componentWillUnmount() {
        this.unsubscribe();
    }

    /*
    getEntityType = (entitySelectedClasName) => {
        switch(entitySelectedClasName) {
          case 'de.unibonn.simpleml.simpleML.SmlPlaceholder':
            return 'Dataset';
          case 'de.unibonn.simpleml.simpleML.SmlCall':
            return 'Process';
          default:
            return '';
        }
    }
    */

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

    handleOpen= () => {
        this.props.showDataViewBackdrop();
    }

    render() {
        const { placeholders } = this.state;
            return(
              <div style= {{backgroundColor: 'white'}}>
                <header style= {{backgroundColor: 'white'}}>
                    <h1>{'Select data set'}</h1>
                </header> 
                {
                <Autocomplete
                    freeSolo
                    id="free-solo-2-demo"
                    disableClearable
                    options={['WhiteWineQualityBinary']}
                    renderInput={(params) => (
                        <TextField
                            {...params}
                            label="Search input"
                            InputProps={{
                            ...params.InputProps,
                            type: 'search',
                            }}
                        />
                    )}
                />
                }
                { 
                Object.keys(placeholders).length !== 0 ? (
                    Object.keys(placeholders).map((placeholder) => (
                        placeholder ? (
                            <Accordion>
                                <AccordionSummary
                                    expandIcon={<ExpandMoreIcon />}
                                    aria-controls="panel1a-content"
                                    id="panel1a-header"
                                >
                                    <Typography>{placeholder}</Typography>
                                </AccordionSummary>
                                <AccordionDetails>
                                    <Typography>{placeholders[placeholder].id}</Typography>
                                    <Button onClick={this.handleOpen}>
                                        <Icons icons="tableChart"/>
                                    </Button>
                                </AccordionDetails>
                            </Accordion>
                        ): <div></div>
                    ))
                ): <div></div>}
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
