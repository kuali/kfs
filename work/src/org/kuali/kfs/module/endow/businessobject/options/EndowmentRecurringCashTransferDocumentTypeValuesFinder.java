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
