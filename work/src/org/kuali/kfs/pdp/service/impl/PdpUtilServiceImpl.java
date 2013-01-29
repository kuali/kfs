/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.pdp.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.service.PdpUtilService;
import org.kuali.kfs.sys.KFSConstants;

/**
 * Utility service for assisting in PDP processing.
 */
public class PdpUtilServiceImpl implements PdpUtilService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpUtilServiceImpl.class);

    public PdpUtilServiceImpl() {
        super();
    }

    @Override
    public boolean isDebit(PaymentAccountDetail paymentAccountDetail, boolean reversal) {
    	boolean isDebit = true;
    	String docType = paymentAccountDetail.getPaymentDetail().getFinancialDocumentTypeCode();
    	if(StringUtils.equalsIgnoreCase(docType, KFSConstants.FinancialDocumentTypeCodes.VENDOR_CREDIT_MEMO)) {
    		// Debits are negative for CM docs
    		isDebit &= paymentAccountDetail.getAccountNetAmount().bigDecimalValue().signum() <= 0;
        }
    	else {
    		// Debits are positive for PREQ, DV and all PDP load files.
    		isDebit &= paymentAccountDetail.getAccountNetAmount().bigDecimalValue().signum() >= 0;
        }

    	if (reversal) {
    		return !isDebit;
    	}

    	return isDebit;
    }

}
