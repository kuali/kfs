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
package org.kuali.kfs.module.endow.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class EndowmentRecurringCashTransferDocumentTypeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List<KeyValue> getKeyValues() {
        // Create label and initialize with the first entry being blank.
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("", ""));
        
        List<String> documentTypeNames = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(EndowmentRecurringCashTransfer.class, EndowParameterKeyConstants.ENDOWMENT_RECURRING_CASH_TRANSFER_DOCUMENT_TYPES) );
        DocumentTypeService documentTypeService = SpringContext.getBean(DocumentTypeService.class);

        String label= null;
        for (String documentTypeName : documentTypeNames) {
            if(documentTypeService.getDocumentTypeByName(documentTypeName)== null){
                keyValues.add(new ConcreteKeyValue(documentTypeName, documentTypeName+" - Can't find it!"));
            }
            else{
                label = documentTypeService.getDocumentTypeByName(documentTypeName).getLabel();
                keyValues.add(new ConcreteKeyValue(documentTypeName, documentTypeName+" - "+label));
            }            
        }

        return keyValues;
    }

}
