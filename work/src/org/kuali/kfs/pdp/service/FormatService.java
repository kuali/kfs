/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
