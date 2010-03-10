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
package org.kuali.kfs.module.endow.document.validation.impl;

import org.apache.log4j.Logger;

import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.Security;

import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.ClassCodeType;
import org.kuali.kfs.sys.KFSConstants;


public class PooledFundControlRule extends MaintenanceDocumentRuleBase {

//  protected static Logger LOG = org.apache.log4j.Logger.getLogger(PooledFundControlRule.class);
    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        PooledFundControl newPooledFundControl = (PooledFundControl) document.getNewMaintainableObject().getBusinessObject();
        Security theSecurity = newPooledFundControl.getSecurity();
        
        if (!ObjectUtils.isNotNull(newPooledFundControl.getSecurity())){
//          putFieldError(EndowPropertyConstants.POOL_SECURITY_ID, EndowKeyConstants.PooledFundControlConstants.ERROR_POOL_SECURITY_ID_NOT_EXIST);
            return false;            
        }  
        ClassCodeType classCodeType = newPooledFundControl.getSecurity().getClassCode().getCodeType();
        isValid = checkCustomRequiredFields(classCodeType);        
        return isValid;

            
    }
    
    /**
     * Checks if the value of the class code type defined in the new Security ID through Class Code equals to "P" -- Pooled Investment. 
     * If yes, return true; otherwise, return false.
     * 
     * @param classCodeType 
     * @return true if class code type == "P", false otherwise
     */
    private boolean checkCustomRequiredFields(ClassCodeType classCodeType) {
        boolean isValid = true;
        
        if (!"P".equals(classCodeType.getCode())){
            putFieldError(EndowPropertyConstants.POOL_SECURITY_ID, EndowKeyConstants.PooledFundControlConstants.ERROR_CLASS_CODE_TYPE_IS_NOT_P);
            isValid = false;
        }
        
        return isValid;
    }
    
    
}
