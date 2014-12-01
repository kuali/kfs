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
