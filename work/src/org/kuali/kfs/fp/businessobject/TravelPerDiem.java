/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class is used to represent a Travel Per Diem business object.
 */
public class TravelPerDiem extends PersistableBusinessObjectBase {
    private Integer universityFiscalYear;
    private String perDiemCountryName;
    private KualiDecimal perDiemRate;
    private String perDiemCountryText;

    /**
     * Default no-arg constructor.
     */
    public TravelPerDiem() {

    }

    /**
     * @return Returns the fiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * @param fiscalYear The fiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer fiscalYear) {
        this.universityFiscalYear = fiscalYear;
    }

    /**
     * @return Returns the perDiemCountryName.
     */
    public String getPerDiemCountryName() {
        return perDiemCountryName;
    }

    /**
     * @param perDiemCountryName The perDiemCountryName to set.
     */
    public void setPerDiemCountryName(String perDiemCountryName) {
        this.perDiemCountryName = perDiemCountryName;
    }

    /**
     * @return Returns the perDiemCountryText.
     */
    public String getPerDiemCountryText() {
        return perDiemCountryText;
    }

    /**
     * @param perDiemCountryText The perDiemCountryText to set.
     */
    public void setPerDiemCountryText(String perDiemCountryText) {
        this.perDiemCountryText = perDiemCountryText;
    }

    /**
     * @return Returns the perDiemRate.
     */
    public KualiDecimal getPerDiemRate() {
        return perDiemRate;
    }

    /**
     * @param perDiemRate The perDiemRate to set.
     */
    public void setPerDiemRate(KualiDecimal perDiemRate) {
        this.perDiemRate = perDiemRate;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("perDiemCountryName", this.perDiemCountryName);
        return m;
    }
}
