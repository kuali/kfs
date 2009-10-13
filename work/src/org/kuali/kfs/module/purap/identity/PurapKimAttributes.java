/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.identity;

import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

public class PurapKimAttributes extends KfsKimAttributes {

    public static final String SENSITIVE_DATA_CODE = "sensitiveDataCode";
    public static final String ACCOUNTS_PAYABLE_PURCHASING_DOCUMENT_LINK_IDENTIFIER = "accountsPayablePurchasingDocumentLinkIdentifier";
    public static final String DOCUMENT_SENSITIVE = "documentSensitive";

    protected String sensitiveDataCode;
    protected boolean documentSensitive;

    protected SensitiveData sensitiveData;

    public String getSensitiveDataCode() {
        return sensitiveDataCode;
    }

    public void setSensitiveDataCode(String sensitiveDataCode) {
        this.sensitiveDataCode = sensitiveDataCode;
    }

    public boolean isDocumentSensitive() {
        return documentSensitive;
    }

    public void setDocumentSensitive(boolean documentSensitive) {
        this.documentSensitive = documentSensitive;
    }

    public SensitiveData getSensitiveData() {
        return sensitiveData;
    }

    public void setSensitiveData(SensitiveData sensitiveData) {
        this.sensitiveData = sensitiveData;
    }
    
    
}
