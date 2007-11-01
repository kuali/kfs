/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.pdp.service;

import java.util.Date;
import java.util.List;

import org.kuali.module.pdp.bo.PdpUser;

public interface FormatService {
    // Get the customer profiles to list on the screen
    public List getAllCustomerProfiles();

    // Get disbursement numbers to list on the screen
    public List getAllDisbursementNumberRanges();

    // Find out if the format is already running somewhere
    public Date getFormatProcessStartDate(String campus);

    // Mark the process log so a format only happens once per campus. Mark all the
    // payments that will be formatted and return a summary. attachments will be Y, N or null for both.
    public List startFormatProcess(PdpUser user, String campus, List customers, Date paydate, boolean immediate, String paymentTypes);

    // Mark the process as ended.
    public void endFormatProcess(String campus);

    // Called from a struts action class, select data to format
    public FormatSelection formatSelectionAction(PdpUser user, boolean clearFormat);

    // Actually format the data for check printing.
    // Return a list of Process Summaries to be displayed
    public List performFormat(Integer procId);

    // If the start format process was run and the user doesn't want to continue,
    // this needs to be run to set all payments back to open
    public void clearUnfinishedFormat(Integer procId);

    // Get a list of FormatResults for a format
    public List getFormatSummary(Integer procId);

    // Reset Payments after a format error
    public void resetFormatPayments(Integer procId);

    // Gets the most current Processes for Format Summary Viewing
    public List getMostCurrentProcesses();
}
