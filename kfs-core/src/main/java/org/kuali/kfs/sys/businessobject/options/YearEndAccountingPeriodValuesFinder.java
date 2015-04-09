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
package org.kuali.kfs.sys.businessobject.options;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * returns list of accounting period value pairs valid for year end posting
 */
public class YearEndAccountingPeriodValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<ConcreteKeyValue> accountingPeriodCodes = new ArrayList<ConcreteKeyValue>();

        Date date = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        AccountingPeriod currentAccountingPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(date);

        if (currentAccountingPeriod.isOpen()) {
            // add the current period with blank value, so scrubber will set entries when posting
            accountingPeriodCodes.add(new ConcreteKeyValue("", currentAccountingPeriod.getUniversityFiscalPeriodName()));
        }

        String numberOfPostbackPeriodsParmVal = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.NUMBER_OF_POSTBACK_PERIODS);
        if (StringUtils.isNotBlank(numberOfPostbackPeriodsParmVal) && Integer.valueOf(numberOfPostbackPeriodsParmVal) > 0) {
            for (int i = 1; i <= Integer.valueOf(numberOfPostbackPeriodsParmVal); i++) {
                int currentFiscalYear = currentAccountingPeriod.getUniversityFiscalYear();
                int currentFiscalPeriod = Integer.valueOf(currentAccountingPeriod.getUniversityFiscalPeriodCode());

                if (currentFiscalPeriod == 1) {
                    currentFiscalYear = currentFiscalYear - 1;
                    currentFiscalPeriod = 13;
                } else {
                    currentFiscalPeriod = currentFiscalPeriod - 1;
                }

                currentAccountingPeriod = SpringContext.getBean(AccountingPeriodService.class).getByPeriod(StringUtils.leftPad(Integer.toString(currentFiscalPeriod), 2, "0"), currentFiscalYear);
                if (currentAccountingPeriod.isOpen()) {
                    accountingPeriodCodes.add(new ConcreteKeyValue(currentAccountingPeriod.getUniversityFiscalPeriodCode() + currentAccountingPeriod.getUniversityFiscalYear(), currentAccountingPeriod.getUniversityFiscalPeriodName()));
                }
            }
        }

        return accountingPeriodCodes;
    }
}
