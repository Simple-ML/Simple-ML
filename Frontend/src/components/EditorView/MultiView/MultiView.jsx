
// node_modules
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import $ from "jquery";
import Accordion from '@material-ui/core/Accordion';
import AccordionSummary from '@material-ui/core/AccordionSummary';
import AccordionDetails from '@material-ui/core/AccordionDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import Autocomplete from '@material-ui/lab/Autocomplete';
import TextField from '@material-ui/core/TextField';

// React.Components
import GoldenLayoutComponent from './../../../helper/goldenLayoutServices/goldenLayoutComponent';
import SideToolbar from './SideToolbar/SideToolbar';

// Styles
import './multiView.scss';

// Config
import MultiViewConfig from './MultiViewConfig';

class MultiView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    wrapComponent = Component => {
        class Wrapped extends React.Component {
            render() {
                return (
                    <Component {...this.props}/>
                );
            }
        }
        return Wrapped;
    }

    whatToShowAtStartup = () => {
        return this.props.showAtStartup.map((item) => {
            return MultiViewConfig.getComponentConfigByName(item).config;
        });
    }

    render() {
        return (
            <div className={'multi-view-container'}>
                <SideToolbar componentConfigs={MultiViewConfig.getPureConfigList()} layout={this.state.myLayout} />
                <GoldenLayoutComponent
                    htmlAttrs={{ style: { minHeight: "780px", width: "100%" } }}
                    config={{
                        dimensions:{
                            headerHeight: "100%"
                        },
                        content:[{
                            type: "row",
                            content: [
                                {
                                    type: 'column',
                                    content: this.whatToShowAtStartup()
                                },
                            ]
                        }]
                    }}
                    registerComponents={myLayout => {
                        MultiViewConfig.getComponentConfigList().forEach((item) => {
                            myLayout.registerComponent(item.config.component, this.wrapComponent(item.component));
                        });
                        this.setState({myLayout});
                        /*
                        * Since our layout is not a direct child
                        * of the body we need to tell it when to resize
                        */
                        $(window).on("resize", function(){
                            myLayout.updateSize();
                        })
                    }}
                />
                <div style= {{backgroundColor: 'white'}}>
                    <header style= {{backgroundColor: 'white'}}>
                        <h1>Select data set</h1>
                        <p>Please select a data set</p>
                    </header> 
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
                    <Accordion>
                        <AccordionSummary
                            expandIcon={<ExpandMoreIcon />}
                            aria-controls="panel1a-content"
                            id="panel1a-header"
                        >
                        <Typography>SpeedAverages</Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                            <Typography>
                                Straßensegment (OpenStreetMap-ID)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Ortstyp (Bezeichnung)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Geschwindigkeitsbegrenzung (hat Geschwindigkeit)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Zeit (Zeit)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Zeit (Zeit)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Verkehrsfluss (Anzahl von Aufzeichnungen)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Verkehrsfluss (Fahrzeuganzahl)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Verkehrsfluss (Durchschnittsgeschwindigkeit)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Wetteraufzeichnung (Saison)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Wetteraufzeichnung (Tageslicht)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Zeit (am Wochenende)
                            </Typography>
                        </AccordionDetails>
                        <AccordionDetails>
                            <Typography>    
                                Straßensegment (im Well-known-Binary-(WKB)-Format)
                            </Typography>
                        </AccordionDetails>
                    </Accordion>
                </div>
            </div>
        )
    }
}


MultiView.propTypes = {

};

const mapStateToProps = state => {
    return {
    }
};

const mapDispatchToProps = dispatch => {
    return {
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(MultiView);
