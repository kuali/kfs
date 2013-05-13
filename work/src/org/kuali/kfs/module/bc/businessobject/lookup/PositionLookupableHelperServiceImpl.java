/*
 * Copyright 2007-2008 The Kuali Foundation
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
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Lookupable helper service implementation for the position lookup..
 */
public class PositionLookupableHelperServiceImpl extends SelectLookupableHelperServiceImpl {
    public ConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List, java.util.List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        Map requestParameters = super.getParameters();
        if (requestParameters.containsKey(BCConstants.SHOW_SALARY_BY_POSITION_ACTION)) {
            String[] requestParm = (String[]) requestParameters.get(BCConstants.SHOW_SALARY_BY_POSITION_ACTION);
            Boolean showSalaryByPosition = (Boolean) (new BooleanFormatter()).convertFromPresentationFormat(requestParm[0]);
            if (!showSalaryByPosition) {
                return getPositionCustomActionUrls(businessObject, pkNames);
            }
        }
        else {
            return getPositionCustomActionUrls(businessObject, pkNames);
        }
        return getSalarySettingByPositionUrls(businessObject);
    }


    /**
     * Checks system parameter to determine if the position table is maintained by an external system or internally. If internally
     * they calls super to display the edit and copy links. If external then returns the refresh button source.
     *
     * @param businessObject business object for result row
     * @return String holding the action column contents
     */
    private List<HtmlData> getPositionCustomActionUrls(BusinessObject businessObject, List pkNames) {
        BudgetConstructionPosition position = (BudgetConstructionPosition) businessObject;

        boolean payrollPositionFeed = BudgetParameterFinder.getPayrollPositionFeedIndicator();
        if (!payrollPositionFeed) {
            return super.getCustomActionUrls(businessObject, pkNames);
        }
        return super.getEmptyActionUrls();
    }

    /***
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getActionUrlHref(org.kuali.rice.krad.bo.BusinessObject, java.lang.String, java.util.List)
     */
    @Override
    protected String getActionUrlHref(BusinessObject businessObject, String methodToCall, List pkNames){
        String href = super.getActionUrlHref(businessObject, methodToCall, pkNames);
        href = StringUtils.replace(href, KFSConstants.MAINTENANCE_ACTION,
                KFSConstants.RICE_PATH_PREFIX + KFSConstants.MAINTENANCE_ACTION);
        return href;
    }

    /**
     * Override to check system parameter for determining if the position data is feed from Payroll or maintained in the KFS.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {
        boolean payrollPositionFeed = BudgetParameterFinder.getPayrollPositionFeedIndicator();
        if (payrollPositionFeed) {
            return false;
        }

        return true;
    }

    private Properties getSalarySettingByPositionParameters(BusinessObject businessObject){
        BudgetConstructionPosition position = (BudgetConstructionPosition) businessObject;

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.POSITION_SALARY_SETTING_METHOD);

        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, position.getUniversityFiscalYear().toString());
        parameters.put(BCPropertyConstants.POSITION_NUMBER, position.getPositionNumber());

        Map requestParameters = super.getParameters();
        boolean linkToNewWindow = true;
        if (requestParameters.containsKey(BCPropertyConstants.ADD_LINE)) {
            String[] requestParm = (String[]) requestParameters.get(BCPropertyConstants.ADD_LINE);
            parameters.put(BCPropertyConstants.ADD_LINE, requestParm[0]);
            Boolean addNewFunding = (Boolean) (new BooleanFormatter()).convertFromPresentationFormat(requestParm[0]);
            if (addNewFunding) {
                linkToNewWindow = false;
            }
        }
        else {
            parameters.put(BCPropertyConstants.ADD_LINE, "false");
        }

        if (requestParameters.containsKey(KRADConstants.DOC_FORM_KEY)) {
            String[] requestParm = (String[]) requestParameters.get(KRADConstants.DOC_FORM_KEY);
            parameters.put(BCConstants.RETURN_FORM_KEY, requestParm[0]);
        }
        else  if (requestParameters.containsKey(KFSConstants.FORM_KEY)) {
            String[] requestParm = (String[]) requestParameters.get(KFSConstants.FORM_KEY);
            parameters.put(BCConstants.RETURN_FORM_KEY, requestParm[0]);
        }

        if (requestParameters.containsKey(KFSConstants.BACK_LOCATION)) {
            String[] requestParm = (String[]) requestParameters.get(KFSConstants.BACK_LOCATION);
            parameters.put(KFSConstants.BACK_LOCATION, requestParm[0]);
        }

        if (requestParameters.containsKey(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)) {
            String[] requestParm = (String[]) requestParameters.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, requestParm[0]);
        }

        if (requestParameters.containsKey(KFSPropertyConstants.ACCOUNT_NUMBER)) {
            String[] requestParm = (String[]) requestParameters.get(KFSPropertyConstants.ACCOUNT_NUMBER);
            parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, requestParm[0]);
        }

        if (requestParameters.containsKey(KFSPropertyConstants.SUB_ACCOUNT_NUMBER)) {
            String[] requestParm = (String[]) requestParameters.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            parameters.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, requestParm[0]);
        }

        if (requestParameters.containsKey(KFSPropertyConstants.FINANCIAL_OBJECT_CODE)) {
            String[] requestParm = (String[]) requestParameters.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
            parameters.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, requestParm[0]);
        }

        if (requestParameters.containsKey(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE)) {
            String[] requestParm = (String[]) requestParameters.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
            parameters.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, requestParm[0]);
        }

        if (requestParameters.containsKey(BCPropertyConstants.BUDGET_BY_ACCOUNT_MODE)) {
            String[] requestParm = (String[]) requestParameters.get(BCPropertyConstants.BUDGET_BY_ACCOUNT_MODE);
            parameters.put(BCPropertyConstants.BUDGET_BY_ACCOUNT_MODE, requestParm[0]);
        }

        if (requestParameters.containsKey(BCPropertyConstants.MAIN_WINDOW)) {
            String[] requestParm = (String[]) requestParameters.get(BCPropertyConstants.MAIN_WINDOW);
            parameters.put(BCPropertyConstants.MAIN_WINDOW, requestParm[0]);
        }

        if (requestParameters.containsKey(BCPropertyConstants.SINGLE_ACCOUNT_MODE)) {
            String[] requestParm = (String[]) requestParameters.get(BCPropertyConstants.SINGLE_ACCOUNT_MODE);
            parameters.put(BCPropertyConstants.SINGLE_ACCOUNT_MODE, requestParm[0]);
        }
        else {
            parameters.put(BCPropertyConstants.SINGLE_ACCOUNT_MODE, "false");
        }
        parameters.put(BCConstants.REFRESH_POSITION_BEFORE_SALARY_SETTING, "false");
        return parameters;
    }

    /**
     * Builds URL to salary setting by Position setting parameters based on the caller request.
     *
     * @param businessObject business object for result row
     * @return String holding the action column contents
     */
    public List<HtmlData> getSalarySettingByPositionUrls(BusinessObject businessObject) {
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();

        Properties parameters = getSalarySettingByPositionParameters(businessObject);
        String href = UrlFactory.parameterizeUrl(BCConstants.POSITION_SALARY_SETTING_ACTION, parameters);
        AnchorHtmlData urlData1 =
            new AnchorHtmlData(href, BCConstants.POSITION_SALARY_SETTING_METHOD, "Posn Salset");

        Map requestParameters = super.getParameters();

        boolean linkToNewWindow = true;
        if (requestParameters.containsKey(BCPropertyConstants.ADD_LINE)) {
            String[] requestParm = (String[]) requestParameters.get(BCPropertyConstants.ADD_LINE);
            Boolean addNewFunding = (Boolean) (new BooleanFormatter()).convertFromPresentationFormat(requestParm[0]);
            if (addNewFunding) {
                linkToNewWindow = false;
            }
        }
        if (linkToNewWindow) {
            urlData1.setTarget(BCConstants.SECOND_WINDOW_TARGET_NAME);
        }

        anchorHtmlDataList.add(urlData1);

        // now add refresh url if feed from payroll is on
        boolean payrollPositionFeed = BudgetParameterFinder.getPayrollPositionFeedIndicator();
        String url2 = "";
        if (payrollPositionFeed) {
            parameters.put(BCConstants.REFRESH_POSITION_BEFORE_SALARY_SETTING, "true");
            href = UrlFactory.parameterizeUrl(BCConstants.POSITION_SALARY_SETTING_ACTION, parameters);
            AnchorHtmlData urlData2 =
                new AnchorHtmlData(href, BCConstants.POSITION_SALARY_SETTING_METHOD, "Posn Salset w/sync");

            if (linkToNewWindow) {
                urlData2.setTarget(KFSConstants.NEW_WINDOW_URL_TARGET);
            }

            anchorHtmlDataList.add(urlData2);
            anchorHtmlDataList.get(anchorHtmlDataList.lastIndexOf(urlData2)).setPrependDisplayText("<br />");
        }

        return anchorHtmlDataList;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     *
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
