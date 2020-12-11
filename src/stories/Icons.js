import React from 'react';
import PropTypes from 'prop-types';

import { makeStyles } from '@material-ui/core/styles';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';
import ArrowDropUpIcon from '@material-ui/icons/ArrowDropUp';
import  DeleteIcon  from '@material-ui/icons/Delete';
import CloseIcon from '@material-ui/icons/Close';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ErrorIcon from '@material-ui/icons/Error';
import KeyboardArrowUpIcon from '@material-ui/icons/KeyboardArrowUp';
import KeyboardArrowDownIcon from '@material-ui/icons/KeyboardArrowDown';
import CancelIcon from '@material-ui/icons/Cancel';
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import AddIcon from '@material-ui/icons/Add';
import SearchIcon from '@material-ui/icons/Search';
import FilterListIcon from '@material-ui/icons/FilterList';
import FullscreenIcon from '@material-ui/icons/Fullscreen';
import FullscreenExitIcon from '@material-ui/icons/FullscreenExit';
const useStyles = makeStyles((theme) => ({
  root: {
    color: theme.palette.text.primary,
  },
}));
export default function Icons(props) {
	const classes = useStyles();
	console.log(props);
	// TODO should not include all props but only those related to style
	let style = {
		...props
	}
	switch(props.icons){
		case 'fullscreenExit':
			return (
				<FullscreenExitIcon style={style}/ >
			);
		case 'chevronLeft':
			return (
				<ChevronLeftIcon style={style}/ >
			);
		case 'close':
			return (
				<CloseIcon style={style}/ >
			);
		case 'keyboardArrowUp':
			return (
				<KeyboardArrowUpIcon style={style}/ >
			);
		case 'keyboardArrowDown':
			return (
				<KeyboardArrowUpIcon style={style}/ >
			);
		case 'cancel':
			return (
				<CancelIcon style={style}/ >
			);
		case 'error':
			return (
				<ErrorIcon style={style}/ >
			);
		case 'checkCircle':
			return (
				<CheckCircleIcon style={style}/ >
			);
		case 'arrowDropUp':
			return (
				<ArrowDropUpIcon style={style}/ >
			);
		case 'arrowDropDown':
			return (
				<ArrowDropDownIcon style={style}/ >
			);
		case 'add':
			return (
				<AddIcon style={style}/ >
			);
		case 'search':
			return (
				<SearchIcon style={style}/ >
			);
		case 'filterList':
			return (
				<FilterListIcon style={style}/ >
			);
		case 'fullscreen':
			return (
				<FullscreenIcon style={style}/ >
			);
		default: 
			console.debug('Icon Name does not exist renders null')
			return null
	}
}
Icons.propTypes = {
  /**
   * How large should the icon be?
   */
  fontSize: PropTypes.oneOf([24, 40, 100]),
  /**
   * What icon should we display? For icon names see: https://material-ui.com/components/material-icons/
   */
  icons: PropTypes.string.isRequired,
  /**
   * Optional click handler
   */
  onClick: PropTypes.func,
};
Icons.defaultProps = {
  onClick: undefined,
	fontSize:24 ,
	icons:''
};
