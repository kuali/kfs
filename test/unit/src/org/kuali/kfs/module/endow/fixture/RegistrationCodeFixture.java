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
