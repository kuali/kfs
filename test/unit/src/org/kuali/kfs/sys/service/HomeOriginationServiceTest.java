/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
