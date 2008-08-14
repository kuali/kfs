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
package org.kuali.kfs.module.purap.batch.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.batch.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.XMLParseException;

public class ElectronicInvoiceHelperServiceImpl implements ElectronicInvoiceHelperService {
    
    private static Logger LOG = Logger.getLogger(ElectronicInvoiceHelperServiceImpl.class);

    private BatchInputFileType electronicInvoiceInputFileType;
    private BatchInputFileService batchInputFileService;

    public boolean loadElectronicInvoiceFile(File file) {
        
        /**
         * FIXME: If there is any problem reading file content, what should we do.... mailing or moving the file to a
         * prob dir sothat someone can fix it up!!!!!!!!!!
         */
        BufferedInputStream fileStream = null;
        try {
            fileStream = new BufferedInputStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e) {
            LOG.error("Error parsing xml " + e.getMessage());
            throw new RuntimeException("Error parsing xml " + e.getMessage(), e);
        }

        ElectronicInvoice parsedObject = null;
        try {
            byte[] fileByteContent = IOUtils.toByteArray(fileStream);
            parsedObject = (ElectronicInvoice) batchInputFileService.parse(electronicInvoiceInputFileType, fileByteContent);
            LOG.debug("ElectronicInvoice..."+parsedObject);
        }catch (IOException e) {
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        }catch (XMLParseException e) {
            LOG.error("Error parsing xml " + e.getMessage());
            throw new RuntimeException("Error parsing xml " + e.getMessage(), e);
        }

        return true;

    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public BatchInputFileType getElectronicInvoiceInputFileType() {
        return electronicInvoiceInputFileType;
    }

    public void setElectronicInvoiceInputFileType(BatchInputFileType electronicInvoiceInputFileType) {
        this.electronicInvoiceInputFileType = electronicInvoiceInputFileType;
    }
}
