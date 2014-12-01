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
package org.kuali.kfs.coa.document.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.CodeDescriptionFormatterBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;

public class ObjectCodeDescriptionFormatter extends CodeDescriptionFormatterBase {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;

    public ObjectCodeDescriptionFormatter(Integer universityFiscalYear, String chartOfAccountsCode) {
        this.universityFiscalYear = universityFiscalYear;
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    @Override
    protected String getDescriptionOfBO(PersistableBusinessObject bo) {
        return ((ObjectCode) bo).getFinancialObjectCodeName();
    }

    @Override
    protected Map<String, PersistableBusinessObject> getValuesToBusinessObjectsMap(Set values) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        criteria.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.put(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, values);
        Collection<ObjectCode> coll = SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(ObjectCode.class, criteria, KFSConstants.VERSION_NUMBER, true);

        Map<String, PersistableBusinessObject> results = new HashMap<String, PersistableBusinessObject>();
        // TODO: worry about active flag?
        for (ObjectCode oc : coll) {
            results.put(oc.getFinancialObjectCode(), oc);
        }
        return results;
    }
}
