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
package org.kuali.kfs.module.endow.fixture;

import org.kuali.kfs.module.endow.businessobject.FeeSecurity;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum FeeSecurityFixture {

    FEE_SECURITY_RECORD_1("TESTFEE1", // feeMethodCode
            "TESTSECID",  // securityCode
            true //include
    ),

    FEE_SECURITY_RECORD_2("TESTFEE1", // feeMethodCode
            "TESTSEC2", // securityCode
            true //include
    );

    public final String feeMethodCode;
    public final String securityCode;
    public final boolean include;
    
    private FeeSecurityFixture(String feeMethodCode, String securityCode, boolean include) {
        this.feeMethodCode = feeMethodCode;
        this.securityCode = securityCode;
        this.include = include;
    }
    
    /**
     * This method creates a Fee Security record and saves it to the DB table.
     * 
     * @return feeSecurity record
     */
    public FeeSecurity createFeeSecurityRecord() {
        FeeSecurity feeSecurity = new FeeSecurity();

        feeSecurity.setFeeMethodCode(this.feeMethodCode);
        feeSecurity.setSecurityCode(this.securityCode);
        feeSecurity.setInclude(this.include);
        
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(feeSecurity);

        return feeSecurity;
    }

}
