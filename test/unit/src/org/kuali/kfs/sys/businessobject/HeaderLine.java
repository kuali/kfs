/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.batch.FlatFileData;
import org.kuali.kfs.sys.batch.FlatFileTransactionInformation;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;



public class HeaderLine extends TransientBusinessObjectBase implements FlatFileData {
    private Date scannedInvoiceDate; //the date when the customer paid the invoice.
    private String lockboxNumber; //a unique number assigned to each processing organization.
    private List<DetailLine> detailLine;
    private FlatFileTransactionInformation fileTransactionInformation;
    


    public List<DetailLine> getDetailLine() {
        return detailLine;
    }



    public void setDetailLine(List<DetailLine> detailLine) {
        this.detailLine = detailLine;
    }



    public HeaderLine() {
        
        detailLine = new ArrayList<DetailLine>();
    }

    public Date getScannedInvoiceDate() {
        return scannedInvoiceDate;
    }

    public void setScannedInvoiceDate(Date scannedInvoiceDate) {
        this.scannedInvoiceDate = scannedInvoiceDate;
    }

    public String getLockboxNumber() {
        return lockboxNumber;
    }

    public void setLockboxNumber(String lockboxNumber) {
        this.lockboxNumber = lockboxNumber;
    }

   

    
    
    public FlatFileTransactionInformation getFlatFileTransactionInformation() {
        if (fileTransactionInformation == null ) {
          this.fileTransactionInformation = new FlatFileTransactionInformation(getLockboxNumber());
        }
        return fileTransactionInformation;
    }

    @SuppressWarnings("unchecked")
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pkMap = new LinkedHashMap();
        pkMap.put("lockboxNumber", this.lockboxNumber);
        return pkMap;
    }
    
    
    
    
    
 

}
