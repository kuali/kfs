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
package org.kuali.kfs.module.cam.businessobject;

import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.AMOUNT;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_NUMBER;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.PURCHASE_ORDER;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.REQUISITION_NUMBER;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE;

import org.kuali.kfs.sys.businessobject.AccountingLineParserBase;

public class AssetPaymentAccountingLineParser extends AccountingLineParserBase {    
    protected static final String[] AP_FORMAT = 
    { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, AMOUNT,
        PURCHASE_ORDER,
        REQUISITION_NUMBER,
        ORIGINATION_CODE,        
        DOCUMENT_NUMBER,
        DOCUMENT_TYPE_CODE,         
        DOCUMENT_POSTING_DATE
    };
    
    public AssetPaymentAccountingLineParser() {
        super();
    }
    
    /**
     * 
     * @see org.kuali.kfs.sys.businessobject.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(AP_FORMAT);
    }
}
