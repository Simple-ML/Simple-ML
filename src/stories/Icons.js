import React from 'react';

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
export default function Icons() {
	const classes = useStyles();
	return (
		<div>
			<CloseIcon style={{fontSize:24}}/ >
			<ChevronLeftIcon style={{fontSize:24}}/ >
			<KeyboardArrowUpIcon style={{fontSize:24}}/ >
			<KeyboardArrowDownIcon style={{fontSize:24}}/ >
			<CancelIcon style={{fontSize:24}}/ >
			<ErrorIcon style={{fontSize:24}}/ >
			<CheckCircleIcon style={{fontSize:24}}/ >
			<br/>
			<ArrowDropUpIcon style={{fontSize:24}}/ >
			<ArrowDropDownIcon style={{fontSize:24}}/ >
			<AddIcon style={{fontSize:24}}/ >
			<SearchIcon style={{fontSize:24}}/ >
			<FilterListIcon style={{fontSize:24}}/ >
			<FullscreenIcon style={{fontSize:24}}/ >
			<FullscreenExitIcon style={{fontSize:24}}/ >
		</div>
	);
}
