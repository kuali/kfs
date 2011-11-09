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
