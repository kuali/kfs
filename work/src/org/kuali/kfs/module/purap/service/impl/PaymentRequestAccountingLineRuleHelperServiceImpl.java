/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.service.impl;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class PaymentRequestAccountingLineRuleHelperServiceImpl extends AccountsPayableAccountingLineRuleHelperServiceImpl {

/*
    public boolean isValidObjectCode(ObjectCode objectCode, DataDictionary dataDictionary, String errorPropertyName) {
        
        String label = getObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectCode)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        
        Integer universityFiscalYear = getDocument().getPostingYearNextOrCurrent();
        ObjectCode objectCodeForValidation = (SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(universityFiscalYear, objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode()));
//      setSubObjectCode(SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(universityFiscalYear, this.getChartOfAccountsCode(), this.getAccountNumber(), this.getFinancialObjectCode(), this.getFinancialSubObjectCode()));
            


        // check active status
        if (!objectCodeForValidation.isFinancialObjectActiveCode()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }

        return true;
    }
*/    
  
/*    
    public boolean isValidSubObjectCode(SubObjectCode subObjectCode, DataDictionary dataDictionary, String errorPropertyName) {


        String label = getSubObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subObjectCode)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        
        Integer universityFiscalYear = getDocument().getPostingYearNextOrCurrent();
        SubObjectCode subObjectCodeForValidation = (SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(universityFiscalYear, subObjectCode.getChartOfAccountsCode(), subObjectCode.getAccountNumber(), subObjectCode.getFinancialObjectCode(), subObjectCode.getFinancialSubObjectCode()));


        // check active flag
        if (!subObjectCodeForValidation.isActive()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }
        return true;
    }
*/
    
}
