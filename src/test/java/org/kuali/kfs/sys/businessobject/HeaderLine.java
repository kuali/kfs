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
