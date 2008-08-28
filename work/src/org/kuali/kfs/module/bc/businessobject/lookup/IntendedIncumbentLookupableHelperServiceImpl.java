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

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.format.BooleanFormatter;

/**
 * Lookupable helper service implementation for the intended incumbent lookup..
 */
public class IntendedIncumbentLookupableHelperServiceImpl extends SelectLookupableHelperServiceImpl {
    public KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject businessObject) {
        Map requestParameters = super.getParameters();
        if (requestParameters.containsKey(BCConstants.SHOW_SALARY_BY_INCUMBENT_ACTION)) {
            String[] requestParm = (String[]) requestParameters.get(BCConstants.SHOW_SALARY_BY_INCUMBENT_ACTION);
            Boolean showSalaryByIncumbent = (Boolean) (new BooleanFormatter()).convertFromPresentationFormat(requestParm[0]);
            if (!showSalaryByIncumbent) {
                return getMaintenanceUrls(businessObject);
            }
        }
        else {
            return getMaintenanceUrls(businessObject);
        }

        return getSalarySettingByIncumbentUrls(businessObject);
    }


    /**
     * Checks system parameter to determine if the incumbent table is maintained by an external system or internally. If internally
     * they calls super to display the edit and copy links. If external then returns the refresh button source.
     * 
     * @param businessObject business object for result row
     * @return String holding the action column contents
     */
    public String getMaintenanceUrls(BusinessObject businessObject) {
        BudgetConstructionIntendedIncumbent intendedIncumbent = (BudgetConstructionIntendedIncumbent) businessObject;

        boolean payrollIncumbentFeed = BudgetParameterFinder.getPayrollIncumbentFeedIndictor();
        if (!payrollIncumbentFeed) {
            String url = super.getActionUrls(businessObject);
            url = StringUtils.replace(url, KFSConstants.MAINTENANCE_ACTION, KFSConstants.RICE_PATH_PREFIX + KFSConstants.MAINTENANCE_ACTION);
            return url;
        }

        return "";
    }

    /**
     * Override to check system parameter for determining if the incumbent data is feed from Payroll or maintained in the KFS.
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {
        boolean payrollIncumbentFeed = BudgetParameterFinder.getPayrollIncumbentFeedIndictor();
        if (payrollIncumbentFeed) {
            return false;
        }

        return true;
    }

    /**
     * Builds URL to salary setting by Incumbent setting parameters based on the caller request.
     * 
     * @param businessObject business object for result row
     * @return String holding the action column contents
     */
    public String getSalarySettingByIncumbentUrls(BusinessObject businessObject) {
        BudgetConstructionIntendedIncumbent intendedIncumbent = (BudgetConstructionIntendedIncumbent) businessObject;

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.INCUMBENT_SALARY_SETTING_METHOD);
        parameters.put(KFSPropertyConstants.EMPLID, intendedIncumbent.getEmplid());

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

        String[] universityFiscalYear = (String[]) super.getParameters().get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear[0]);

        if (requestParameters.containsKey(KNSConstants.DOC_FORM_KEY)) {
            String[] requestParm = (String[]) requestParameters.get(KNSConstants.DOC_FORM_KEY);
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

        if (requestParameters.containsKey(KFSPropertyConstants.OBJECT_CODE)) {
            String[] requestParm = (String[]) requestParameters.get(KFSPropertyConstants.OBJECT_CODE);
            parameters.put(KFSPropertyConstants.OBJECT_CODE, requestParm[0]);
        }

        if (requestParameters.containsKey(KFSPropertyConstants.SUB_OBJECT_CODE)) {
            String[] requestParm = (String[]) requestParameters.get(KFSPropertyConstants.SUB_OBJECT_CODE);
            parameters.put(KFSPropertyConstants.SUB_OBJECT_CODE, requestParm[0]);
        }

        if (requestParameters.containsKey(BCPropertyConstants.SINGLE_ACCOUNT_MODE)) {
            String[] requestParm = (String[]) requestParameters.get(BCPropertyConstants.SINGLE_ACCOUNT_MODE);
            parameters.put(BCPropertyConstants.SINGLE_ACCOUNT_MODE, requestParm[0]);
        }
        else {
            parameters.put(BCPropertyConstants.SINGLE_ACCOUNT_MODE, "false");
        }

        parameters.put(BCConstants.REFRESH_INCUMBENT_BEFORE_SALARY_SETTING, "false");

        String url = UrlFactory.parameterizeUrl(BCConstants.INCUMBENT_SALARY_SETTING_ACTION, parameters);
        url = "<a href=\"" + url + "\"";
        if (linkToNewWindow) {
            url += "target=\"blank\" ";
        }
        url += "title=\"Incmbnt Salset\">Incmbnt Salset</a>  ";

        // now add refresh url if feed from payroll is on
        boolean payrollIncumbentFeed = BudgetParameterFinder.getPayrollIncumbentFeedIndictor();
        String url2 = "";
        if (payrollIncumbentFeed) {
            parameters.put(BCConstants.REFRESH_INCUMBENT_BEFORE_SALARY_SETTING, "true");
            url2 = UrlFactory.parameterizeUrl(BCConstants.INCUMBENT_SALARY_SETTING_ACTION, parameters);
            url2 = "<a href=\"" + url2 + "\"";
            if (linkToNewWindow) {
                url2 += "target=\"blank\" ";
            }
            url2 += "title=\"Incmbnt Salset w/sync\">Incmbnt Salset w/sync</a>  ";
        }

        return url + "<br/>" + url2;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}