/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.bo.user.PersonTaxId;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.UniversalUserService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;

public class DisbursementVoucherPayeeInitiatorValidation extends GenericValidation implements DisbursementVoucherRuleConstants{
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPayeeInitiatorValidation.class);
    
    private AccountingDocument accountingDocumentForValidation;
    
    public static final String DV_PAYEE_ID_NUMBER_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER;

    public boolean validate(AttributedDocumentEvent event) {
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();
        
        ErrorMap errors = GlobalVariables.getErrorMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);

        String uuid = "";
        // If payee is a vendor, then look up SSN and look for SSN in the employee table
        if (StringUtils.equals(payeeDetail.getDisbursementVoucherPayeeTypeCode(), DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR) && StringUtils.isNotBlank(payeeDetail.getDisbVchrVendorHeaderIdNumber())) {
            VendorDetail dvVendor = retrieveVendorDetail(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger(), payeeDetail.getDisbVchrVendorDetailAssignedIdNumberAsInteger());
            // if the vendor tax type is SSN, then check the tax number
            if (dvVendor != null && TAX_TYPE_SSN.equals(dvVendor.getVendorHeader().getVendorTaxTypeCode())) {
                // check ssn against employee table
                UniversalUser user = retrieveEmployeeBySSN(dvVendor.getVendorHeader().getVendorTaxNumber());
                if (user != null) {
                    uuid = user.getPersonUniversalIdentifier();
                }
            }
        }
        // If payee is an employee, then pull payee from employee table
        else if(StringUtils.equals(payeeDetail.getDisbursementVoucherPayeeTypeCode(), DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE)) {
            uuid = payeeDetail.getDisbVchrEmployeeIdNumber();
        }

        // If a uuid was found for payee, check it against the initiator uuid
        if (StringUtils.isNotBlank(uuid)) {
            UniversalUser initUser = getInitiator(document);
            if (uuid.equals(initUser.getPersonUniversalIdentifier())) {
                errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_PAYEE_INITIATOR);
            }
        }
        
        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
        
        return errors.isEmpty();
    }

    /**
     * Retrieves the VendorDetail object from the vendor id number.
     * 
     * @param vendorIdNumber vendor ID number
     * @param vendorDetailIdNumber vendor detail ID number
     * @return <code>VendorDetail</code>
     */
    private VendorDetail retrieveVendorDetail(Integer vendorIdNumber, Integer vendorDetailIdNumber) {
        return SpringContext.getBean(VendorService.class).getVendorDetail(vendorIdNumber, vendorDetailIdNumber);
    }

    /**
     * Retrieves UniversalUser from SSN
     * 
     * @param ssnNumber social security number
     * @return <code>UniversalUser</code>
     */
    private UniversalUser retrieveEmployeeBySSN(String ssnNumber) {
        PersonTaxId personTaxId = new PersonTaxId(ssnNumber);
        UniversalUser user = null;
        try {
            user = SpringContext.getBean(UniversalUserService.class).getUniversalUser(personTaxId);
        } catch (UserNotFoundException e) {
            LOG.error("User Not Found", e);
        }
        return user;
    }
    
    /**
     * Returns the initiator of the document as a KualiUser
     * 
     * @param document submitted document
     * @return <code>KualiUser</code>
     */
    private UniversalUser getInitiator(AccountingDocument document) {
        UniversalUser initUser = null;
        try {
            initUser = SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId());
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Document Initiator not found " + e.getMessage());
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
