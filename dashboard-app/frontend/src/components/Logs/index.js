/**
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 */

import React, { useReducer, useEffect } from "react";

const Logs = () => {
	const [logMessages, dispatchLogMessages] = useReducer(reducerLogMessages, []);
	
	useEffect(() => {
		//Assume that this is used to receive data from the log of the random number emitter service
		const eventSource = new EventSource("http://localhost:8080/sse-logs");
		eventSource.onmessage = (event) => {
					
			let message = event.data;
			let timeInMilliseconds = Date.now();
			let item = { value: message, time: timeInMilliseconds }
			const actionObj = { type: 'add', item: item };

			dispatchLogMessages(actionObj);
		}
		return () => {
			eventSource.close();
		};
	}, []);	
	
	
  return (<>
  	<div style={{fontWeight: 'bold', marginBottom: '10pt'}} >
  		<span className="blinking-red-dot"></span> Live log from the RandomNumbers backend service ...
  	</div>
  	<div style={{fontSize: 'small', fontFamily: 'Courier', overflowY: 'scroll',  height: '70vh'}} >
		<LogMessages data={logMessages}/>
	</div>
  </>);
};

function reducerLogMessages(state, action) {
	let newState;
	switch (action.type) {
		case 'add':
			newState = [...state, action.item];

			//keep only 100 newest log lines in memory
			if (newState.length > 100) {
				newState.shift();
			}

			break;
		default:
			throw new Error();
	}
	return newState;
}

function LogMessages(props) {
	let items = props.data.map((item, index) => {
		const itemId = index + '_' + item.time;
		//console.log('DEBUG: ' + itemId + ', ' + item.message);
		return { id: itemId, value: item.value };
	});
	
	return (
		<div style={{fontFamily: 'Courier', fontSize: 'small'}} >
			<ul>
				{items.map(item => (
					<li key={item.id}>{item.value}</li>
				))}
			</ul>
		</div>
	);
}

export default Logs;