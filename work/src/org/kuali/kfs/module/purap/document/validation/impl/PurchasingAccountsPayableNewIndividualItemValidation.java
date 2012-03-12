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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kns.service.DataDictionaryService;

public class PurchasingAccountsPayableNewIndividualItemValidation extends GenericValidation {

    private DataDictionaryService dataDictionaryService;
    private ParameterService parameterService;
    private PurApItem itemForValidation;
    private PurchasingAccountsPayableBelowTheLineValuesValidation belowTheLineValuesValidation;    
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        if (itemForValidation.getItemType().isAdditionalChargeIndicator()) {
            belowTheLineValuesValidation.setItemForValidation(itemForValidation);
            valid &= belowTheLineValuesValidation.validate(event);                       
        }
        return valid;
    }

    protected String getDocumentTypeLabel(String documentTypeName) {
            return SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(documentTypeName).getLabel();
    }
    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public PurchasingAccountsPayableBelowTheLineValuesValidation getBelowTheLineValuesValidation() {
        return belowTheLineValuesValidation;
    }

    public void setBelowTheLineValuesValidation(PurchasingAccountsPayableBelowTheLineValuesValidation belowTheLineValuesValidation) {
        this.belowTheLineValuesValidation = belowTheLineValuesValidation;
    }    


}
