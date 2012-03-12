/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.service.impl;

import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.rice.kim.api.identity.Person;

/**
 * for those cases when admins claim a payment without an associated document
 */
public class ElectronicPaymentClaimingDocumentGenerationStrategyImpl implements ElectronicPaymentClaimingDocumentGenerationStrategy {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicPaymentClaimingDocumentGenerationStrategyImpl.class);
    
    private ElectronicPaymentClaimingService electronicPaymentClaimingService;

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#createDocumentFromElectronicPayments(java.util.List, org.kuali.rice.kim.api.identity.Person)
     */
    public String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, Person user) {
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getDocumentCode()
     */
    public String getDocumentCode() {
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getDocumentLabel()
     */
    public String getDocumentLabel() {
        return "No Document";
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getClaimingDocumentWorkflowDocumentType()
     * 
     * return null in the case of no document available
     */
    public String getClaimingDocumentWorkflowDocumentType() {
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#isDocumentReferenceValid(java.lang.String)
     */
    public boolean isDocumentReferenceValid(String referenceDocumentNumber) {
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#userMayUseToClaim(org.kuali.rice.kim.api.identity.Person)
     */
    public boolean userMayUseToClaim(Person claimingUser) {
        return electronicPaymentClaimingService.isAuthorizedForClaimingElectronicPayment(claimingUser, null);
    }

    /**
     * Sets the electronicPaymentClaimingService attribute value.
     * @param electronicPaymentClaimingService The electronicPaymentClaimingService to set.
     */
    public void setElectronicPaymentClaimingService(ElectronicPaymentClaimingService electronicPaymentClaimingService) {
        this.electronicPaymentClaimingService = electronicPaymentClaimingService;
    }
}
