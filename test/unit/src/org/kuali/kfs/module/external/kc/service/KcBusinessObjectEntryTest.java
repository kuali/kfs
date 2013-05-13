/*
 * Copyright 2010 The Kuali Foundation.
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


