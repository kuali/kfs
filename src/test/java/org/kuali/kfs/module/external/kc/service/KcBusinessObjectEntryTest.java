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
package org.kuali.kfs.module.external.kc.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;

@ConfigureContext(session = khuntley)
public class KcBusinessObjectEntryTest extends KualiTestBase {
   
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {         
        super.setUp();
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testBOEntry(){
        BusinessObjectEntry boe1 = (BusinessObjectEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry("org.kuali.kfs.module.external.kc.businessobject.AwardAccount");
        BusinessObjectEntry boe2 = (BusinessObjectEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry("org.kuali.kfs.module.cg.businessobject.AwardAccount");
        BusinessObjectEntry boe3 = (BusinessObjectEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry("org.kuali.kfs.integration.cg.businessobject.AwardAccount");
        BusinessObjectEntry boe4 = (BusinessObjectEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry("org.kuali.kfs.module.external.kc.businessobject.Award");
        BusinessObjectEntry boe5 = (BusinessObjectEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry("org.kuali.kfs.module.external.kc.businessobject.UnitDTO");
        
        System.out.print("done");
    }
}


