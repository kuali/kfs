/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class VendorCreditMemoAccountPercentBetween0And100Validation extends GenericValidation {

    private DataDictionaryService dataDictionaryService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;        
        PurApAccountingLine account = (PurApAccountingLine)((AddAccountingLineEvent)event).getAccountingLine();
        
        if (validateRequiredField(account, PurapPropertyConstants.ACCOUNT_LINE_PERCENT)) {
            double pct = account.getAccountLinePercent().doubleValue();
            if (pct <= 0 || pct > 100) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ACCOUNT_LINE_PERCENT, PurapKeyConstants.ERROR_CREDIT_MEMO_LINE_PERCENT);
                isValid = false;
            }
        }
        else {
            isValid = false;
        }

        return isValid;
    }

    /**
     * Helper method to perform required field checks add error messages if the validation fails. Adds an error required to
     * GlobalVariables.errorMap using the given fieldName as the error key and retrieving the error label from the data dictionary
     * for the error required message param.
     * 
     * @param businessObject - Business object to check for value
     * @param fieldName - Name of the property in the business object
     */
    protected boolean validateRequiredField(BusinessObject businessObject, String fieldName) {
        boolean valid = true;

        Object fieldValue = ObjectUtils.getPropertyValue(businessObject, fieldName);
        if (fieldValue == null || (fieldValue instanceof String && StringUtils.isBlank(fieldName))) {
            String label = dataDictionaryService.getAttributeErrorLabel(businessObject.getClass(), fieldName);
            GlobalVariables.getMessageMap().putError(fieldName, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        return valid;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
