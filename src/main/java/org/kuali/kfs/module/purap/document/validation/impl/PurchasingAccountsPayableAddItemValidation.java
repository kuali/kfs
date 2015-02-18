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

import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DictionaryValidationService;

public class PurchasingAccountsPayableAddItemValidation extends GenericValidation {

    private PurApItem itemForValidation;
    private DictionaryValidationService dictionaryValidationService;
    
    public boolean validate(AttributedDocumentEvent event) {
        return getDictionaryValidationService().isBusinessObjectValid(itemForValidation, PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);    
    }

    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
