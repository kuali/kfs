/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
