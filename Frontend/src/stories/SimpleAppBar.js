import { createMuiTheme,ThemeProvider } from '@material-ui/core/styles';
import purple from '@material-ui/core/colors/purple';
import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import Avatar from '@material-ui/core/Avatar';
import Icons from './Icons.js';
import '../componentCss.css';
import PropTypes from 'prop-types';

const theme = createMuiTheme({
	// muiTheme would create the darker and ligher shades for us  we only rly need to input
	// the main color for primary and secondary
  palette: {
    primary: {
      light: '#533b57',
      main: '#290b2e',
      dark: '#1c0720',
      contrastText: '#fff',
    },
    secondary: {
      light: '#ec7147',
      main: '#e84e1a',
      dark: '#a23612',
      contrastText: '#000',
    },
  },
});
const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
  },
	AppBar:{
		justifyContent:'space-between'
	},
	AppBar__logoTitle:{
		marginRight:'5vw'
	},
	AppBar__title:{
	},
	AppBar__icons:{
	},
	AppBar__icon:{
	},
	AppBar__iconsContainer:{
		display:'flex',
		alignItems: 'center',
		justifyContent: 'space-evenly',
		width: '15vw'
	},
  small: {
    width: theme.spacing(3),
    height: theme.spacing(3),
  },
}));
export default function SimpleAppBar(props) {
 const classes = useStyles();
	console.log(classes);
  return (
		<ThemeProvider theme={theme}>
			<AppBar position="static">
        <Toolbar className={classes.AppBar}>
          <Typography color="secondary" variant="h6" className= {classes.AppBar__logoTitle} >
						{props.logoTitle}
          </Typography>
					<div className="AppBar__title">
						<Typography variant="h6" className= {classes.AppBar__title}>
							{props.title}
						</Typography>
					</div>
		<div className={classes.AppBar__iconsContainer}>
							{props.icons.map((element) => { 
								return (
								<Icons  icons={element}></Icons>
									)
							}) }
						<Avatar alt="Username" src="anzumana.png" className={classes.medium} />
		</div>
        </Toolbar>
      </AppBar>
		</ThemeProvider>
  );
}
SimpleAppBar.defaultProps = {
	icons:['starOutline','helpOutline'],
	title: 'Traffic Speed Prediction Niedersachen',
	logoTitle: 'SIMPLE-ML'
};
SimpleAppBar.propTypes = {
  /**
   * Array of strings for the icons that should be displayed
   */
  icons: PropTypes.array,
  /**
	 * String for the title
   */
  title: PropTypes.string,
  /**
   * Logo string for the project name
   */
  logoTitle: PropTypes.string,
};
