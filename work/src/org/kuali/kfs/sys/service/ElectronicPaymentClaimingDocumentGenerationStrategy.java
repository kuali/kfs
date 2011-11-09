/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.service;

import java.util.List;

import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.rice.kim.api.identity.Person;

/**
 * A set of methods that help the ElectronicPaymentClaimingService turn a list of ElectronicPaymentClaim records into a document
 * used to claim those records.
 * 
 * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingService
 * @see org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim
 */
public interface ElectronicPaymentClaimingDocumentGenerationStrategy {

    /**
     * Returns the label which will identify the claiming document to users
     * 
     * @return a label
     */
    public abstract String getDocumentLabel();

    /**
     * get the workflow document type code of the claiming document
     * 
     * @return the workflow document type code of the claiming document
     */
    public String getClaimingDocumentWorkflowDocumentType();

    /**
     * Determines if the given user can use the document wrapped by this ElectronicPaymentClaimingDocumentGenerationStrategy
     * implementaton to claim any ElectronicPaymentClaim records
     * 
     * @param claimingUser the user attempting to claim ElectronicPaymentClaim records with a document
     * @return true if the user can use this kind of document to claim ElectronicPaymentClaim records, false otherwise
     */
    public abstract boolean userMayUseToClaim(Person claimingUser);

    /**
     * Creates a document to claim a given list of ElectronicPaymentClaim records.
     * 
     * @param electronicPayments a List of ElectronicPaymentClaim records
     * @param user the user doing the claiming
     * @return the absolute URL that should be redirected to, so that the user can edit the document
     */
    public abstract String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, Person user);

    /**
     * Determines whether the given document number would be considered valid by the system that the document this strategy
     * interacts with
     * 
     * @param referenceDocumentNumber the document number reference to validate
     * @return true if the document reference is considered valid, false otherwise
     */
    public abstract boolean isDocumentReferenceValid(String referenceDocumentNumber);
}
