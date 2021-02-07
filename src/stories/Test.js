import React from 'react';

import { makeStyles } from '@material-ui/core/styles';
import  DeleteIcon  from '@material-ui/icons/Delete';
import CloseIcon from '@material-ui/icons/Close';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import KeyboardArrowUpIcon from '@material-ui/icons/KeyboardArrowUp';
import KeyboardArrowDownIcon from '@material-ui/icons/KeyboardArrowDown';
import CancelIcon from '@material-ui/icons/Cancel';
const useStyles = makeStyles((theme) => ({
  root: {
    color: theme.palette.text.primary,
  },
}));
export default function TAnzuSS() {
	const classes = useStyles();
	return (
		<div>
			<CloseIcon style={{fontSize:24}}/ >
			<ChevronRightIcon style={{fontSize:24}}/ >
			<KeyboardArrowUpIcon style={{fontSize:24}}/ >
			<KeyboardArrowDownIcon style={{fontSize:24}}/ >
			<CancelIcon style={{fontSize:24}}/ >
		</div>
	);
}
