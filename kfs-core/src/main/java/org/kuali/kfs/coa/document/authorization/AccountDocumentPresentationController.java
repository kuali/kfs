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
package org.kuali.kfs.coa.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.BusinessObject;

public class AccountDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountDocumentPresentationController.class);

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        setLaborBenefitRateCategoryCodeEditable(readOnlyPropertyNames);
        return readOnlyPropertyNames;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyHiddenPropertyNames(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        Set<String> hiddenPropertyNames = super.getConditionallyHiddenPropertyNames(businessObject);
        
        String sourceOfFundsParmValue = getParameterService().getParameterValueAsString(Account.class, KFSParameterKeyConstants.CoaParameterConstants.DISPLAY_SOURCE_OF_FUNDS_IND);

        if (StringUtils.equalsIgnoreCase(sourceOfFundsParmValue, KFSConstants.ParameterValues.YES)) {
            hiddenPropertyNames.remove(KFSPropertyConstants.SOURCE_OF_FUNDS_TYPE_CODE);
        } else {
            hiddenPropertyNames.add(KFSPropertyConstants.SOURCE_OF_FUNDS_TYPE_CODE);
        }
        return hiddenPropertyNames;
    }

    /**
     * 
     * @return
     */
    protected Boolean getFridgeBenefitCalculationEnableIndicator(){
        AccountService service = SpringContext.getBean(AccountService.class);
        return service.isFridgeBenefitCalculationEnable();
    }
    
    /**
     * 
     * Sets the Labor Benefit Rate Category Code, otherwise leave
     * it read/wrtie.
     * 
     * @param readOnlyPropetyNames
     */
    protected void setLaborBenefitRateCategoryCodeEditable(Set<String> readOnlyPropertyNames){
        Boolean isFridgeBenefitCalcEnable = getFridgeBenefitCalculationEnableIndicator();
        
        //default null to false, if FridgeBenefitCalculation is NOT enable - makes code not editable 
        if ( ! isFridgeBenefitCalcEnable ){
            readOnlyPropertyNames.add(KFSPropertyConstants.LABOR_BENEFIT_RATE_CATEGORY_CODE);
        }
    }
    
    /**
     * 
     * Hides the Labor Benefit Rate Category Code depending on the system parameter ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND
     * 
     * @param hiddenPropetyNames
     */
    protected void setLaborBenefitRateCategoryCodeHidden(Set<String> hiddenPropertyNames){
        Boolean isFridgeBenefitCalcEnable = getFridgeBenefitCalculationEnableIndicator();
        
        //default null to false, if FridgeBenefitCalculation is NOT enable - makes code not editable 
        if ( ! isFridgeBenefitCalcEnable ){
            hiddenPropertyNames.add(KFSPropertyConstants.LABOR_BENEFIT_RATE_CATEGORY_CODE);
        }
    }
    
}
