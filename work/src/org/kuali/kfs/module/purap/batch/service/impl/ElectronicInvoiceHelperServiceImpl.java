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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.module.purap.batch.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceContact;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoicePostalAddress;
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
            LOG.debug("Version#:"+parsedObject.getVersion());
            LOG.debug("PayloadID:"+parsedObject.getPayloadID());
            LOG.debug("From Domain:"+parsedObject.getCxmlHeader().getFromDomain());
            LOG.debug("From Identity:"+parsedObject.getCxmlHeader().getFromIdentity());
            //REQUEST
            /**
             * Deployment mode moved to ElectronicInvoice class
             */
//            LOG.debug("DeploymentMode:"+parsedObject.getInvoiceDetailRequestHeader().getDeploymentMode());
//            LOG.debug("DeploymentMode:"+parsedObject.getDeploymentMode());
            LOG.debug("InvoiceDate:"+parsedObject.getInvoiceDetailRequestHeader().getInvoiceDateString());
            LOG.debug("Operation:"+parsedObject.getInvoiceDetailRequestHeader().getOperation());
            LOG.debug("Purpose:"+parsedObject.getInvoiceDetailRequestHeader().getPurpose());
            LOG.debug("isShippingInLine:"+parsedObject.getInvoiceDetailRequestHeader().isShippingInLine());
            
            ElectronicInvoiceContact[] contacts = parsedObject.getInvoiceDetailRequestHeader().getInvoicePartnerContactsAsArray();
            for (int i = 0; i < contacts.length; i++) {
                LOG.debug("Partner Contact...."+contacts[i]);
            }
            
            contacts = parsedObject.getInvoiceDetailRequestHeader().getInvoiceShippingContactsAsArray();
            for (int i = 0; i < contacts.length; i++) {
                LOG.debug("Shipping Contact...."+contacts[i]);
            }
//            
            ElectronicInvoiceOrder[] orders = parsedObject.getInvoiceDetailOrdersAsArray();
            for (int i = 0; i < orders.length; i++) {
                LOG.debug("Order...."+orders[i]);
                ElectronicInvoiceItem[] item = orders[i].getInvoiceItemsAsArray();
                for (int j = 0; i < item.length; i++) {
                    LOG.debug("item...."+item[i]);
                }
            }
            
            
        }
        catch (IOException e) {
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        }
        catch (XMLParseException e) {
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
