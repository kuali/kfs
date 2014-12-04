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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.endow.batch.service.RollProcessDateService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

@ConfigureContext(session = khuntley)
public class RollProcessDateServiceImplTest extends KualiTestBase {
   
    protected ParameterService parameterService;
    protected KEMService kemService;

    protected RollProcessDateService rollProcessDateService;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception { 
    
        // Initialize service objects.
        rollProcessDateService = SpringContext.getBean(RollProcessDateService.class);        
        kemService = SpringContext.getBean(KEMService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
                        
        super.setUp();
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        rollProcessDateService = null;        
        kemService = null;
        parameterService = null;
        super.tearDown();
    }
    
    /**
     * checks rollProcessDateService#rollDate
     * But, this batch can be run only when the database is initialized. Thus, the unit-test is meaningless. 
     */
    public void testProcessDate() {
//        rollProcessDateService.rollDate();
    }
}
