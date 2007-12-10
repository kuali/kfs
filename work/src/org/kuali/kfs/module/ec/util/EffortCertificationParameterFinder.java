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
package org.kuali.module.effort.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.batch.EffortCertificationExtractStep;

/**
 * This class is a convenient utility that can delegate the calling client to retrieve system parameters of effor certification
 * module.
 */
public class EffortCertificationParameterFinder {
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);

    /**
     * get the federal agency type codes setup in system parameters
     * 
     * @return the federal agency type codes setup in system parameters
     */
    public static List<String> getFederalAgencyTypeCodes() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, EffortConstants.extractProcess.FEDERAL_AGENCY_TYPE_CD);
    }

    /**
     * get the fedeal only balance indicatior
     * 
     * @return the fedeal only balance indicatior
     */
    public static boolean getFederalOnlyBalanceIndicator() {
        return parameterService.getIndicatorParameter(EffortCertificationExtractStep.class, EffortConstants.extractProcess.FEDERAL_ONLY_BALANCE_IND);
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
     * get the fund group denotes C&G indicator setup in system paremters
     * 
     * @return the fund group denotes C&G indicator setup in system paremters
     */
    public static boolean getFundGroupDenotesCGIndicator() {
        return parameterService.getIndicatorParameter(EffortCertificationExtractStep.class, EffortConstants.extractProcess.FUND_GROUP_DENOTES_CG_IND);
    }

    /**
     * get the fund group denotes C&G indicator setup in system paremters
     * 
     * @return the fund group denotes C&G indicator setup in system paremters
     */
    public static List<String> getFundGroupDenotesCGIndicatorAsString() {
        List<String> indicatorList = new ArrayList<String>();
        indicatorList.add(Boolean.toString(getFundGroupDenotesCGIndicator()));
        return indicatorList;
    }

    /**
     * get the C&G denoting values setup in system paremters
     * 
     * @return the C&G denoting values setup in system paremters
     */
    public static List<String> getCGDenotingValues() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, EffortConstants.extractProcess.CG_DENOTING_VALUE);
    }

    /**
     * get the account type codes setup in system parameters
     * 
     * @return the account type codes setup in system parameters
     */
    public static List<String> getAccountTypeCodes() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, EffortConstants.extractProcess.ACCOUNT_TYPE_CD_BALANCE_SELECT);
    }

    /**
     * get the report fiscal year setup in system paremters
     * 
     * @return the report fiscal year setup in system paremters
     */
    public static Integer getReportFiscalYear() {
        return Integer.valueOf(parameterService.getParameterValue(EffortCertificationExtractStep.class, EffortConstants.extractProcess.FISCAL_YEAR));
    }

    /**
     * get the report number setup in system paremters
     * 
     * @return the report number setup in system paremters
     */
    public static String getReportNumber() {
        return parameterService.getParameterValue(EffortCertificationExtractStep.class, EffortConstants.extractProcess.REPORT_NUMBER);
    }
}
