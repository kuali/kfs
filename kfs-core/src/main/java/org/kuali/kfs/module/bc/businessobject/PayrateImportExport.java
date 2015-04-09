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
package org.kuali.kfs.module.bc.businessobject;

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class PayrateImportExport extends TransientBusinessObjectBase {
    private String positionUnionCode;
    private Date csfFreezeDate;
    private int exportCount;
    private String fileName;
    private int importCount;
    private int updateCount;
    
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

    public Date getCsfFreezeDate() {
        return csfFreezeDate;
    }

    public void setCsfFreezeDate(Date csfFreezeDate) {
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
        this.positionUnionCode = positionUnionCode;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put("positionUnionCode", positionUnionCode);
        m.put("csfFreezeDate", csfFreezeDate);
        
        return m;
    }

}
