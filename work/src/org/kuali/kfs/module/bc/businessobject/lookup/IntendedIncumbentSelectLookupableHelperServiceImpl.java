/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.businessobject.lookup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbentSelect;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * Lookupable helper service implementation for the intended incumbent select screen.
 */
public class IntendedIncumbentSelectLookupableHelperServiceImpl extends SelectLookupableHelperServiceImpl {

    /**
     * Override to set the fiscal year on the BudgetConstructionIntendedIncumbentSelect objects after they have been retrieved.
     * 
     * @see org.kuali.module.budget.web.lookupable.SelectLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        Integer universityFiscalYear = Integer.valueOf(fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
        fieldValues.remove(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);

        List<BudgetConstructionIntendedIncumbentSelect> resultIntendedIncumbents = new ArrayList<BudgetConstructionIntendedIncumbentSelect>();

        List searchResults = super.getSearchResults(fieldValues);
        for (Iterator iterator = searchResults.iterator(); iterator.hasNext();) {
            BudgetConstructionIntendedIncumbentSelect intendedIncumbentSelect = (BudgetConstructionIntendedIncumbentSelect) iterator.next();
            intendedIncumbentSelect.setUniversityFiscalYear(universityFiscalYear);

            resultIntendedIncumbents.add(intendedIncumbentSelect);
        }

        return new CollectionIncomplete(resultIntendedIncumbents, new Long(0));
    }

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject businessObject) {
        BudgetConstructionIntendedIncumbentSelect intendedIncumbentSelect = (BudgetConstructionIntendedIncumbentSelect) businessObject;

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.INCUMBENT_SALARY_SETTING_METHOD);

        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, intendedIncumbentSelect.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.EMPLID, intendedIncumbentSelect.getEmplid());
        parameters.put(BCConstants.BUDGET_BY_ACCOUNT_MODE, "false");
        parameters.put(KFSConstants.ADD_LINE_METHOD, "false");

        String url = UrlFactory.parameterizeUrl(BCConstants.INCUMBENT_SALARY_SETTING_ACTION, parameters);

        return url = "<a href=\"" + url + "\" target=\"blank\" title=\"Incumbent Salary Setting\">Incumbent Salary Setting</a>";
    }
 
}
