/*
 * Copyright 2006 The Kuali Foundation
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

