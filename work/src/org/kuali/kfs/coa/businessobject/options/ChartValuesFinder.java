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
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of chart key value pairs.
 */
public class ChartValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link Chart} using their code as the key and their code "-" description
     *
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        Collection<Chart> chartCodes = SpringContext.getBean(ChartService.class).getAllActiveCharts();

        List<KeyValue> chartKeyLabels = new ArrayList<KeyValue>(chartCodes.size()+1);
        chartKeyLabels.add(new ConcreteKeyValue("", ""));
        for (Chart chart : chartCodes) {
            chartKeyLabels.add(new ConcreteKeyValue(chart.getChartOfAccountsCode(), chart.getCodeAndDescription()));
        }

        return chartKeyLabels;
    }

}
