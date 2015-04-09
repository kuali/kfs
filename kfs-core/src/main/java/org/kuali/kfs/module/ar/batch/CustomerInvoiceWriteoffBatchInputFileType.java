/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.batch;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class CustomerInvoiceWriteoffBatchInputFileType extends XmlBatchInputFileTypeBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceWriteoffBatchInputFileType.class);

    private static final String FILE_NAME_PREFIX = "customer_invoice_writeoff";
    private static final String FILE_NAME_DELIM = "_";
    
    private DateTimeService dateTimeService;
    
    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.api.identity.Person, java.lang.Object, java.lang.String)
     */
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifer) {
        
        //  start with the batch-job-prefix
        StringBuilder fileName = new StringBuilder(FILE_NAME_PREFIX);
        
        //  add the logged-in user name if there is one, otherwise use a sensible default
        fileName.append(FILE_NAME_DELIM + principalName);
        
        //  if the user specified an identifying label, then use it
        if (StringUtils.isNotBlank(fileUserIdentifer)) {
            fileName.append(FILE_NAME_DELIM + fileUserIdentifer);
        }
        
        //  stick a time stamp on the end
        fileName.append(FILE_NAME_DELIM + dateTimeService.toString(dateTimeService.getCurrentTimestamp(), "yyyyMMdd_HHmmss"));

        //  stupid spaces, begone!
        return StringUtils.remove(fileName.toString(), " ");
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return ArConstants.CustomerInvoiceWriteoff.CUSTOMER_INVOICE_WRITEOFF_FILE_TYPE_IDENTIFIER;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    public boolean validate(Object parsedFileContents) {
        return true;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileTypeBase#process(java.lang.String, java.lang.Object)
     */
    public void process(String fileName, Object parsedFileContents) {
        super.process(fileName, parsedFileContents);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        // this is never uploaded via the GUI
        return "";
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public String getAuthorPrincipalName(File file) {
        // this should never allow uploads or downloads via the GUI
        return null;
    }
}

