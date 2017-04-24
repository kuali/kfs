/*
 * Copyright 2010 The Kuali Foundation.
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
package edu.arizona.kfs.pdp.businessobject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.config.property.ConfigurationService;

import edu.arizona.kfs.pdp.batch.service.impl.ShippingInvoiceLoadServiceImpl;
import edu.arizona.kfs.sys.KFSConstants;

/**
 * Adding history BOs for shipping feed data
 */
public class ShippingHeaderHistory extends ShippingHeader {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ShippingInvoiceLoadServiceImpl.class);	
    private static final long serialVersionUID = 2691800933204756597L;
    protected Timestamp loadDate;
    protected String fileName;
    protected ConfigurationService configurationService;
    
    public ShippingHeaderHistory() {
        super();
    }
    
    public ShippingHeaderHistory( Timestamp loadDate, String fileName, ShippingHeader headerToCopy ) {
        this.loadDate = loadDate;
        this.fileName = fileName;
        copyFields( headerToCopy );
    }
    
    protected LinkedHashMap<String, String> toStringMapper() {
    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
    	map.put("loadDate", loadDate.toString());
    	map.put("fileName", fileName);
    	map.putAll( super.toStringMapper() );
    	return map;
    }

    public Timestamp getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(Timestamp loadDate) {
        this.loadDate = loadDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileNameWithoutStagingDirectoryPrefix() {       
    	String stagingDirKey = configurationService.getPropertyValueAsString(KFSConstants.STAGING_DIRECTORY_KEY);
    	String fileNameWithoutStagingDirectoryPrefix = stagingDirKey;
    	if ( (stagingDirKey == null) || (stagingDirKey.isEmpty())) {
            LOG.warn("KFSConstants.STAGING_DIRECTORY_KEY is Empty!" + fileName);    		
    	}
    	else {
    		fileNameWithoutStagingDirectoryPrefix = fileName.replace(stagingDirKey, "" );
    	}
    	if (fileNameWithoutStagingDirectoryPrefix.isEmpty()) {
            LOG.warn("Input File Name is Empty!");    		
    	}    		
        return fileNameWithoutStagingDirectoryPrefix;
    }
    
    /**
     * Copies the entire shipping information data structure into this object and
     * new internal objects.  It copies all the child information and adds the 
     * loadDate and fileName properties.  This is used to copy the base objects
     * (ShippingHeader, ShippingInvoice, and ShippingInvoiceTracking) into their
     * "History" counterparts.
     * 
     * @param headerToCopy
     */
    void copyFields( ShippingHeader headerToCopy ) {
        openCustomField = headerToCopy.openCustomField;
        shippingCompany = headerToCopy.shippingCompany;
        creationDate = headerToCopy.creationDate;
        transactionRefNumber = headerToCopy.transactionRefNumber;
        
        if ( headerToCopy.invoices != null ) {
            invoices = new ArrayList<ShippingInvoice>( headerToCopy.invoices.size() );
            for ( ShippingInvoice invoiceToCopy : headerToCopy.invoices ) {
                ShippingInvoiceHistory invoice = new ShippingInvoiceHistory();
                invoice.copyFields(invoiceToCopy);
                invoice.fileName = fileName;
                invoice.loadDate = loadDate;
                invoices.add(invoice);
                if ( invoiceToCopy.getInvoiceTrackingElements() != null ) {
                    for ( ShippingInvoiceTracking trackingToCopy : invoiceToCopy.getInvoiceTrackingElements() ) {
                        ShippingInvoiceTrackingHistory tracking = new ShippingInvoiceTrackingHistory();
                        tracking.copyFields(trackingToCopy);
                        tracking.fileName = fileName;
                        tracking.loadDate = loadDate;
                        invoice.addShippingInvoiceTracking(tracking);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ShippingHeaderHistory [");
        if (loadDate != null)
            builder.append("loadDate=").append(loadDate).append(", ");
        if (fileName != null)
            builder.append("fileName=").append(fileName).append(", ");
        if (openCustomField != null)
            builder.append("openCustomField=").append(openCustomField).append(", ");
        if (shippingCompany != null)
            builder.append("shippingCompany=").append(shippingCompany).append(", ");
        if (creationDate != null)
            builder.append("creationDate=").append(creationDate).append(", ");
        if (transactionRefNumber != null)
            builder.append("transactionRefNumber=").append(transactionRefNumber).append(", ");
        builder.append("]");
        return builder.toString();
    }

}
