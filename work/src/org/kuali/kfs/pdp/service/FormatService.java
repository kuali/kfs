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
package org.kuali.kfs.pdp.service;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.businessobject.FormatProcessSummary;
import org.kuali.kfs.pdp.businessobject.FormatSelection;
import org.kuali.kfs.pdp.service.impl.exception.FormatException;
import org.kuali.rice.kim.api.identity.Person;

public interface FormatService {

    /**
     * This method gets all customer profiles
     * @return
     */
    public List<CustomerProfile> getAllCustomerProfiles();

    /**
     * This method gets all disbursement number ranges
     * @return
     */
    public List<DisbursementNumberRange> getAllDisbursementNumberRanges();

    /**
     * This method gets the format process by campus code and returns the start date for that process.
     * @param campus the campus code
     * @return the format process start date if any process found for the given campus code, null otherwise
     */
    public Date getFormatProcessStartDate(String campus);

    /**
     * This method gets the data for the format process
     * @param user the user that initiated the format process
     * @return FormatSelection
     */
    public FormatSelection getDataForFormat(Person user);

    /**
     * This method formats the data for check printing.
     * @param procId
     */
    public void performFormat(Integer procId) throws FormatException;

    /**
     *  If the start format process was run and errored out,
     *  this needs to be run to allow formats to continue to function
     * @param procId
     */
    public void resetFormatPayments(Integer procId);

    /**
     * This method marks the process log so a format only happens once per campus. Mark all the
     * payments that will be formatted and return a summary. attachments will be Y, N or null for both.
     * 
     * @param user
     * @param campus
     * @param customers
     * @param paydate
     * @param paymentTypes
     * @return FormatProcessSummary
     */
    public FormatProcessSummary startFormatProcess(Person user, String campus, List<CustomerProfile> customers, Date paydate, String paymentTypes);

    /**
     * This method removes the format process from the format process table
     * @param campus
     */
    public void endFormatProcess(String campus);
    
    /**
     * If the start format process was run and the user doesn't want to continue,
     * this needs to be run to set all payments back to open.
     * This method unmarks the payments and removes the format process entry.
     * @param processId
     */
    public void clearUnfinishedFormat(Integer processId) ;
}

