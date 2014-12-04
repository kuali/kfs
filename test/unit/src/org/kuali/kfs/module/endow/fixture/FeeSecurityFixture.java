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
