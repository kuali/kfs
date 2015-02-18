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
package org.kuali.kfs.sys.document;

import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.PaymentSourceWireTransfer;

/**
 * Information needed by PDP to pay out from a given document
 */
public interface PaymentSource extends GeneralLedgerPostingDocument, GeneralLedgerPendingEntrySource {
    /**
     * @return the wire transfer associated with this payment source
     */
    public abstract PaymentSourceWireTransfer getWireTransfer();

    /**
     * @return true if this payment has an attachment with it (which would prevent it from being used as part of a wire transfer)
     */
    public abstract boolean hasAttachment();

    /**
     * @return the method to pay out this payment
     */
    public abstract String getPaymentMethodCode();

    /**
     * @return the code identifier of the campus most associated with this campus
     */
    public abstract String getCampusCode();

    /**
     * @return the bank associated with this document
     */
    public abstract Bank getBank();
}
