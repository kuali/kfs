/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_CODE;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_DESCRIPTION;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_QUANTITY;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_SERVICE_DATE;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_TAXABLE_INDICATOR;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.UNIT_OF_MEASURE_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.sys.KFSPropertyConstants.PROJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.SUB_ACCOUNT_NUMBER;

import org.kuali.kfs.sys.businessobject.AccountingLineParserBase;

/**
 * This class...
 */
public class CustomerInvoiceAccountingLineParser extends AccountingLineParserBase {

    protected static final String[] CUSTOMER_INVOICE_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, INVOICE_ITEM_CODE, INVOICE_ITEM_QUANTITY, INVOICE_ITEM_DESCRIPTION, INVOICE_ITEM_SERVICE_DATE, UNIT_OF_MEASURE_CODE, INVOICE_ITEM_UNIT_PRICE, INVOICE_ITEM_TAXABLE_INDICATOR };
    
    public CustomerInvoiceAccountingLineParser() {
        super();
    }
	
	/**
     * @see org.kuali.rice.krad.bo.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(CUSTOMER_INVOICE_FORMAT);
    }
    
}
