/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a Travel Per Diem business object.
 */
public class TravelPerDiem extends PersistableBusinessObjectBase implements FiscalYearBasedBusinessObject {
    protected Integer universityFiscalYear;
    protected String perDiemCountryName;
    protected KualiDecimal perDiemRate;
    protected String perDiemCountryText;

    protected transient SystemOptions fiscalYear;

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
     * Gets the fiscalYear attribute.
     * 
     * @return Returns the fiscalYear.
     */
    public SystemOptions getFiscalYear() {
        return fiscalYear;
    }

    /**
     * Sets the fiscalYear attribute value.
     * 
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(SystemOptions fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("perDiemCountryName", this.perDiemCountryName);
        return m;
    }
}
