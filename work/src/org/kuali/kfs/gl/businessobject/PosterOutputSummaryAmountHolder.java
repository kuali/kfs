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
package org.kuali.kfs.gl.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Interface for any class which holds a set of poster output summary amounts
 */
public interface PosterOutputSummaryAmountHolder {
    /**
     * This method adds an amounts to the correct totals for a poster output summary entry
     * 
     * @param debitCreditCode credit code used to determine whether amounts is debit or credit
     * @param objectTypeCode object type code associated with amount
     * @param amount amount to add
     */
    public void addAmount(String debitCreditCode, String objectTypeCode, KualiDecimal amount);
}
