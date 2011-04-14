/*
 * Copyright 2007 The Kuali Foundation
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

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurityBase;

public enum EndowmentTransactionSecurityFixture {
    // Endowment Transaction Document Fixture
    ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD("ABCD-TEST", //documentNumber
            "F", //securityLineTypeCode - From section
            "TESTSECID", // securityID
            "1234567890", // objectId
            new Long(1) // versionNumber
    ), 
    
    ENDOWMENT_TRANSACTIONAL_SOURCE_SECURITY("ABCD-TEST", //documentNumber
            "F", //securityLineTypeCode - From section
            "TESTSECID", // securityID
            "1234567890", // objectId
            new Long(1) // versionNumber 
    ), 
    
    ENDOWMENT_TRANSACTIONAL_TARGET_SECURITY("ABCD-TEST", //documentNumber
            "F", //securityLineTypeCode - From section
            "TESTSEC2", // securityID
            "1234567890", // objectId
            new Long(1) // versionNumber
    
    ),
    ENDOWMENT_TRANSACTIONAL_TARGET_FOR_POSTING_EDOC_EAI_NON_CASH("EDOC-TEST", //documentNumber
            "F", //securityLineTypeCode - From section --> need to check
            "165167107", // securityID
            "1234567890", // objectId
            new Long(1) // versionNumber
    ),
    ENDOWMENT_TRANSACTIONAL_TARGET_FOR_POSTING_EDOC_EAD_NON_CASH("EDOC-TEST", //documentNumber
            "F", //securityLineTypeCode - From section --> need to check
            "99PLTF021", // securityID
            "1234567890", // objectId
            new Long(1) // versionNumber
    )
    ;
    
    public final String documentNumber;
    public final String securityLineTypeCode; 
    public final String  securityID;
    public final String objectId;
    public final Long versionNumber;   

    private EndowmentTransactionSecurityFixture(String documentNumber, 
                                                String securityLineTypeCode, 
                                                String securityID, 
                                                String objectId, Long versionNumber) {
        this.documentNumber = documentNumber;
        this.securityLineTypeCode = securityLineTypeCode;
        this.securityID = securityID;
        this.objectId = objectId;
        this.versionNumber = versionNumber;
    }

    /**
     * This method creates a Endowment Transaction Document Base record
     * @return endowmentTransactionDocument
     */
    public EndowmentTransactionSecurityBase createEndowmentTransactionSecurity(boolean isSource) {
        EndowmentTransactionSecurityBase endowmentTransactionSecurity = null;
        
        if (isSource) {
            endowmentTransactionSecurity = (EndowmentTransactionSecurityBase) new EndowmentSourceTransactionSecurity();
        }
        else {
            endowmentTransactionSecurity = (EndowmentTransactionSecurityBase) new EndowmentTargetTransactionSecurity();
        }
        
        endowmentTransactionSecurity.setDocumentNumber(this.documentNumber);
        endowmentTransactionSecurity.setSecurityLineTypeCode(this.securityLineTypeCode);
        endowmentTransactionSecurity.setSecurityID(this.securityID);
        endowmentTransactionSecurity.setObjectId(this.objectId);
        endowmentTransactionSecurity.setVersionNumber(this.versionNumber);
        endowmentTransactionSecurity.refreshNonUpdateableReferences();
        
        return endowmentTransactionSecurity;
    }
    
    /**
     * This method creates a Endowment Transaction Document Base record
     * @return endowmentTransactionDocument
     */
    public EndowmentTransactionSecurityBase createEndowmentTransactionSecurity(String documentNumber, 
                                                                               String securityLineTypeCode, 
                                                                               String  securityID, 
                                                                               String objectId, Long versionNumber) {
        EndowmentTransactionSecurityBase endowmentTransactionSecurity = null;
        
        endowmentTransactionSecurity.setDocumentNumber(this.documentNumber);
        endowmentTransactionSecurity.setSecurityLineTypeCode(this.securityLineTypeCode);
        endowmentTransactionSecurity.setSecurityID(this.securityID);
        endowmentTransactionSecurity.setObjectId(this.objectId);
        endowmentTransactionSecurity.setVersionNumber(this.versionNumber);
        endowmentTransactionSecurity.refreshNonUpdateableReferences();
        
        return endowmentTransactionSecurity;
    }
}

