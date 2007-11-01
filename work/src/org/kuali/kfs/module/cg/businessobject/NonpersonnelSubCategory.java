/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.kra.budget.bo;

// import com.sun.rsasign.s;
import org.kuali.core.bo.KualiCodeBase;

/**
 * This class...
 */
public class NonpersonnelSubCategory extends KualiCodeBase implements Comparable {

    private static final long serialVersionUID = 992811943219411565L;

    public NonpersonnelSubCategory() {
        super();
    }

    public NonpersonnelSubCategory(String nonpersonnelSubCategoryCode) {
        super();
        super.setCode(nonpersonnelSubCategoryCode);
    }

    private boolean nonpersonnelMtdcExcludedIndicator;
    private boolean nonpersonnelModularExcludedIndicator;

    /**
     * @return Returns the excluded.
     */
    public boolean isNonpersonnelMtdcExcludedIndicator() {
        return nonpersonnelMtdcExcludedIndicator;
    }

    /**
     * @param excluded The excluded to set.
     */
    public void setNonpersonnelMtdcExcludedIndicator(boolean excluded) {
        this.nonpersonnelMtdcExcludedIndicator = excluded;
    }

    /**
     * Gets the nonpersonnelModularExcludedIndicator attribute.
     * 
     * @return Returns the nonpersonnelModularExcludedIndicator.
     */
    public boolean isNonpersonnelModularExcludedIndicator() {
        return nonpersonnelModularExcludedIndicator;
    }

    /**
     * Sets the nonpersonnelModularExcludedIndicator attribute value.
     * 
     * @param nonpersonnelModularExcludedIndicator The nonpersonnelModularExcludedIndicator to set.
     */
    public void setNonpersonnelModularExcludedIndicator(boolean nonpersonnelModularExcludedIndicator) {
        this.nonpersonnelModularExcludedIndicator = nonpersonnelModularExcludedIndicator;
    }

    public int compareTo(Object o) {
        return super.getName().compareTo(((NonpersonnelSubCategory) o).getName());
    }
}
