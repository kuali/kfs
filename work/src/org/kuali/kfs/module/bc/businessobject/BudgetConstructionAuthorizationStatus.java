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
package org.kuali.kfs.module.bc.businessobject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds document action and edit mode Maps for session storage
 */
public class BudgetConstructionAuthorizationStatus implements Serializable {
    private Map<String, String> editingMode;
    private Map<String, String> documentActions;

    /**
     * Default Constructor
     */
    public BudgetConstructionAuthorizationStatus() {
        editingMode = new HashMap<String, String>();
        documentActions = new HashMap<String, String>();
    }

    /**
     * Gets the editingMode attribute.
     * 
     * @return Returns the editingMode.
     */
    public Map<String, String> getEditingMode() {
        return editingMode;
    }

    /**
     * Sets the editingMode attribute value.
     * 
     * @param editingMode The editingMode to set.
     */
    public void setEditingMode(Map<String, String> editingMode) {
        this.editingMode = editingMode;
    }

    /**
     * Gets the documentActions attribute.
     * 
     * @return Returns the documentActions.
     */
    public Map<String, String> getDocumentActions() {
        return documentActions;
    }

    /**
     * Sets the documentActions attribute value.
     * 
     * @param documentActions The documentActions to set.
     */
    public void setDocumentActions(Map<String, String> documentActions) {
        this.documentActions = documentActions;
    }

}
