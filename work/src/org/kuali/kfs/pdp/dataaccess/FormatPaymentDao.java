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
/*
 * Created on Sep 1, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.pdp.businessobject.PaymentProcess;


/**
 * @author jsissom
 */
public interface FormatPaymentDao {
    /**
     * This method mark payments for format
     * @param proc
     * @param customers
     * @param paydate
     * @param paymentTypes
     */
    public Iterator markPaymentsForFormat(List customers, Timestamp paydate, String paymentTypes);

    /**
     * This method unmark payments that were marked for format.
     * @param proc
     */
    public Iterator unmarkPaymentsForFormat(PaymentProcess proc);
}
