/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPositionSelect;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Lookupable helper service implementation for the position select screen.
 */
public class PositionSelectLookupableHelperServiceImpl extends SelectLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List, java.util.List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        BudgetConstructionPositionSelect positionSelect = (BudgetConstructionPositionSelect) businessObject;

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.POSITION_SALARY_SETTING_METHOD);

        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, positionSelect.getUniversityFiscalYear().toString());
        parameters.put(BCPropertyConstants.POSITION_NUMBER, positionSelect.getPositionNumber());
        parameters.put(BCPropertyConstants.SINGLE_ACCOUNT_MODE, "false");
        parameters.put(BCPropertyConstants.ADD_LINE, "false");

        String href = UrlFactory.parameterizeUrl(BCConstants.POSITION_SALARY_SETTING_ACTION, parameters);
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, BCConstants.POSITION_SALARY_SETTING_METHOD, "Posn Salset");
        anchorHtmlData.setTarget(BCConstants.SECOND_WINDOW_TARGET_NAME);
        anchorHtmlDataList.add(anchorHtmlData);

        return anchorHtmlDataList;
    }
}
