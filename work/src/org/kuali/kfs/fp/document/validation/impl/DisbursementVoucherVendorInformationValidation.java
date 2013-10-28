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

import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.KimConstants.PersonExternalIdentifierTypes;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class DisbursementVoucherVendorInformationValidation extends GenericValidation implements DisbursementVoucherConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPaymentReasonValidation.class);

    private ParameterService parameterService;
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

        if (!payeeDetail.isVendor()) {
            
            String initiator = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
            final Entity entity= SpringContext.getBean(IdentityManagementService.class).getEntityByPrincipalId(initiator);
            // KFSCNTRB-1718- Don't assume that an initiator is an employee.
            List<EntityEmployment> employmentInformation = entity.getEmploymentInformation();
            if (employmentInformation != null && employmentInformation.size() > 0) {
                String originatorId = employmentInformation.get(0).getEmployeeId();
                String employeeId = payeeDetail.getDisbVchrEmployeeIdNumber();
                // verify that originator does not equal payee
                if (originatorId.equals(employeeId)) {
                    isValid = false;
                    MessageMap errors = GlobalVariables.getMessageMap();
                    errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
                    String[] errorName = { "Payee ID " + employeeId, " Originator has the same ID ", "name" };
                    errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_VENDOR_NAME_PERSON_NAME_CONFUSION, errorName);
                }
            }
            return isValid;
        }

        if (StringUtils.isBlank(payeeDetail.getDisbVchrPayeeIdNumber())) {
            return false;
        }

        VendorDetail vendor = retrieveVendorDetail(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger(), payeeDetail.getDisbVchrVendorDetailAssignedIdNumberAsInteger());

        MessageMap errors = GlobalVariables.getMessageMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);

        /* Retrieve Vendor */
        if (vendor == null) {
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_EXISTENCE, SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER));
            errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
            return false;
        }

        /* DV Vendor Detail must be active */
        if (!vendor.isActiveIndicator()) {
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_INACTIVE, SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER));
            errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
            return false;
        }

        /* for vendors with tax type ssn, check employee restrictions */
        if (TAX_TYPE_SSN.equals(vendor.getVendorHeader().getVendorTaxTypeCode())) {
            if (isActiveEmployeeSSN(vendor.getVendorHeader().getVendorTaxNumber())) {
                // determine if the rule is flagged off in the param setting
                boolean performPrepaidEmployeeInd = parameterService.getParameterValueAsBoolean(DisbursementVoucherDocument.class, PERFORM_PREPAID_EMPL_PARM_NM);

                if (performPrepaidEmployeeInd) {
                    /* active vendor employees cannot be paid for prepaid travel */
                    ParameterEvaluator travelPrepaidPaymentReasonCodeEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, PREPAID_TRAVEL_PAYMENT_REASONS_PARM_NM, payeeDetail.getDisbVchrPaymentReasonCode());
                    if (travelPrepaidPaymentReasonCodeEvaluator.evaluationSucceeds()) {
                        errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_ACTIVE_EMPLOYEE_PREPAID_TRAVEL);
                        isValid = false;
                    }

                }
            }
           
            else if (isEmployeeSSN(vendor.getVendorHeader().getVendorTaxNumber())) {
                // check param setting for paid outside payroll check
                boolean performPaidOutsidePayrollInd = parameterService.getParameterValueAsBoolean(DisbursementVoucherDocument.class, DisbursementVoucherConstants.CHECK_EMPLOYEE_PAID_OUTSIDE_PAYROLL_PARM_NM);

                if (performPaidOutsidePayrollInd) {
                    /* If vendor is type employee, vendor record must be flagged as paid outside of payroll */
                    if (!SpringContext.getBean(VendorService.class).isVendorInstitutionEmployee(vendor.getVendorHeaderGeneratedIdentifier())) {
                        errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_EMPLOYEE_PAID_OUTSIDE_PAYROLL);
                        isValid = false;
                    }
                }
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
        Person person = (Person) SpringContext.getBean(PersonService.class).getPersonByExternalIdentifier(PersonExternalIdentifierTypes.TAX, ssnNumber).get(0);
        if (person == null) {
            LOG.error("User Not Found");
        }
        return person;
    }

    /**
     * Confirms that the SSN provided is associated with an employee.
     * 
     * @param ssnNumber social security number
     * @return true if the ssn number is a valid employee ssn
     */
    protected boolean isEmployeeSSN(String ssnNumber) {
        return retrieveEmployeeBySSN(ssnNumber) != null;
    }

    /**
     * Performs a lookup on universal users for the given ssn number.
     * 
     * @param ssnNumber social security number
     * @return true if the ssn number is a valid employee ssn and the employee is active
     */
    protected boolean isActiveEmployeeSSN(String ssnNumber) {
        Person employee = retrieveEmployeeBySSN(ssnNumber);
        return employee != null && KFSConstants.EMPLOYEE_ACTIVE_STATUS.equals(employee.getEmployeeStatusCode());
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }
}

