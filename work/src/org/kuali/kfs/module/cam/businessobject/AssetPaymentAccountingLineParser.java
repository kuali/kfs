/*
 * Copyright 2008 The Kuali Foundation
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
