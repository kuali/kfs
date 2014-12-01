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
package org.kuali.kfs.module.purap.batch;

import java.io.File;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;

/**
 * Batch input type for the electronic invoice job.
 */
public class ElectronicInvoiceInputFileType extends XmlBatchInputFileTypeBase {
    protected String reportPath;
    protected String reportPrefix;
    protected String reportExtension;
    
    /**
     * Returns the identifier of the electronic invoice file type
     * 
     * @return the electronic invoice file type identifier
     */
    public String getFileTypeIdentifer() {
        return PurapConstants.ELECTRONIC_INVOICE_FILE_TYPE_INDENTIFIER;
    }

    public boolean validate(Object parsedFileContents) {
        return true;
    }

    public String getTitleKey() {
        return PurapKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_EINVOICE;
    }

    public String getAuthorPrincipalName(File file) {
        return null;
    }

    public String getFileName(String principalName, 
                              Object parsedFileContents, 
                              String fileUserIdentifer) {
        
        if (!(parsedFileContents instanceof ElectronicInvoice)){
            throw new RuntimeException("Invalid object type.");
        }
        
        String fileName = ((ElectronicInvoice)parsedFileContents).getFileName();
        if (fileName == null) return fileUserIdentifer;
        int whereDot = fileName.lastIndexOf('.');
        
        return fileName.substring(0, whereDot);
    }
    
    
    /**
     * Sets the reportPath attribute value.
     * @param reportPath The reportPath to set.
     */
    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    /**
     * Sets the reportExtension attribute value.
     * @param reportExtension The reportExtension to set.
     */
    public void setReportExtension(String reportExtension) {
        this.reportExtension = reportExtension;
    }

    /**
     * Sets the fileReportPrefix attribute value.
     * @param fileReportPrefix The fileReportPrefix to set.
     */
    public void setReportPrefix(String reportPrefix) {
        this.reportPrefix = reportPrefix;
    }

    public String getReportPath() {
        return reportPath;
    }

    public String getReportPrefix() {
        return reportPrefix;
    }

    public String getReportExtension() {
        return reportExtension;
    }
}

