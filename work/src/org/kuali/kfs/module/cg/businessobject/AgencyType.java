/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.module.cg.bo;

import org.kuali.core.bo.KualiCodeBase;

/**
 * Extends KualiCodeBase with no changes.
 */
public class AgencyType extends KualiCodeBase {

    private boolean rowActiveIndicator;

    /**
     * Default no-arg constructor.
     */
    public AgencyType() {
        super(); // KualiCodeBase does some setup in constructor
    }

    /**
     * Returns a boolean defining the active status of the AgencyType object.
     * 
     * @return Getter for the active field.
     */
    public boolean isRowActiveIndicator() {
        return rowActiveIndicator;
    }


    /**
     * Sets the active indicator flag for this AgencyType object.
     * 
     * @param name - Setter for the active field.
     */
    public void setRowActiveIndicator(boolean a) {
        this.rowActiveIndicator = a;
    }

}
