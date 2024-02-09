/**
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 */

import React, { useEffect } from "react";
import { Stack, Content, Theme } from '@carbon/react';
import { Route, Routes, BrowserRouter } from 'react-router-dom';
import MainHeader from './components/MainHeader';

import Home from "./components/Home"
import Logs from "./components/Logs"
import Db2Cdc from "./components/Db2Cdc"

import "./App.scss";
import "./App.css";

function App() {

	useEffect(() => {
		document.title = 'Kafka Demo'
	}, []);

	return (
		<>
		<BrowserRouter>
			<Stack gap={4} style={{height: '100vh'}}> 
				<MainHeader/>
				<Content style={{marginTop: '10pt', marginBottom: '15pt'}}>
						<Routes>
							<Route path="/" element={<Home />} />
							<Route path="/logs" element={<Logs />} />
							<Route path="/db2cdc" element={<Db2Cdc />} />
						</Routes>
				</Content>
				<Theme theme="g100" style={{position: 'fixed', bottom: 0, height: '30pt', width: '100%', padding: '10pt' }}>
						<p style={{ fontSize: 'x-small' }}>
							@IBM Corp., Apache2.0
						</p>
				</Theme> 
			</Stack>
		</BrowserRouter>

	</>
	);
}

export default App;