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

    public String getFileName(Person user, Object parsedFileContents, String fileUserIdentifer) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean validate(Object parsedFileContents) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean checkAuthorization(Person user, File batchFile) {
        // TODO Auto-generated method stub
        return false;
    }

    public String getTitleKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public Class getUploadWorkgroupParameterComponent() {
        // TODO Auto-generated method stub
        return null;
    }

   

}

