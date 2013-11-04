/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowPropertyConstants;

/**
 * Business Object for Fee Transaction table.
 */
public class FeeTransaction extends FeeMethodCodeBase {
    private static final Logger LOG = Logger.getLogger(FeeTransaction.class);
    
    private String documentTypeName;
    
    /**
     * Default constructor.
     */   
    public FeeTransaction() {
       super();
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(EndowPropertyConstants.FEE_METHOD_CODE, super.getFeeMethodCode());
        m.put(EndowPropertyConstants.FEE_TRANSACTION_DOCUMENT_TYPE_CODE, this.getDocumentTypeName());        
        return m;
        
    }
       
    /**
     * This method gets documentTypeName
     * 
     * @return documentTypeName
     */
    public String getDocumentTypeName() {
        return documentTypeName;
    }

    /**
     * This method sets documentTypeName.
     * 
     * @param documentTypeName
     */
    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }
}
