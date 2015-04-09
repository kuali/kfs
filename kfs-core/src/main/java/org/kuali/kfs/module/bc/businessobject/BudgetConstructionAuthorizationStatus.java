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
