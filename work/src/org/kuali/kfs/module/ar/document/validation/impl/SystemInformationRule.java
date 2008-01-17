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
import org.kuali.module.ar.bo.SystemInformation;
import org.kuali.module.chart.service.ObjectTypeService;

public class SystemInformationRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(SystemInformationRule.class);
    
    private ObjectTypeService objectTypeService;
    private SystemInformation newSystemInformation;

    public SystemInformationRule() {
        // insert object type service
        this.setObjectTypeService(SpringContext.getBean(ObjectTypeService.class));
    }

    @Override
    public void setupConvenienceObjects() {
        newSystemInformation = (SystemInformation) super.getNewBo();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success;
        success = checkSalesTaxObjectValidCode(newSystemInformation);
        success &= checkRefundObjectValidCode(newSystemInformation);

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
     * This method checks that the Sales Tax Objcet Code is of type Income
     * Using the ParameterService to find this valid value?
     * <ul>
     * <li>IC</li>
     * <li>IH</li>
     * <li>CN</li>
     * </ul>
     * @return true if it is an income object
     */
    protected boolean checkSalesTaxObjectValidCode(SystemInformation document) {
        boolean success = true;
        Integer universityFiscalYear = document.getUniversityFiscalYear();
        String salesTaxFinancialObjectCode = document.getSalesTaxFinancialObjectCode();
        
        if (ObjectUtils.isNotNull(universityFiscalYear) && StringUtils.isNotEmpty(salesTaxFinancialObjectCode)) {
            success = objectTypeService.getBasicIncomeObjectTypes(universityFiscalYear).contains(salesTaxFinancialObjectCode);

            if (!success) {
                putFieldError("salesTaxFinancialObjectCode",KFSKeyConstants.SystemInformation.SALES_TAX_OBJECT_CODE_INVALID,salesTaxFinancialObjectCode);
            }
        }
        return success;
    }

    /**
     * 
     * This method checks that the Refund Object Code is of type Expense
     * <ul>
     * <li>EE</li>
     * <li>ES</li>
     * <li>EX</li>
     * </ul>
     * @return true if it is an expense object
     */
    protected boolean checkRefundObjectValidCode(SystemInformation document) {
        
        boolean success = true;
        Integer universityFiscalYear = document.getUniversityFiscalYear();
        String refundFinancialObjectCode = document.getRefundFinancialObjectCode();
        
        if (ObjectUtils.isNotNull(universityFiscalYear) && StringUtils.isNotEmpty(refundFinancialObjectCode)) {
            success = objectTypeService.getBasicExpenseObjectTypes(universityFiscalYear).contains(refundFinancialObjectCode);

            if (!success) {
                putFieldError("refundFinancialObjectCode",KFSKeyConstants.SystemInformation.REFUND_OBJECT_CODE_INVALID,refundFinancialObjectCode);
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
