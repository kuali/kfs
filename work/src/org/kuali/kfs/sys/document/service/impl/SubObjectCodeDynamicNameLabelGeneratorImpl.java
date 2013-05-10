/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.DynamicNameLabelGenerator;
import org.kuali.rice.krad.util.ObjectUtils;

public class SubObjectCodeDynamicNameLabelGeneratorImpl implements DynamicNameLabelGenerator {

    /**
     * If the subObjectCode reference on the line is refreshable, returns the sub object code's name; otherwise returns null 
     * @see org.kuali.kfs.sys.document.service.DynamicNameLabelGenerator#getDynamicNameLabelFieldName(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    public String getDynamicNameLabelValue(AccountingLine line, String accountingLineProperty) {
        if (line.getPostingYear() != null && !StringUtils.isBlank(line.getChartOfAccountsCode()) && !StringUtils.isBlank(line.getFinancialSubObjectCode())) {
            line.refreshReferenceObject("subObjectCode");
            if (!ObjectUtils.isNull(line.getSubObjectCode())) return line.getSubObjectCode().getFinancialSubObjectCodeName();
        }
        return null;
    }

    /**
     * Builds the proper call to loadSubObjectInfo
     * @see org.kuali.kfs.sys.document.service.DynamicNameLabelGenerator#getDynamicNameLabelOnBlur(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    public String getDynamicNameLabelOnBlur(AccountingLine line, String accountingLineProperty) {
        StringBuilder onBlur = new StringBuilder();
        onBlur.append("loadSubObjectInfo( '");
        onBlur.append(line.getPostingYear());
        onBlur.append("', ");
        onBlur.append("this.name, '");
        onBlur.append(getDynamicNameLabelFieldName(line, accountingLineProperty));
        onBlur.append("' );");
        return onBlur.toString();
    }

    /**
     * Always returns accountingLineProperty+".subObjectCode.financialSubObjectCodeName"
     * @see org.kuali.kfs.sys.document.service.DynamicNameLabelGenerator#getDynamicNameLabelName(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    public String getDynamicNameLabelFieldName(AccountingLine line, String accountingLineProperty) {
        return accountingLineProperty+".subObjectCode.financialSubObjectCodeName";
    }

}
