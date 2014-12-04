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
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class EndowmentTransactionDocumentTypeValuesFinder extends KeyValuesBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EndowmentTransactionDocumentTypeValuesFinder.class);
    
    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        
        // Create service objects.
        DocumentTypeService documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        ParameterService parameterService       = SpringContext.getBean(ParameterService.class);
        
        // Create label and initialize with the first entry being blank.
        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));
        
        // Read in parameter values.
        Collection<String> documentTypeNames =
            parameterService.getParameterValuesAsString(TransactionArchive.class, EndowParameterKeyConstants.TRANSACTION_ARCHIVE_DOCUMENT_TYPE_NAMES);
        String label= null;
        for (String documentTypeName : documentTypeNames) {
            if(documentTypeService.getDocumentTypeByName(documentTypeName)== null){
                labels.add(new ConcreteKeyValue(documentTypeName, documentTypeName+" - Can't find it!"));
            }
            else{
                label = documentTypeService.getDocumentTypeByName(documentTypeName).getLabel();
                labels.add(new ConcreteKeyValue(documentTypeName, documentTypeName+" - "+label));
            }
        }
        
        return labels;
    }
}
