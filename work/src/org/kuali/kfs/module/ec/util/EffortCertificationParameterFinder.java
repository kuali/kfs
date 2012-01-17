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
package org.kuali.kfs.module.ec.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ec.EffortConstants.SystemParameters;
import org.kuali.kfs.module.ec.batch.EffortCertificationCreateStep;
import org.kuali.kfs.module.ec.batch.EffortCertificationExtractStep;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * A convenient utility that can delegate the calling client to retrieve system parameters of effort certification module.
 */
public class EffortCertificationParameterFinder {
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);

    /**
     * get the run indicator setup in system paremters
     * 
     * @return the run indicator setup in system paremters
     */
    public static boolean getRunIndicator() {
        return parameterService.getParameterValueAsBoolean(EffortCertificationExtractStep.class, SystemParameters.RUN_IND);
    }

    /**
     * get the federal agency type codes setup in system parameters
     * 
     * @return the federal agency type codes setup in system parameters
     */
    public static Collection<String> getFederalAgencyTypeCodes() {
        return parameterService.getParameterValuesAsString(EffortCertificationExtractStep.class, SystemParameters.FEDERAL_AGENCY_TYPE_CODE);
    }

    /**
     * get the federal only balance indicator
     * 
     * @return the federal only balance indicator
     */
    public static boolean getFederalOnlyBalanceIndicator() {
        return parameterService.getParameterValueAsBoolean(EffortCertificationExtractStep.class, SystemParameters.FEDERAL_ONLY_BALANCE_IND);
    }
    
    /**
     * get the federal only route indicator
     * 
     * @return the federal only balance indicator
     */
    public static boolean getFederalOnlyRouteIndicator() {
        return parameterService.getParameterValueAsBoolean(EffortCertificationDocument.class, SystemParameters.FEDERAL_ONLY_ROUTE_IND);
    }

    /**
     * get the fedeal only balance indicatior
     * 
     * @return the fedeal only balance indicatior
     */
    public static List<String> getFederalOnlyBalanceIndicatorAsString() {
        List<String> indicatorList = new ArrayList<String>();
        indicatorList.add(Boolean.toString(getFederalOnlyBalanceIndicator()));
        return indicatorList;
    }

    /**
     * get the account type codes setup in system parameters
     * 
     * @return the account type codes setup in system parameters
     */
    public static Collection<String> getAccountTypeCodes() {
        return parameterService.getParameterValuesAsString(EffortCertificationExtractStep.class, SystemParameters.ACCOUNT_TYPE_CODE_BALANCE_SELECT);
    }

    /**
     * get the report fiscal year setup in system paremters for extract process
     * 
     * @return the report fiscal year setup in system paremters
     */
    public static Integer getExtractReportFiscalYear() {
        return Integer.valueOf(parameterService.getParameterValueAsString(EffortCertificationExtractStep.class, SystemParameters.RUN_FISCAL_YEAR));
    }

    /**
     * get the report number setup in system paremters for extract process
     * 
     * @return the report number setup in system paremters
     */
    public static String getExtractReportNumber() {
        return parameterService.getParameterValueAsString(EffortCertificationExtractStep.class, SystemParameters.RUN_REPORT_NUMBER);
    }

    /**
     * get the report fiscal year setup in system paremters for create process
     * 
     * @return the report fiscal year setup in system paremters
     */
    public static Integer getCreateReportFiscalYear() {
        return Integer.valueOf(parameterService.getParameterValueAsString(EffortCertificationCreateStep.class, SystemParameters.CREATE_FISCAL_YEAR));
    }

    /**
     * get the report number setup in system paremters for create process
     * 
     * @return the report number setup in system paremters
     */
    public static String getCreateReportNumber() {
        return parameterService.getParameterValueAsString(EffortCertificationCreateStep.class, SystemParameters.CREATE_REPORT_NUMBER);
    }
}
