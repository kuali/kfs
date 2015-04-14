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
package org.kuali.kfs.module.bc.document.web.struts;

import org.apache.struts.upload.FormFile;

public class PayrateImportExportForm extends BudgetExpansionForm {
    
    private String positionUnionCode;
    private String csfFreezeDate;
    private int exportCount;
    private String fileName;
    private int importCount;
    private int updateCount;
    private FormFile file;
    private String csfFreezeDateFormattedForExportFile;
    
    public FormFile getFile() {
        return file;
    }

    public void setFile(FormFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getImportCount() {
        return importCount;
    }

    public void setImportCount(int importCount) {
        this.importCount = importCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }
    
    public String getCsfFreezeDate() {
        return csfFreezeDate;
    }

    public void setCsfFreezeDate(String csfFreezeDate) {
        this.csfFreezeDate = csfFreezeDate;
    }

    public int getExportCount() {
        return exportCount;
    }

    public void setExportCount(int exportCount) {
        this.exportCount = exportCount;
    }

    public String getPositionUnionCode() {
        return positionUnionCode;
    }

    public void setPositionUnionCode(String positionUnionCode) {
        if (positionUnionCode != null) this.positionUnionCode = positionUnionCode.toUpperCase();
    }
    
    public String getCsfFreezeDateFormattedForExportFile() {
        return this.csfFreezeDateFormattedForExportFile;
    }
    
    public void setCsfFreezeDateFormattedForExportFile(String formattedDate) {
        this.csfFreezeDateFormattedForExportFile = formattedDate;
    }
}
