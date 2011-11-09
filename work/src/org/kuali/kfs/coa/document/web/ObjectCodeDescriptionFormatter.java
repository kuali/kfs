/*
 * Copyright 2006-2008 The Kuali Foundation
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
