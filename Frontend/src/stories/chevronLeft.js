import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
const useStyles = makeStyles((theme) => ({
  root: {
    color: theme.palette.text.primary,
  },
}));
export default function NavigationChevronLeft() {
	const classes = useStyles();
	return (
		<div>
			<ChevronRightIcon style={{fontSize:24}}/ >
		</div>
	);
}
