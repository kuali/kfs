/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.ar.rules;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.chart.service.ObjectTypeService;

public class OrganizationAccountingDefaultRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationAccountingDefaultRule.class);

    private ObjectTypeService objectTypeService;
    private OrganizationAccountingDefault newOrganizationAccountingDefault;
    private OrganizationAccountingDefault oldOrganizationAccountingDefault;

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

        return success;
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
        String writeOffObjectCode = organizationAccountingDefault.getWriteoffObjectCode();

        if (ObjectUtils.isNotNull(universityFiscalYear) && StringUtils.isNotEmpty(writeOffObjectCode)) {
            success = objectTypeService.getBasicExpenseObjectTypes(universityFiscalYear).contains(writeOffObjectCode);

            if (!success) {
                putFieldError("writeoffObjectCode", KFSKeyConstants.OrganizationAccountingDefault.WRITE_OFF_OBJECT_CODE_INVALID, writeOffObjectCode);
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
        String lateChargeObjectCode = organizationAccountingDefault.getOrganizationLateChargeObjectCode();

        if (ObjectUtils.isNotNull(universityFiscalYear) && StringUtils.isNotEmpty(lateChargeObjectCode)) {
            success = objectTypeService.getBasicIncomeObjectTypes(universityFiscalYear).contains(lateChargeObjectCode);

            if (!success) {
                putFieldError("organizationLateChargeObjectCode", KFSKeyConstants.OrganizationAccountingDefault.LATE_CHARGE_OBJECT_CODE_INVALID, lateChargeObjectCode);
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
        Integer universityFiscalYear = organizationAccountingDefault.getUniversityFiscalYear();
        String defaultInvoiceFinancialObjectCode = organizationAccountingDefault.getDefaultInvoiceFinancialObjectCode();

        if (ObjectUtils.isNotNull(universityFiscalYear) && StringUtils.isNotEmpty(defaultInvoiceFinancialObjectCode)) {
            success = objectTypeService.getBasicIncomeObjectTypes(universityFiscalYear).contains(defaultInvoiceFinancialObjectCode);

            if (!success) {
                putFieldError("defaultInvoiceFinancialObjectCode", KFSKeyConstants.OrganizationAccountingDefault.DEFAULT_INVOICE_FINANCIAL_OBJECT_CODE_INVALID, defaultInvoiceFinancialObjectCode);
            }
        }

        return success;
    }

    public ObjectTypeService getObjectTypeService() {
        return objectTypeService;
    }

    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }
}
