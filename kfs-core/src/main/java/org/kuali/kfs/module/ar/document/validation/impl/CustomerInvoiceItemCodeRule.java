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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizer;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;


public class CustomerInvoiceItemCodeRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceItemCodeRule.class);

    protected CustomerInvoiceItemCode newInvoiceItemCode;

    @Override
    public void setupConvenienceObjects() {
        newInvoiceItemCode = (CustomerInvoiceItemCode) super.getNewBo();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success;
        success = validateItemDefaultPrice(newInvoiceItemCode);
        success &= validateItemDefaultQuantity(newInvoiceItemCode);
        success &= validateExistenceOfOrganizationOption(newInvoiceItemCode);
        success &= isCustomerInvoiceItemCodeObjectValid(newInvoiceItemCode);
        success &= validateBillingOrg(document);

        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // always return true even if there are business rule failures.
        processCustomRouteDocumentBusinessRules(document);
        return true;
    }

    public boolean validateBillingOrg(MaintenanceDocument document) {
        boolean success = true;

        String billingChartCode = newInvoiceItemCode.getChartOfAccountsCode();
        String billingOrganizationCode = newInvoiceItemCode.getOrganizationCode();

        if (ObjectUtils.isNull(billingChartCode) || ObjectUtils.isNull(billingOrganizationCode)) {
            return success;
        }

        // get the documentAuthorizer for this document
        MaintenanceDocumentAuthorizer documentAuthorizer = (MaintenanceDocumentAuthorizer) getDocumentHelperService().getDocumentAuthorizer(document);
        success = documentAuthorizer.isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        if (!success){
            putFieldError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, ArKeyConstants.InvoiceItemCode.ERROR_INVALID_CHART_OF_ACCOUNTS_CODE);
            putFieldError(KFSPropertyConstants.ORGANIZATION_CODE,ArKeyConstants.InvoiceItemCode.ERROR_INVALID_ORGANIZATION_CODE );
            success = false;
        }
        return success;
    }

    public boolean validateItemDefaultPrice(CustomerInvoiceItemCode customerInvoiceItemCode) {

        boolean validEntry = true;
        BigDecimal itemDefaultPrice = null;
        if (customerInvoiceItemCode.getItemDefaultPrice() != null) {
            itemDefaultPrice = customerInvoiceItemCode.getItemDefaultPrice().bigDecimalValue();
        }

        if (ObjectUtils.isNotNull(itemDefaultPrice)) {
            validEntry = itemDefaultPrice.compareTo(BigDecimal.ZERO) == 1;
            if (!validEntry) {
                putFieldError("itemDefaultPrice",ArKeyConstants.InvoiceItemCode.NONPOSITIVE_ITEM_DEFAULT_PRICE, "Item Default Price" );
            }
        }
        return validEntry;
    }

    public boolean validateItemDefaultQuantity(CustomerInvoiceItemCode customerInvoiceItemCode) {

        boolean validEntry = true;
        BigDecimal itemDefaultQuantity = customerInvoiceItemCode.getItemDefaultQuantity();

        if (ObjectUtils.isNotNull(itemDefaultQuantity)) {
            if (itemDefaultQuantity.floatValue() <= 0) {
                putFieldError("itemDefaultQuantity",ArKeyConstants.InvoiceItemCode.NONPOSITIVE_ITEM_DEFAULT_QUANTITY, "Item Default Quantity" );
                validEntry = false;
            }
        }
        return validEntry;
    }

    /**
     * This method returns true of organization option row exists with the same chart of accounts code and organization code
     * as the customer invoice item code
     *
     * @param customerInvoiceItemCode
     * @return
     */
    public boolean validateExistenceOfOrganizationOption(CustomerInvoiceItemCode customerInvoiceItemCode) {

        boolean isValid = true;

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", customerInvoiceItemCode.getChartOfAccountsCode());
        criteria.put("organizationCode", customerInvoiceItemCode.getOrganizationCode());

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        if( businessObjectService.countMatching(OrganizationOptions.class, criteria) == 0) {
            putFieldError("organizationCode",ArKeyConstants.InvoiceItemCode.ORG_OPTIONS_DOES_NOT_EXIST_FOR_CHART_AND_ORG, new String[]{customerInvoiceItemCode.getChartOfAccountsCode(),customerInvoiceItemCode.getOrganizationCode()});
            isValid = false;
        }

        return isValid;
    }

    /**
    *
    * This method checks to see if the customer invoice item object code is not restricted by the two parameters
    *
    * namespace: KFS-AR
    * component: Customer Invoice
    * parameter: OBJECT_CONSOLIDATIONS, OBJECT_LEVELS
    *
    * @return true if it is an income object
    */
      protected boolean isCustomerInvoiceItemCodeObjectValid(CustomerInvoiceItemCode customerInvoiceItemCode) {
       boolean success = true;

           Integer universityFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
           ObjectCode defaultInvoiceItemCodeObject = customerInvoiceItemCode.getDefaultInvoiceFinancialObject();

           if (ObjectUtils.isNotNull(universityFiscalYear) && ObjectUtils.isNotNull(defaultInvoiceItemCodeObject)) {
               ParameterService parameterSerivce = SpringContext.getBean(ParameterService.class);

               Collection<String> restrictedObjectConsolidations = parameterSerivce.getParameterValuesAsString(CustomerInvoiceDocument.class, ArConstants.OBJECT_CONSOLIDATIONS);
               Collection<String> restrictedObjectLevels = parameterSerivce.getParameterValuesAsString(CustomerInvoiceDocument.class, ArConstants.OBJECT_LEVELS);

               //first check consolidation is not in the restricted
               if (restrictedObjectConsolidations.contains(defaultInvoiceItemCodeObject.getFinancialObjectLevel().getFinancialConsolidationObjectCode())){
                   putFieldError("defaultInvoiceFinancialObjectCode", ArKeyConstants.OrganizationAccountingDefaultErrors.DEFAULT_INVOICE_FINANCIAL_OBJECT_CODE_INVALID_RESTRICTED,
                           new String[]{defaultInvoiceItemCodeObject.getCode(), "Object Consolidation", restrictedObjectConsolidations.toString()});
                   success = false;
               }else if (restrictedObjectLevels.contains(defaultInvoiceItemCodeObject.getFinancialObjectLevelCode())){
                   putFieldError("defaultInvoiceFinancialObjectCode", ArKeyConstants.OrganizationAccountingDefaultErrors.DEFAULT_INVOICE_FINANCIAL_OBJECT_CODE_INVALID_RESTRICTED,
                           new String[]{defaultInvoiceItemCodeObject.getCode(), "Object Level", restrictedObjectLevels.toString()});
                   success = false;
               }
           }

       return success;
   }
}
