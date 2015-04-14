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
