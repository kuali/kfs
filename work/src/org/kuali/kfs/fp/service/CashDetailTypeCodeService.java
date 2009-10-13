/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.fp.service;

import org.kuali.kfs.fp.businessobject.CashDetailTypeCode;

/**
 * This service interface defines methods that a CashDetailTypeCodeService implementation must provide.
 * 
 */
public interface CashDetailTypeCodeService {
    /**
     * This method retrieves a full instance of the appropriate CashReceiptCheckTypeCode instance.
     * 
     * @return A CashDetailTypeCode object of type CashReceiptCheckTypeCode.
     */
    public CashDetailTypeCode getCashReceiptCheckTypeCode();

    /**
     * This method retrieves a full instance of the appropriate CashReceiptCoinTypeCode instance.
     * 
     * @return A CashDetailTypeCode object of type CashReceiptCoinTypeCode.
     */
    public CashDetailTypeCode getCashReceiptCoinTypeCode();
}
