/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class TransactionDocumentExceptionReportLine extends TransactionDocumentForReportLineBase {

    protected String kemid;

    public TransactionDocumentExceptionReportLine(String documentType, String documentId) {
        this(documentType, documentType, "", "");        
    }
    
    public TransactionDocumentExceptionReportLine(String documentType, String documentId, String securityId) {
        this(documentType, documentType, securityId, "");        
    }
    
    public TransactionDocumentExceptionReportLine(String documentType, String documentId, String securityId, String kemid) {
        this.documentType = documentType;
        this.documentId   = documentId;
        this.securityId   = securityId;
        this.kemid        = kemid;        
    }
    
    /**
     * Gets the kemid attribute. 
     * @return Returns the kemid.
     */
    public String getKemid() {
        return kemid;
    }
}
