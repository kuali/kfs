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
