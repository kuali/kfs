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

import java.util.Properties;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPositionSelect;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * Lookupable helper service implementation for the position select screen.
 */
public class PositionSelectLookupableHelperServiceImpl extends SelectLookupableHelperServiceImpl {

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject businessObject) {
        BudgetConstructionPositionSelect positionSelect = (BudgetConstructionPositionSelect) businessObject;

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.POSITION_SALARY_SETTING_METHOD);

        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, positionSelect.getUniversityFiscalYear().toString());
        parameters.put(BCPropertyConstants.POSITION_NUMBER, positionSelect.getPositionNumber());
        parameters.put(BCConstants.BUDGET_BY_ACCOUNT_MODE, "false");
        parameters.put(KFSConstants.ADD_LINE_METHOD, "false");

        String url = UrlFactory.parameterizeUrl(BCConstants.POSITION_SALARY_SETTING_ACTION, parameters);

        return url = "<a href=\"" + url + "\" target=\"blank\" title=\"Position Salary Setting\">Position Salary Setting</a>";
    }
}