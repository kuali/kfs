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
package org.kuali.kfs.module.bc.document.web.struts;

import org.kuali.core.web.struts.form.KualiForm;

/**
 * Holds properties necessary for expansion screen handling.
 */
public class BudgetExpansionForm extends KualiForm {
    private String backLocation;
    private String returnAnchor;
    private String returnFormKey;

    /**
     * Default Constructor
     */
    public BudgetExpansionForm() {
        super();
    }

    /**
     * Gets the backLocation attribute.
     * 
     * @return Returns the backLocation.
     */
    public String getBackLocation() {
        return backLocation;
    }

    /**
     * Sets the backLocation attribute value.
     * 
     * @param backLocation The backLocation to set.
     */
    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }

    /**
     * Gets the returnAnchor attribute.
     * 
     * @return Returns the returnAnchor.
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor attribute value.
     * 
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the returnFormKey attribute.
     * 
     * @return Returns the returnFormKey.
     */
    public String getReturnFormKey() {
        return returnFormKey;
    }

    /**
     * Sets the returnFormKey attribute value.
     * 
     * @param returnFormKey The returnFormKey to set.
     */
    public void setReturnFormKey(String returnFormKey) {
        this.returnFormKey = returnFormKey;
    }
}
