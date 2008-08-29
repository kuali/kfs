/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.kfs.module.cg.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * This is KRAs NonpersonnelCategory business object.
 */
public class NonpersonnelCategory extends PersistableBusinessObjectBase implements Inactivateable {

    private static final long serialVersionUID = -908290558174256616L;
    private String code;
    private boolean active;
    private String name;
    private Integer sortNumber;

    private List nonpersonnelObjectCodes;

    public NonpersonnelCategory() {
        super();
        nonpersonnelObjectCodes = new ArrayList();
    }


    /**
     * Gets the sortNumber attribute.
     * 
     * @return Returns the sortNumber.
     */
    public Integer getSortNumber() {
        return sortNumber;
    }

    /**
     * Sets the sortNumber attribute value.
     * 
     * @param sortNumber The sortNumber to set.
     */
    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    /**
     * 
     * Constructs a NonpersonnelCategory.java.
     * @param nonpersonnelCategoryCode
     */
    public NonpersonnelCategory(String nonpersonnelCategoryCode) {
        this.setCode(nonpersonnelCategoryCode);
    }

    /**
     * 
     * This method...
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * 
     * This method...
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 
     * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        active = active;
    }

    /**
     * 
     * This method...
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 
     * This method...
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("code", this.code);
        return m;
    }


    /**
     * Gets the nonpersonnelObjectCodes attribute.
     * 
     * @return Returns the nonpersonnelObjectCodes.
     */
    public List<NonpersonnelObjectCode> getNonpersonnelObjectCodes() {
        return nonpersonnelObjectCodes;
    }

    /**
     * Sets the nonpersonnelObjectCodes attribute value.
     * 
     * @param nonpersonnelObjectCodes The nonpersonnelObjectCodes to set.
     */
    public void setNonpersonnelObjectCodes(List nonpersonnelObjectCodes) {
        this.nonpersonnelObjectCodes = nonpersonnelObjectCodes;
    }

    /**
     * Gets the newNonpersonnel attribute.
     * 
     * @return Returns the newNonpersonnel.
     */
    public NonpersonnelObjectCode getNonpersonnelObjectCode(int index) {
        while (getNonpersonnelObjectCodes().size() <= index) {
            getNonpersonnelObjectCodes().add(new NonpersonnelObjectCode());
        }
        return (NonpersonnelObjectCode) getNonpersonnelObjectCodes().get(index);
    }
}
