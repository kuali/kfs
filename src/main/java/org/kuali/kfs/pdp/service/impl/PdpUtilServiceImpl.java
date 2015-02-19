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
package org.kuali.kfs.pdp.service.impl;

import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.service.PdpUtilService;

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
        // Debits are positive for PREQ, VCM (since amount has been negated in PdpExtractServiceImpl.addAccounts()), DV and all PDP load files.
		isDebit &= paymentAccountDetail.getAccountNetAmount().bigDecimalValue().signum() >= 0;

    	if (reversal) {
    		return !isDebit;
    	}

    	return isDebit;
    }

}
