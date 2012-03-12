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
 * Created on Aug 12, 2004
 */
package org.kuali.kfs.pdp.service;

import org.kuali.rice.kim.api.identity.Person;

/**
 * @author HSTAPLET
 */
/**
 * This class defines services for Batch maintenance.
 */
public interface BatchMaintenanceService {

    /**
     * This method cancels a pending Batch.
     * @param batchId the id of the batch to be canceled
     * @param note  a note stating the reason for the batch cancelation
     * @param user the user that performed the batch cancelation
     * @return true if batch successfully canceled, false otherwise
     */
    public boolean cancelPendingBatch(Integer batchId, String note, Person user);

    /**
     * This method holds a pending Batch.
     * @param batchId the id of the batch to perfomr hold on
     * @param note a nite stating the reason for holding batch
     * @param user the user that performed the batch hold
     * @return true if batch successfully hold, false otherwise
     */
    public boolean holdPendingBatch(Integer batchId, String note, Person user);

    /**
     * This method removes a hold on a Batch.
     * @param batchId the id of the batch we want to remove the hold
     * @param changeText a text stating the reason for removing the hold
     * @param user the user that removed hold on batch
     * @return  true if batch hold successfully removed, false otherwise
     */
    public boolean removeBatchHold(Integer batchId, String changeText, Person user);

    /**
     * This method checks if the batch has open payments.
     * @param batchId the id of the batch
     * @return returns true if batch has open payments, false otherwise
     */
    public boolean doBatchPaymentsHaveOpenStatus(Integer batchId);

    /**
     * This method checks if batch payments has open or held payments.
     * @param batchId the id of the batch
     * @return true if batch has open or held payments, false otherwise
     */
    public boolean doBatchPaymentsHaveOpenOrHeldStatus(Integer batchId);

    /**
     * This method checks if batch payments have held status.
     * @param batchId the id of the batch
     * @return true if batch payments have held status, false otherwise
     */
    public boolean doBatchPaymentsHaveHeldStatus(Integer batchId);
}

