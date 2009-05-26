/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kns.service.KNSServiceLocator;

import junit.framework.TestCase;

@ConfigureContext
public class KualiConfigurationServiceTest extends KualiTestBase {

    public void testGetSecureProperties() throws Exception {
        String pw = KNSServiceLocator.getKualiConfigurationService().getPropertyString( "datasource.password" );
        if ( pw != null ) {
            KNSServiceLocator.getKualiConfigurationService().getAllProperties().store( System.out , "" );
            assertNull( "Datasource information should not be available through the kuali configuration service.", pw );
        }
    }
}
