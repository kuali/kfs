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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class returns list of chart key value pairs with the key and label both being the chart code.
 */
public class SimpleChartValuesFinder extends KeyValuesBase {

    protected ParameterService parameterService;
    protected FinancialSystemUserService financialSystemUserService;

    /**
     * Creates a list of {@link Chart}s using their code as their key, and their code as the display value
     *
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {

        String defaultChartCode = "";
        String defaultChartCodeMethod = "";

        defaultChartCode = getParameterService().getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_DOCUMENT.class, KFSParameterKeyConstants.DEFAULT_CHART_CODE);
        defaultChartCodeMethod = getParameterService().getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_DOCUMENT.class, KFSParameterKeyConstants.DEFAULT_CHART_CODE_METHOD);

        List<String> chartCodes = SpringContext.getBean(ChartService.class).getAllActiveChartCodes();
        List<KeyValue> chartKeyLabels = new ArrayList<KeyValue>();

        //If the default set by parameter is not a valid chart code then set it to an empty string
        if (!chartCodes.contains(defaultChartCode)) {
            defaultChartCode = "";
        }

        //populate with the default chart
        if (StringUtils.equals(defaultChartCodeMethod, KFSConstants.COAConstants.DEFAULT_CHART_METHOD)) {
            chartKeyLabels.add(new ConcreteKeyValue(defaultChartCode, defaultChartCode));
            fillChartKeyLabels (chartKeyLabels,  chartCodes, defaultChartCode);
        //populate with chart code of the user's primary department
        } else if (StringUtils.equals(defaultChartCodeMethod, KFSConstants.COAConstants.DEFAULT_PRIMARY_DEPT_METHOD) || StringUtils.equals(defaultChartCodeMethod, KFSConstants.COAConstants.DEFAULT_PRIMARY_DEPT_CHART_METHOD)) {
            Person currentUser = GlobalVariables.getUserSession().getPerson();
            String primaryDepartmentChartCode = getFinancialSystemUserService()
                    .getPrimaryOrganization(currentUser, KFSConstants.CoreModuleNamespaces.KFS).getChartOfAccountsCode();
            String chartUsed = "";

            if (StringUtils.isNotBlank(primaryDepartmentChartCode)) {
                chartUsed = primaryDepartmentChartCode;
            } else {
                //if DEFAULT_PRIMARY_DEPT_CHART_METHOD and no primary department use defaultChartCode, otherwise
                //the default code will be blank
                if (StringUtils.equals(defaultChartCodeMethod, KFSConstants.COAConstants.DEFAULT_PRIMARY_DEPT_CHART_METHOD)) {
                    chartUsed = defaultChartCode;
                }
            }

            chartKeyLabels.add(new ConcreteKeyValue(chartUsed, chartUsed));
            fillChartKeyLabels (chartKeyLabels,  chartCodes, chartUsed);
        // In the case where we didn't find a valid defaultChartCodeMethod set the default to blank
        }  else {
            chartKeyLabels.add(new ConcreteKeyValue("", ""));
            fillChartKeyLabels (chartKeyLabels,  chartCodes, "");
        }

        return chartKeyLabels;
    }

    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }

        return parameterService;
    }

    protected FinancialSystemUserService getFinancialSystemUserService() {
        if (financialSystemUserService == null) {
            financialSystemUserService = SpringContext.getBean(FinancialSystemUserService.class);
        }

        return financialSystemUserService;
    }

    protected void fillChartKeyLabels (List<KeyValue> chartKeyLabels, List<String> chartCodes, String defaultChartCode) {
        for (String chartCode : chartCodes) {
            if (!StringUtils.equals(chartCode, defaultChartCode)) {
                chartKeyLabels.add(new ConcreteKeyValue(chartCode, chartCode));
            }
        }
    }

}
