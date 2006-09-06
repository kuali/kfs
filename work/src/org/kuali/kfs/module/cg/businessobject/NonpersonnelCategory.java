/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.kuali.module.kra.budget.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * This is KRAs NonpersonnelCategory business object.
 * 
 * @author pcberg
 */
public class NonpersonnelCategory extends BusinessObjectBase {

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

    public NonpersonnelCategory(String nonpersonnelCategoryCode) {
        this.setCode(nonpersonnelCategoryCode);
    }

    /**
     * @see org.kuali.core.bo.KualiCodeBase#getCode()
     */
    public String getCode() {
        // TODO Auto-generated method stub
        return code;
    }

    /**
     * @see org.kuali.core.bo.KualiCodeBase#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }

    /**
     * @see org.kuali.core.bo.KualiCodeBase#isActive()
     */
    public boolean isActive() {
        // TODO Auto-generated method stub
        return active;
    }

    /**
     * @see org.kuali.core.bo.KualiCodeBase#setActive(boolean)
     */
    public void setActive(boolean a) {
        // TODO Auto-generated method stub
        active = a;
    }

    /**
     * @see org.kuali.core.bo.KualiCodeBase#setCode(java.lang.String)
     */
    public void setCode(String code) {
        // TODO Auto-generated method stub
        this.code = code;
    }

    /**
     * @see org.kuali.core.bo.KualiCodeBase#setName(java.lang.String)
     */
    public void setName(String name) {
        // TODO Auto-generated method stub
        this.name = name;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
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
