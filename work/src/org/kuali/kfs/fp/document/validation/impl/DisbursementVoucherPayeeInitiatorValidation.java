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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class DisbursementVoucherPayeeInitiatorValidation extends GenericValidation implements DisbursementVoucherConstants{
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPayeeInitiatorValidation.class);
    
    private AccountingDocument accountingDocumentForValidation;
    
    public static final String DV_PAYEE_ID_NUMBER_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");        
        boolean isValid = true;
        
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();
        
        MessageMap errors = GlobalVariables.getMessageMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);

        String uuid = null;
        // If payee is a vendor, then look up SSN and look for SSN in the employee table
        if (payeeDetail.isVendor() && StringUtils.isNotBlank(payeeDetail.getDisbVchrVendorHeaderIdNumber())) {
            VendorDetail dvVendor = retrieveVendorDetail(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger(), payeeDetail.getDisbVchrVendorDetailAssignedIdNumberAsInteger());
            // if the vendor tax type is SSN, then check the tax number
            if (dvVendor != null && TAX_TYPE_SSN.equals(dvVendor.getVendorHeader().getVendorTaxTypeCode())) {
                // check ssn against employee table
                Person user = retrieveEmployeeBySSN(dvVendor.getVendorHeader().getVendorTaxNumber());
                if (user != null) {
                    uuid = user.getPrincipalId();
                }
            }
        }
        // If payee is an employee, then pull payee from employee table
        else if(payeeDetail.isEmployee()) {
            uuid = payeeDetail.getDisbVchrEmployeeIdNumber();
        }

        // If a uuid was found for payee, check it against the initiator uuid
        if (StringUtils.isNotBlank(uuid)) {
            Person initUser = getInitiator(document);
            if (uuid.equals(initUser.getPrincipalId())) {
                errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_PAYEE_INITIATOR);
                isValid = false;
            }
        }
        
        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);   
        
        return isValid;
    }

    /**
     * Retrieves the VendorDetail object from the vendor id number.
     * 
     * @param vendorIdNumber vendor ID number
     * @param vendorDetailIdNumber vendor detail ID number
     * @return <code>VendorDetail</code>
     */
    protected VendorDetail retrieveVendorDetail(Integer vendorIdNumber, Integer vendorDetailIdNumber) {
        return SpringContext.getBean(VendorService.class).getVendorDetail(vendorIdNumber, vendorDetailIdNumber);
    }

    /**
     * Retrieves Person from SSN
     * 
     * @param ssnNumber social security number
     * @return <code>Person</code>
     */
    protected Person retrieveEmployeeBySSN(String ssnNumber) {
        Person person = (Person) SpringContext.getBean(PersonService.class).getPersonByExternalIdentifier(KimConstants.PersonExternalIdentifierTypes.TAX, ssnNumber).get(0);
        if (person == null) {
            LOG.error("User Not Found");
        }
        return person;
    }
    
    /**
     * Returns the initiator of the document as a KualiUser
     * 
     * @param document submitted document
     * @return <code>KualiUser</code>
     */
    protected Person getInitiator(AccountingDocument document) {
        Person initUser = KimApiServiceLocator.getPersonService().getPerson(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        if (initUser == null) {
            throw new RuntimeException("Document Initiator not found");
        }

        return initUser;
    }
    
    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

}

