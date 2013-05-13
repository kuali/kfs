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

import org.kuali.kfs.module.endow.businessobject.FeeClassCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum FeeClassCodeFixture {

    FEE_CLASS_CODE_RECORD_1("TESTFEE1", // feeMethodCode
            "AAA",  // feeClassCode
            true //include
    ),

    FEE_CLASS_CODE_RECORD_2("TESTFEE1", // feeMethodCode
            "ABC", // feeClassCode
            true //include
    );

    public final String feeMethodCode;
    public final String feeClassCode;
    public final boolean include;
    
    private FeeClassCodeFixture(String feeMethodCode, String feeClassCode, boolean include) {
        this.feeMethodCode = feeMethodCode;
        this.feeClassCode = feeClassCode;
        this.include = include;
    }
    
    /**
     * This method creates a Fee Transaction record and saves it to the DB table.
     * 
     * @return feeTransaction record
     */
    public FeeClassCode createFeeClassCodeRecord() {
        FeeClassCode feeClassCode = new FeeClassCode();

        feeClassCode.setFeeMethodCode(this.feeMethodCode);
        feeClassCode.setFeeClassCode(this.feeClassCode);
        feeClassCode.setInclude(this.include);
        
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(feeClassCode);

        return feeClassCode;
    }

}
