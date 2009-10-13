/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.pdp.batch.service;

/**
 * Defines methods that must be implemented by a ProcessPdpCancelPaidService implementation.
 */
public interface ProcessPdpCancelPaidService {

    /**
     * Update Payment Requests that were canceled or paid in PDP
     */
    public void processPdpCancelsAndPaids();

    /**
     * Update Payment Requests that were canceled in PDP
     */
    public void processPdpCancels();

    /**
     * Update Payment Requests that were paid in PDP
     */
    public void processPdpPaids();

}
