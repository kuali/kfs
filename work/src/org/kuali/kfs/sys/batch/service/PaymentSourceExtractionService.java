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
package org.kuali.kfs.sys.batch.service;

import org.kuali.kfs.sys.document.PaymentSource;

/**
 *
 * This service interface defines the methods that a PaymentSourceExtractionService implementation must provide.
 *
 */
public interface PaymentSourceExtractionService {

    /**
     * Extract all disbursement vouchers that need to be paid from the database and prepares them for payment.
     *
     * @return True if the extraction of payments is successful, false if not.
     */
    public boolean extractPayments();

    /**
     * Pulls all disbursement voucher which pay checks and which are marked as "immediate payment" from the database and builds payment information for them
     */
    public void extractImmediatePayments();

    /**
     * Creates a batch payment for a single disbursement voucher
     * @param disbursementVoucher the voucher to immediately extract
     */
    public abstract void extractSingleImmediatePayment(PaymentSource paymentSource);

}
