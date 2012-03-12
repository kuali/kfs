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
package org.kuali.kfs.sys.document.datadictionary;

import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.krad.document.Document;

/**
 * This class allows the system to define a custom document base class for
 */
public class FinancialSystemMaintenanceDocumentEntry extends MaintenanceDocumentEntry {

    /**
     * @see org.kuali.rice.kns.datadictionary.DocumentEntry#setDocumentClass(java.lang.Class)
     */
    public Class<? extends Document> getStandardDocumentBaseClass() {
        return FinancialSystemMaintenanceDocument.class;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.DocumentEntry#setDocumentClass(java.lang.Class)
     */
    @Override
    public void setDocumentClass(Class<? extends Document> documentClass) {
        if (!FinancialSystemMaintenanceDocument.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("document class '" + documentClass + "' needs to have a superclass of '" + FinancialSystemMaintenanceDocument.class + "'");
        }
        super.setDocumentClass(documentClass);
    }

}
