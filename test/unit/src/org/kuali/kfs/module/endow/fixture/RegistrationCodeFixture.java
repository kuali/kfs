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

import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum RegistrationCodeFixture {

    REGISTRATION_CODE_RECORD(EndowTestConstants.TEST_REGISTRATION_CD, "Test RegistrationCode", true),

    REGISTRATION_CODE_RECORD_FOR_LIABILITY("TEST", // code
            "Test RegistrationCode", // description
            true // active
    ),

    REGISTRATION_CODE_RECORD1_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT("TST1", "TST Registration Code1", true),

    REGISTRATION_CODE_RECORD1_FOR_PROCESS_FEE_TRANSACTIONS("TST1", "TST Registration Code1", true),

    REGISTRATION_CODE_RECORD2_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT("TST2", "TST Registration Code1", true),

    REGISTRATION_CODE_RECORD2("TST2", // code
            "Test RegistrationCode2", // description
            true // active
    ),
    
    REGISTRATION_CODE_RECORD_COMMITTED(EndowTestConstants.TEST_REGISTRATION_CD_COMMITTED, // code
            "Test RegistrationCode2", // description
            true // active    
    ),
    ASSET_REGISTRATION_CODE_RECORD_1("RC1", "Test RegistrationCode 1", true),
    ASSET_REGISTRATION_CODE_RECORD_2("RC2", "Test RegistrationCode 2", true);

    public String code;
    public String description;
    public boolean active;

    private RegistrationCodeFixture(String code, String description, boolean active) {
        this.code = code;
        this.description = description;
        this.active = active;
    }

    /**
     * This method creates a RegistrationCode record and saves it to the DB table.
     * 
     * @return a RegistrationCode
     */
    public RegistrationCode createRegistrationCode() {
        RegistrationCode registrationCodeRecord = new RegistrationCode();
        registrationCodeRecord.setActive(this.active);
        registrationCodeRecord.setCode(this.code);
        registrationCodeRecord.setName(this.description);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(registrationCodeRecord);

        return registrationCodeRecord;
    }
}
