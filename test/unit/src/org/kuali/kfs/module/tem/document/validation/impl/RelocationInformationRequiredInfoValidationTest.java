/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.junit.Test;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

@ConfigureContext(session = khuntley)
public class RelocationInformationRequiredInfoValidationTest extends KualiTestBase {
    
    private RelocationInformationRequiredInfoValidation validation;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new RelocationInformationRequiredInfoValidation();
    }
    
    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    /**
     * This method tests validate method
     */
    @Test
    public void testValidation() {
        TravelRelocationDocument document = new TravelRelocationDocument();
        document.setFromCountryCode(validation.USA_COUNTRY_CODE);
        
        AttributedDocumentEvent event = new AttributedDocumentEventBase("description", "errorPathPrefix", document);
        
        assertFalse(validation.validate(event));
        
        document.setFromStateCode("AZ");
        
        assertTrue(validation.validate(event));
        
        document.setToCountryCode(validation.USA_COUNTRY_CODE);
        assertFalse(validation.validate(event));
        
        document.setToCountryCode("UK");
        assertTrue(validation.validate(event));
        
        
    }

}
