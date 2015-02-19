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
 * Created on Jul 18, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.kuali.kfs.pdp.businessobject.CustomerProfile;


/**
 * Data access methods for payment load process.
 */
public interface PaymentFileLoadDao {

    /**
     * Checks whether a <code>Batch</code> record already exists for the given key.
     * 
     * @param customer payment file customer
     * @param count payment total count
     * @param totalAmount payment total amount
     * @param createDate payment file create datetime
     * @return true if the batch already exists
     */
    public boolean isDuplicateBatch(CustomerProfile customer, Integer count, BigDecimal totalAmount, Timestamp createDate);
}
