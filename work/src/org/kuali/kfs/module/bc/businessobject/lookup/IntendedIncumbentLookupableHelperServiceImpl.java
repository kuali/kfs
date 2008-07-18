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

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.format.BooleanFormatter;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCParameterKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Lookupable helper service implementation for the intended incumbent lookup..
 */
public class IntendedIncumbentLookupableHelperServiceImpl extends SelectLookupableHelperServiceImpl {
    public ParameterService parameterService;
    public KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.core.bo.BusinessObject)
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

        boolean payrollIncumbentFeed = parameterService.getIndicatorParameter(BudgetConstructionIntendedIncumbent.class, BCParameterKeyConstants.EXTERNAL_INCUMBENT_FEED_IND);
        if (payrollIncumbentFeed) {
            String imageDirectory = kualiConfigurationService.getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);

            String buttonSubmit = "<input name=\"" + KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + BCConstants.TEMP_LIST_REFRESH_INCUMBENT_METHOD + ".";
            buttonSubmit += KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL + intendedIncumbent.getEmplid() + KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL;
            buttonSubmit += "src=\"" + imageDirectory + BCConstants.REFRESH_INCUMBENT_BUTTON_NAME + "\"  type=\"image\" styleClass=\"tinybutton\" alt=\"refresh incumbent\" title=\"refresh incumbent\" border=\"0\" />";

            return buttonSubmit;
        }

        return super.getActionUrls(businessObject);
    }

    /**
     * Override to check system parameter for determining if the incumbent data is feed from Payroll or maintained in the KFS.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {
        boolean payrollIncumbentFeed = parameterService.getIndicatorParameter(BudgetConstructionIntendedIncumbent.class, BCParameterKeyConstants.EXTERNAL_INCUMBENT_FEED_IND);
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
        if (requestParameters.containsKey(BCConstants.ADD_NEW_FUNDING_LINE)) {
            String[] requestParm = (String[]) requestParameters.get(BCConstants.ADD_NEW_FUNDING_LINE);
            parameters.put(BCConstants.ADD_NEW_FUNDING_LINE, requestParm[0]);
            Boolean addNewFunding = (Boolean) (new BooleanFormatter()).convertFromPresentationFormat(requestParm[0]);
            if (addNewFunding) {
                linkToNewWindow = false;
            }
        }
        else {
            parameters.put(BCConstants.ADD_NEW_FUNDING_LINE, "false");
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

        if (requestParameters.containsKey(BCConstants.SINGLE_ACCOUNT_MODE)) {
            String[] requestParm = (String[]) requestParameters.get(BCConstants.SINGLE_ACCOUNT_MODE);
            parameters.put(BCConstants.SINGLE_ACCOUNT_MODE, requestParm[0]);
        }
        else {
            parameters.put(BCConstants.SINGLE_ACCOUNT_MODE, "false");
        }
        
        parameters.put(BCConstants.REFRESH_INCUMBENT_BEFORE_SALARY_SETTING, "false");

        String url = UrlFactory.parameterizeUrl(BCConstants.INCUMBENT_SALARY_SETTING_ACTION, parameters);
        url = "<a href=\"" + url + "\"";
        if (linkToNewWindow) {
            url += "target=\"blank\" ";
        }
        url += "title=\"Incmbnt Salset\">Incmbnt Salset</a>  ";

        // now add refresh url
        parameters.put(BCConstants.REFRESH_INCUMBENT_BEFORE_SALARY_SETTING, "true");
        String url2 = UrlFactory.parameterizeUrl(BCConstants.INCUMBENT_SALARY_SETTING_ACTION, parameters);
        url2 = "<a href=\"" + url2 + "\"";
        if (linkToNewWindow) {
            url2 += "target=\"blank\" ";
        }
        url2 += "title=\"Incmbnt Salset w/sync\">Incmbnt Salset w/sync</a>  ";

        return url + "<br/>" + url2;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
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