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
package org.kuali.kfs.module.purap.batch;

import java.io.File;
import java.sql.Timestamp;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * Batch input type for the electronic invoice job.
 */
public class ElectronicInvoiceInputFileType extends BatchInputFileTypeBase {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceInputFileType.class);

    private DateTimeService dateTimeService;

    /**
     * Returns the identifier of the electronic invoice file type
     * 
     * @return the electronic invoice file type identifier
     */
    public String getFileTypeIdentifer() {
        return PurapConstants.ELECTRONIC_INVOICE_FILE_TYPE_INDENTIFIER;
    }

    /**
     * Returns the class associated with the authorization workgroup for the input type, in this case ElectronicInvoiceStep
     * 
     * @return the ElectronicInvoiceStep class
     */
    public Class getUploadWorkgroupParameterComponent() {
        return ElectronicInvoiceStep.class;
    }

    public String getFileName(Person user, 
                              Object parsedFileContents, 
                              String userIdentifier) {
        
//        ElectronicInvoice electronicInvoiceBatch = (ElectronicInvoice) parsedFileContents;
//        Timestamp currentTimestamp = dateTimeService.getCurrentTimestamp();
//
//        StringBuffer buf = new StringBuffer();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        formatter.setLenient(false);
//        formatter.format(currentTimestamp, buf, new FieldPosition(0));

//        String fileName = "gl_idbilltrans_" + electronicInvoiceBatch.getChartOfAccountsCode() + electronicInvoiceBatch.getOrganizationCode();
//        fileName += "_" + user.getPrincipalName().toLowerCase();
//        if (StringUtils.isNotBlank(userIdentifier)) {
//            fileName += "_" + userIdentifier;
//        }
//        fileName += "_" + buf.toString();
//
//        // remove spaces in filename
//        fileName = StringUtils.remove(fileName, " ");

        return null;
    }

    public String getTitleKey() {
        return null;
    }

    public boolean validate(Object parsedFileContents) {
        return false;
    }

    public boolean checkAuthorization(Person user, File batchFile) {
        return false;
    }


}

