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
 * Business logic which helps support the maintenance of milestone schedules
 */
public interface MilestoneScheduleMaintenanceService {

    /**
     * Has the Milestone been copied to an Invoice Milestone on an invoice doc? This method investigates!
     * @param proposalNumber proposal number to check
     * @param milestoneId milestoneId to check
     * @return true if the Milestone has been copied, false if otherwise
     */
    public boolean hasMilestoneBeenCopiedToInvoice(Long proposalNumber, String milestoneId);
}