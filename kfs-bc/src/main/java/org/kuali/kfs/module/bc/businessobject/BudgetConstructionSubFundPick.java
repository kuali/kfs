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

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object that represents selected/unselected sub fund group code for a user.
 */
public class BudgetConstructionSubFundPick extends PersistableBusinessObjectBase {

    private String principalId;
    private String subFundGroupCode;
    private Integer reportFlag;

    private SubFundGroup subFundGroup;

    /**
     * Default constructor.
     */
    public BudgetConstructionSubFundPick() {
        reportFlag = new Integer(0);
    }

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the subFundGroupCode attribute.
     * 
     * @return Returns the subFundGroupCode
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * Sets the subFundGroupCode attribute.
     * 
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }


    /**
     * Gets the reportFlag attribute.
     * 
     * @return Returns the reportFlag
     */
    public Integer getReportFlag() {
        return reportFlag;
    }

    /**
     * Sets the reportFlag attribute.
     * 
     * @param reportFlag The reportFlag to set.
     */
    public void setReportFlag(Integer reportFlag) {
        this.reportFlag = reportFlag;
    }

    /**
     * Gets the subFundGroup attribute.
     * 
     * @return Returns the subFundGroup.
     */
    public SubFundGroup getSubFundGroup() {
        return subFundGroup;
    }

    /**
     * Sets the subFundGroup attribute value.
     * 
     * @param subFundGroup The subFundGroup to set.
     * @deprecated
     */
    public void setSubFundGroup(SubFundGroup subFundGroup) {
        this.subFundGroup = subFundGroup;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("subFundGroupCode", this.subFundGroupCode);
        return m;
    }

}

