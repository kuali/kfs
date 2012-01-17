/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class OrganizationAccountingDefaultRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationAccountingDefaultRule.class);

    protected ObjectTypeService objectTypeService;
    protected OrganizationAccountingDefault newOrganizationAccountingDefault;
    protected OrganizationAccountingDefault oldOrganizationAccountingDefault;

    public OrganizationAccountingDefaultRule() {

        // insert object type service
        this.setObjectTypeService(SpringContext.getBean(ObjectTypeService.class));
    }

    @Override
    public void setupConvenienceObjects() {
        newOrganizationAccountingDefault = (OrganizationAccountingDefault) super.getNewBo();
        oldOrganizationAccountingDefault = (OrganizationAccountingDefault) super.getOldBo();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        success &= isWriteOffObjectValidExpense(newOrganizationAccountingDefault);
        success &= isLateChargeObjectValidIncome(newOrganizationAccountingDefault);
        success &= isDefaultInvoiceFinancialObjectValidIncome(newOrganizationAccountingDefault);
        
        // validate receivable FAU line if system parameter for receivable is set to 3
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        if (ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption)) {
            success &= doesPaymentAccountNumberExist(newOrganizationAccountingDefault);
            success &= doesPaymentChartOfAccountsCodeExist(newOrganizationAccountingDefault);
            success &= doesPaymentFinancialObjectCodeExist(newOrganizationAccountingDefault);
        }
        
        // validate writeoff FAU line if system parameter for writeoff is set to 2
        String writeoffOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);
        if (ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals(writeoffOption)) {
            success &= doesWriteoffAccountNumberExist(newOrganizationAccountingDefault);
            success &= doesWriteoffChartOfAccountsCodeExist(newOrganizationAccountingDefault);
            success &= doesWriteoffFinancialObjectCodeExist(newOrganizationAccountingDefault);
        }        

        return success;
    }
    
    

    /**
     * This method returns true if payment account number is provided and is valid.
     * 
     * @param doc
     * @return
     */
    protected boolean doesWriteoffAccountNumberExist(OrganizationAccountingDefault organizationAccountingDefault) {

        if (StringUtils.isEmpty(organizationAccountingDefault.getWriteoffAccountNumber())) {
            putFieldError(ArPropertyConstants.OrganizationAccountingDefaultFields.WRITEOFF_ACCOUNT_NUMBER, ArKeyConstants.OrganizationAccountingDefaultErrors.ERROR_WRITEOFF_ACCOUNT_NUMBER_REQUIRED);
            return false;
        }

        return true;
    }

    /**
     * This method returns true if payment chart of accounts code is provided and is valid
     * 
     * @param doc
     * @return
     */
    protected boolean doesWriteoffChartOfAccountsCodeExist(OrganizationAccountingDefault organizationAccountingDefault) {

        if (StringUtils.isEmpty(organizationAccountingDefault.getWriteoffChartOfAccountsCode())) {
            putFieldError(ArPropertyConstants.OrganizationAccountingDefaultFields.WRITEOFF_CHART_OF_ACCOUNTS_CODE, ArKeyConstants.OrganizationAccountingDefaultErrors.ERROR_WRITEOFF_CHART_OF_ACCOUNTS_CODE_REQUIRED);
            return false;
        }

        return true;
    }

    /**
     * This method returns true if payment financial object code is provided and is valid
     * 
     * @param doc
     * @return
     */
    protected boolean doesWriteoffFinancialObjectCodeExist(OrganizationAccountingDefault organizationAccountingDefault) {
        if (StringUtils.isEmpty(organizationAccountingDefault.getWriteoffFinancialObjectCode())) {
            putFieldError(ArPropertyConstants.OrganizationAccountingDefaultFields.WRITEOFF_FINANCIAL_OBJECT_CODE, ArKeyConstants.OrganizationAccountingDefaultErrors.ERROR_WRITEOFF_OBJECT_CODE_REQUIRED);
            return false;
        }

        return true;
    }    


    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // always return true even if there are business rule failures.
        processCustomRouteDocumentBusinessRules(document);
        return true;

    }

    /**
     * 
     * This method checks to see if the Org specified in this document has an Org Options record for it
     * 
     * @return false if it does not have an OrgOptions record
     */
    protected boolean checkOrgOptionsExists() {
        return true;
    }

    /**
     * 
     * This method checks that the Writeoff Object Code is of type Expense
     * <ul>
     * <li>EX</li>
     * <li>EE</li>
     * <li>ES</li>
     * </ul>
     * 
     * @return true if it is an expense object
     */
    protected boolean isWriteOffObjectValidExpense(OrganizationAccountingDefault organizationAccountingDefault) {

        boolean success = true;
        Integer universityFiscalYear = organizationAccountingDefault.getUniversityFiscalYear();
        ObjectCode writeObject = organizationAccountingDefault.getWriteoffFinancialObject();

        if (ObjectUtils.isNotNull(universityFiscalYear) && ObjectUtils.isNotNull(writeObject)) {

            success = objectTypeService.getBasicExpenseObjectTypes(universityFiscalYear).contains(writeObject.getFinancialObjectTypeCode());

            if (!success) {
                putFieldError(ArPropertyConstants.OrganizationAccountingDefaultFields.WRITEOFF_FINANCIAL_OBJECT_CODE, ArKeyConstants.OrganizationAccountingDefaultErrors.WRITE_OFF_OBJECT_CODE_INVALID, writeObject.getCode());
            }
        }

        return success;
    }

    /**
     * 
     * This method checks that the Late Charge Object Code is of type Income Using the ParameterService to find this valid value?
     * <ul>
     * <li>IN</li>
     * <li>IC</li>
     * <li>CH</li>
     * </ul>
     * 
     * @return true if it is an income object
     */
    protected boolean isLateChargeObjectValidIncome(OrganizationAccountingDefault organizationAccountingDefault) {
        boolean success = true;
        Integer universityFiscalYear = organizationAccountingDefault.getUniversityFiscalYear();
        ObjectCode lateChargeObject = organizationAccountingDefault.getOrganizationLateChargeObject();

        if (ObjectUtils.isNotNull(universityFiscalYear) && ObjectUtils.isNotNull(lateChargeObject)) {
            success = objectTypeService.getBasicIncomeObjectTypes(universityFiscalYear).contains(lateChargeObject.getFinancialObjectTypeCode());

            if (!success) {
                putFieldError(ArPropertyConstants.OrganizationAccountingDefaultFields.LATE_CHARGE_OBJECT_CODE, ArKeyConstants.OrganizationAccountingDefaultErrors.LATE_CHARGE_OBJECT_CODE_INVALID, lateChargeObject.getCode());
            }
        }

        return success;
    }

    /**
     * 
     * This method checks to see if the invoice object code is of type Income
     * <ul>
     * <li>IN</li>
     * <li>IC</li>
     * <li>CH</li>
     * </ul>
     * 
     * @return true if it is an income object
     */
    protected boolean isDefaultInvoiceFinancialObjectValidIncome(OrganizationAccountingDefault organizationAccountingDefault) {
        boolean success = true;

        if (StringUtils.isNotEmpty(organizationAccountingDefault.getDefaultInvoiceFinancialObjectCode()) &&
                StringUtils.isEmpty(organizationAccountingDefault.getDefaultInvoiceChartOfAccountsCode())) {
            
            putFieldError(ArPropertyConstants.OrganizationAccountingDefaultFields.INVOICE_CHART_OF_ACCOUNTS_CODE, ArKeyConstants.OrganizationAccountingDefaultErrors.DEFAULT_CHART_OF_ACCOUNTS_REQUIRED_IF_DEFAULT_OBJECT_CODE_EXISTS );
            success = false;
            
        } else {
            Integer universityFiscalYear = organizationAccountingDefault.getUniversityFiscalYear();


            ObjectCode defaultInvoiceFinancialObject = organizationAccountingDefault.getDefaultInvoiceFinancialObject();

            if (ObjectUtils.isNotNull(universityFiscalYear) && ObjectUtils.isNotNull(defaultInvoiceFinancialObject)) {
                success = objectTypeService.getBasicIncomeObjectTypes(universityFiscalYear).contains(defaultInvoiceFinancialObject.getFinancialObjectTypeCode());

                if (!success) {
                    putFieldError(ArPropertyConstants.OrganizationAccountingDefaultFields.INVOICE_CHART_OF_ACCOUNTS_CODE, ArKeyConstants.OrganizationAccountingDefaultErrors.DEFAULT_INVOICE_FINANCIAL_OBJECT_CODE_INVALID, defaultInvoiceFinancialObject.getCode());
                }
            }
        }

        return success;
    }
    
    /**
     * This method returns true if payment account number is provided and is valid.
     * 
     * @param doc
     * @return
     */
    protected boolean doesPaymentAccountNumberExist(OrganizationAccountingDefault organizationAccountingDefault) {

        if (StringUtils.isEmpty(organizationAccountingDefault.getDefaultPaymentAccountNumber())) {
            putFieldError(ArPropertyConstants.OrganizationAccountingDefaultFields.PAYMENT_ACCOUNT_NUMBER, ArKeyConstants.OrganizationAccountingDefaultErrors.ERROR_PAYMENT_ACCOUNT_NUMBER_REQUIRED);
            return false;
        }

        return true;
    }

    /**
     * This method returns true if payment chart of accounts code is provided and is valid
     * 
     * @param doc
     * @return
     */
    protected boolean doesPaymentChartOfAccountsCodeExist(OrganizationAccountingDefault organizationAccountingDefault) {

        if (StringUtils.isEmpty(organizationAccountingDefault.getDefaultPaymentChartOfAccountsCode())) {
            putFieldError(ArPropertyConstants.OrganizationAccountingDefaultFields.PAYMENT_CHART_OF_ACCOUNTS_CODE, ArKeyConstants.OrganizationAccountingDefaultErrors.ERROR_PAYMENT_CHART_OF_ACCOUNTS_CODE_REQUIRED);
            return false;
        }

        return true;
    }

    /**
     * This method returns true if payment financial object code is provided and is valid
     * 
     * @param doc
     * @return
     */
    protected boolean doesPaymentFinancialObjectCodeExist(OrganizationAccountingDefault organizationAccountingDefault) {
        if (StringUtils.isEmpty(organizationAccountingDefault.getDefaultPaymentFinancialObjectCode())) {
            putFieldError(ArPropertyConstants.OrganizationAccountingDefaultFields.PAYMENT_FINANCIAL_OBJECT_CODE, ArKeyConstants.OrganizationAccountingDefaultErrors.ERROR_PAYMENT_OBJECT_CODE_REQUIRED);
            return false;
        }

        return true;
    }    

    public ObjectTypeService getObjectTypeService() {
        return objectTypeService;
    }

    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }
}
