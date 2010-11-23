/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.fixture;

import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;

public enum RegistrationCodeFixture {

    REGISTRATION_CODE_RECORD("TEST", "Test RegistrationCode", true);

    public String code;
    public String description;
    public boolean active;

    private RegistrationCodeFixture(String code, String description, boolean active) {
        this.code = code;
        this.description = description;
        this.active = active;
    }

    /**
     * This method...
     * 
     * @return
     */
    public RegistrationCode createRegistrationCode() {
        RegistrationCode registrationCodeRecord = new RegistrationCode();
        registrationCodeRecord.setActive(this.active);
        registrationCodeRecord.setCode(this.code);
        registrationCodeRecord.setName(this.description);

        return registrationCodeRecord;
    }
}
