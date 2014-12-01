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
package org.kuali.kfs.module.purap.businessobject;

import static org.kuali.kfs.sys.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.sys.KFSPropertyConstants.PERCENT;
import static org.kuali.kfs.sys.KFSPropertyConstants.PROJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.SUB_ACCOUNT_NUMBER;

import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.businessobject.AccountingLineParserBase;
import org.kuali.kfs.sys.document.AccountingDocument;

/**
 * This class is used to parse an PurApItem accounting line.
 */
public class PurApAccountingLineParser extends AccountingLineParserBase {
    protected static final String[] PURAP_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, PERCENT };

    /**
     * Constructs a AuxiliaryVoucherAccountingLineParser.java.
     */
    public PurApAccountingLineParser() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(PURAP_FORMAT);
    }
    
    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParser#getSourceAccountingLineClass(org.kuali.rice.krad.document.AccountingDocument)
     */
    @Override
    protected Class getSourceAccountingLineClass(final AccountingDocument accountingDocument) {
        if (accountingDocument instanceof RequisitionDocument)
            return RequisitionAccount.class;
        else if (accountingDocument instanceof PurchaseOrderDocument)
            return PurchaseOrderAccount.class;
        else return super.getSourceAccountingLineClass(accountingDocument);
    }

}
