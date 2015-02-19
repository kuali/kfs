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
