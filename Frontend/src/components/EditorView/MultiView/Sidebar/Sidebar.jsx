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
          selectedEntityType: this.getEntityType(state.graphicalEditor.entitySelected?.data?.className),
          selectedEntityName: this.getEntityName(state.graphicalEditor.entitySelected),
          // TODO: Runtime not responding
          selectedPlaceholder: state.runtime.placeholder[state.graphicalEditor.entitySelected?.data?.className]
      };
    }    
    
    componentWillUnmount() {
        this.unsubscribe();
    }

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

    getEntityName = (entitySelected) => {
        console.log(entitySelected)
        if (entitySelected?.data?.name !== undefined) {
            return entitySelected.data.name;
        } else if (entitySelected?.data?.className !== undefined) {
            return entitySelected.data.className;
        } else {
            return '';
        }
    }

    render() {
            return(
              <div style= {{backgroundColor: 'white'}}>
                <header style= {{backgroundColor: 'white'}}>
                    <h1>{this.state.selectedEntityType}</h1>
                </header> 
                {/* Search Bar
                <Autocomplete
                    freeSolo
                    id="free-solo-2-demo"
                    disableClearable
                    options={['SpeedAverages']}
                    renderInput={(params) => (
                        <TextField
                        style= {{backgroundColor: 'white'}}
                            {...params}
                            label="Search input"
                            InputProps={{
                            ...params.InputProps,
                            type: 'search',
                            }}
                        />
                    )}
                />
                 */}
                <Accordion>
                    <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel1a-content"
                        id="panel1a-header"
                    >
                    <Typography>{this.state.selectedEntityName}</Typography>
                    </AccordionSummary>
                    <AccordionDetails>
                        <Typography>
                        </Typography>
                    </AccordionDetails>
                </Accordion>
              </div>
            )
      }
    }

Sidebar.propTypes = {
};

const mapStateToProps = state => {
    return {
    }
};

const mapDispatchToProps = dispatch => {
    return {
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Sidebar);
