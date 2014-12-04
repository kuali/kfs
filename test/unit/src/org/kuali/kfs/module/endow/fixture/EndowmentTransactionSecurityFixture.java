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

