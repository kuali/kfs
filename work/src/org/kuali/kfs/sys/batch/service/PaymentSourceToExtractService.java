/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.document.Document;

/**
 * Contract of services which will feed PaymentSources to the PaymentSourceExtractionService
 */
public interface PaymentSourceToExtractService<D extends Document> {
    /**
     * Retrieves all of the PaymentSource documents which should be extracted in PDP, grouped by campus
     *
     * @param immediatesOnly if true, only payments to be immediately extracted will be retrieved
     * @return a Map, where the key is the campus code and the value is a List of PaymentSource documents to be extracted
     */
    public Map<String, List<D>> retrievePaymentSourcesByCampus(boolean immediatesOnly);

    /**
     * @return the unit of the customer profile associated with this payment source
     */
    public abstract String getPreDisbursementCustomerProfileUnit();

    /**
     * @return the sub-unit of the customer profile associated with this payment source
     */
    public abstract String getPreDisbursementCustomerProfileSubUnit();
}
