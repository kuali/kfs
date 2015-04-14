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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.service.impl.FiscalYearFunctionControlServiceImpl;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Business object that represents a selected/unselected object code for a user.
 */
public class BudgetConstructionObjectPick extends PersistableBusinessObjectBase {

    private String financialObjectCode;
    private Integer selectFlag;
    private String principalId;

    /**
     * Default constructor.
     */
    public BudgetConstructionObjectPick() {
        selectFlag = new Integer(0);
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    /**
     * Gets the selectFlag attribute.
     * 
     * @return Returns the selectFlag
     */
    public Integer getSelectFlag() {
        return selectFlag;
    }

    /**
     * We only care about a general description for the object code regardless of chart. Therefore we need to do a query and return
     * first row (if multiple).
     * 
     * @return String - Object code description
     */
    public String getObjectCodeDescription() {
        Map criteria = new HashMap();

        List activeBudgetYears = SpringContext.getBean(FiscalYearFunctionControlServiceImpl.class).getActiveBudgetYear();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, activeBudgetYears.get(0));
        criteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.getFinancialObjectCode());

        Collection results = SpringContext.getBean(BusinessObjectService.class).findMatching(ObjectCode.class, criteria);
        if (results != null && results.size() > 0) {
            ObjectCode objectCode = (ObjectCode) results.iterator().next();
            return objectCode.getFinancialObjectCodeName();
        }

        return "";
    }
    
    /**
     * Dummy setter for UI.
     */
    public void setObjectCodeDescription(String objectCodeDescription) {
    }

    /**
     * Sets the selectFlag attribute.
     * 
     * @param selectFlag The selectFlag to set.
     */
    public void setSelectFlag(Integer selectFlag) {
        this.selectFlag = selectFlag;
    }


    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute value.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("financialObjectCode", this.financialObjectCode);
        return m;
    }
}

