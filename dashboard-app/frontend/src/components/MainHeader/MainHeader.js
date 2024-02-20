/**
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 */

import React from 'react';
import {
  Header,
  HeaderContainer,
  HeaderName,
  HeaderNavigation,
  HeaderMenuItem,
  HeaderGlobalBar,
  HeaderGlobalAction,
  SkipToContent,
  Theme
} from '@carbon/react';

import { IbmCloudEventStreams, Settings, UserAvatar } from '@carbon/icons-react';

import { Link } from 'react-router-dom';

const MainHeader = () => (
  <HeaderContainer
    render={() => (
	  <>
	 <Theme theme="g100">	
      <Header aria-label="Kafka Demo">
        <SkipToContent />
        <HeaderName as={Link} to="/" prefix="Kafka">
         Streaming App. Example<IbmCloudEventStreams style={{marginLeft: '10pt'}} />
        </HeaderName>
        <HeaderNavigation aria-label=" Kafka Demo">
          <HeaderMenuItem as={Link} to="/">
          	Main Dashboard
          </HeaderMenuItem>
          <HeaderMenuItem as={Link} to="/db2cdc">
          	DB2 CDC
          </HeaderMenuItem>
          <HeaderMenuItem as={Link} to="/logs">
         	Logs Monitor
          </HeaderMenuItem>
        </HeaderNavigation>
        
        <HeaderGlobalBar>
          <HeaderGlobalAction
            aria-label="User Avatar"
            tooltipAlignment="center">
            <UserAvatar size={20} />
          </HeaderGlobalAction>
          <HeaderGlobalAction aria-label="Settings" tooltipAlignment="end">
            <Settings size={20} />
          </HeaderGlobalAction>
        </HeaderGlobalBar>
      </Header>
      </Theme>
      </>
    )}
  />
);

export default MainHeader;