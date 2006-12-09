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
package org.kuali.module.financial.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjLevel;

/**
 * This class...
 * 
 * 
 */
public class ObjectLevelCodeDescriptionFormatter extends CodeDescriptionFormatterBase {

    private String chartOfAccountsCode;

    public ObjectLevelCodeDescriptionFormatter(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @see org.kuali.module.financial.util.CodeDescriptionFormatterBase#getDescriptionOfBO(org.kuali.core.bo.BusinessObject)
     */
    @Override
    protected String getDescriptionOfBO(BusinessObject bo) {
        return ((ObjLevel) bo).getFinancialObjectLevelName();
    }

    /**
     * @see org.kuali.module.financial.util.CodeDescriptionFormatterBase#getValuesToBusinessObjectsMap(java.util.Set)
     */
    @Override
    protected Map<String, BusinessObject> getValuesToBusinessObjectsMap(Set values) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.put(Constants.FINANCIAL_OBJECT_LEVEL_CODE_PROPERTY_NAME, values);
        Collection<ObjLevel> coll = SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(ObjLevel.class, criteria, Constants.VERSION_NUMBER, true);

        Map<String, BusinessObject> results = new HashMap<String, BusinessObject>();
        // TODO: worry about version #s
        for (ObjLevel ol : coll) {
            results.put(ol.getFinancialObjectLevelCode(), ol);
        }
        return results;
    }

}
