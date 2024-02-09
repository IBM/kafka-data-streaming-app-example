/**
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 */

import React, { useReducer, useEffect } from "react";

const Db2Cdc = () => {
	const [cdcEvents, dispatchCdcEvents] = useReducer(reducerCdcEvents, []);
	
	useEffect(() => {
		//Assume that it is for messages from DB2 
		const eventSource = new EventSource("http://localhost:8080/sse-db2");
		
		eventSource.onmessage = (event) => {
					
			let jsonMsg = event.data;
			let timeInMilliseconds = Date.now();
			
			const message = JSON.parse(jsonMsg);
			let item = { value: message.content, time: message.time, ssetime: timeInMilliseconds }
			
			const actionObj = { type: 'add', item: item };

			dispatchCdcEvents(actionObj);
		}
		return () => {
			eventSource.close();
		};
	}, []);	
	
	
  return (<>
  	<div style={{fontWeight: 'bold', marginBottom: '10pt'}} >
  		<span className="blinking-red-dot"></span> Live DB2 database activities via CDC and Kafka ...
  	</div>
  	<div style={{fontSize: 'small', overflowY: 'scroll',  height: '70vh'}} >
		<CdcEvents data={cdcEvents}/>
	</div>
  </>);
};

function reducerCdcEvents(state, action) {
	let newState;
	switch (action.type) {
		case 'add':
			newState = [...state, action.item];
			//keep only 30 newest log lines in memory
			if (newState.length > 30) {
				newState.shift();
			}
			break;
		default:
			throw new Error();
	}
	return newState;
}

function CdcEvents(props) {
	let items = props.data.map((item, index) => {
		const itemId = index + '_' + item.ssetime;
		//const value = text.replace("", "");
		return { id: itemId, value: item.value, time: item.time };
	}).reverse();
	
	const YAML = require('yaml');

	
	return (
		<div style={{ fontSize: 'small'}}>
			<ul>
				{items.map( (item, index) => { 
					let itemStyle = 'slide1';
					const jsonContent = JSON.parse(JSON.stringify(item.value));
					let dbOperationName;
					
					if(jsonContent.A_ENTTYP.string === 'UP')
						dbOperationName = 'UPDATE';
					else if (jsonContent.A_ENTTYP.string === 'DL')
						dbOperationName = 'DELETE';
					else if (jsonContent.A_ENTTYP.string === 'PT')
						dbOperationName = 'INSERT';	
					else 
					dbOperationName = jsonContent.A_ENTTYP.string

					return (
						<li style={{marginBottom: '10pt'}} className={itemStyle} key={item.id}>
						<span style={{fontWeight: 'bold'}}>[{item.time}]  &nbsp; 
						<span style={{color: '#fc6203'}}>{dbOperationName}</span>
						</span>
						<pre style={{fontFamily: 'Courier'}}>{YAML.stringify(item.value) }</pre>
						</li> );
					}) 
				}
				
			</ul>
		</div>
	);

}

export default Db2Cdc;