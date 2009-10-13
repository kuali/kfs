/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.HomeOrigination;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the Home Origination service.
 */
@ConfigureContext
public class HomeOriginationServiceTest extends KualiTestBase {

    public void testGetHomeOrigination() {
        HomeOrigination homeOrigination = SpringContext.getBean(HomeOriginationService.class).getHomeOrigination();
        assertNotNull("Home Origination object cannot be retrieved", homeOrigination);
        assertEquals("Home Origination Code should be 01", "01", homeOrigination.getFinSystemHomeOriginationCode());
    }
}
