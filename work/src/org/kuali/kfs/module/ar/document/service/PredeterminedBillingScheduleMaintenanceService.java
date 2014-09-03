/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service;

/**
 * Services to support predetermined billing schedule maintenance
 */
public interface PredeterminedBillingScheduleMaintenanceService {
    /**
     * Has the Bill been copied to an Invoice Bill on an invoice doc?
     *
     * @param proposalNumber proposal number to check
     * @param billId billId to check
     * @return true if the Bill has been copied, false if otherwise
     */
    public boolean hasBillBeenCopiedToInvoice(Long proposalNumber, String billId);
}