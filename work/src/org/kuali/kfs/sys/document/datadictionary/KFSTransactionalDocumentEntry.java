/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.datadictionary;

import java.util.Map;

import org.kuali.core.datadictionary.TransactionalDocumentEntry;
import org.kuali.kfs.validation.Validation;

/**
 * An extension of the Rice TransactionalDocumentEntry that allows for KFS-centric properties, such as
 * Accounting Document validations 
 */
public class KFSTransactionalDocumentEntry extends TransactionalDocumentEntry {
    private Map<Class, String> validationMap;

    /**
     * Gets the validationMap attribute. 
     * @return Returns the validationMap.
     */
    public Map<Class, String> getValidationMap() {
        return validationMap;
    }

    /**
     * Sets the validationMap attribute value.
     * @param validationMap The validationMap to set.
     */
    public void setValidationMap(Map<Class, String> validationMap) {
        this.validationMap = validationMap;
    }
    
}
