/*
 * Copyright 2008 The Kuali Foundation
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
