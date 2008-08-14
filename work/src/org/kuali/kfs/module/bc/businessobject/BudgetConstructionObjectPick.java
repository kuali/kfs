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
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * Business object that represents a selected/unselected object code for a user.
 */
public class BudgetConstructionObjectPick extends PersistableBusinessObjectBase {

    private String financialObjectCode;
    private Integer selectFlag;
    private String personUniversalIdentifier;

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
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier.
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute value.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        m.put("financialObjectCode", this.financialObjectCode);
        return m;
    }
}
