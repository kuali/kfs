/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.rice.kns.util.KualiDecimal;

public class PerDiemForLoad extends PerDiem {
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PerDiemForLoad.class);

    private String effectiveDateAsString;
    private String seasonBeginDateAsString;
    private String seasonEndDateAsString;
    private String expirationDateAsString;
    private String unusedValueHolder;

    /**
     * Gets the unusedValueHolder attribute.
     * 
     * @return Returns the unusedValueHolder.
     */
    public String getUnusedValueHolder() {
        return unusedValueHolder;
    }

    /**
     * Sets the unusedValueHolder attribute value.
     * 
     * @param unusedValueHolder The unusedValueHolder to set.
     */
    public void setUnusedValueHolder(String unusedValueHolder) {
        this.unusedValueHolder = unusedValueHolder;
    }

    /**
     * Gets the seasonBeginDateAsString attribute.
     * 
     * @return Returns the seasonBeginDateAsString.
     */
    public String getSeasonBeginDateAsString() {
        return seasonBeginDateAsString;
    }

    /**
     * Sets the seasonBeginDateAsString attribute value.
     * 
     * @param seasonBeginDateAsString The seasonBeginDateAsString to set.
     */
    public void setSeasonBeginDateAsString(String seasonBeginDateAsString) {
        this.seasonBeginDateAsString = seasonBeginDateAsString;
    }

    /**
     * Gets the seasonEndDateAsString attribute.
     * 
     * @return Returns the seasonEndDateAsString.
     */
    public String getSeasonEndDateAsString() {
        return seasonEndDateAsString;
    }

    /**
     * Sets the seasonEndDateAsString attribute value.
     * 
     * @param seasonEndDateAsString The seasonEndDateAsString to set.
     */
    public void setSeasonEndDateAsString(String seasonEndDateAsString) {
        this.seasonEndDateAsString = seasonEndDateAsString;
    }

    /**
     * Gets the effectiveDateAsString attribute.
     * 
     * @return Returns the effectiveDateAsString.
     */
    public String getEffectiveDateAsString() {
        return effectiveDateAsString;
    }

    /**
     * Sets the effectiveDateAsString attribute value.
     * 
     * @param effectiveDateAsString The effectiveDateAsString to set.
     */
    public void setEffectiveDateAsString(String effectiveDateAsString) {
        this.effectiveDateAsString = effectiveDateAsString;
    }
    
    /**
     * Gets the expirationDateAsString attribute. 
     * @return Returns the expirationDateAsString.
     */
    public String getExpirationDateAsString() {
        return expirationDateAsString;
    }

    /**
     * Sets the expirationDateAsString attribute value.
     * @param expirationDateAsString The expirationDateAsString to set.
     */
    public void setExpirationDateAsString(String expirationDateAsString) {
        this.expirationDateAsString = expirationDateAsString;
    }    

    /**
     * Sets the localMeals attribute value.
     * @param localMeals The localMeals to set.
     */
    public void setLocalMeals(String localMeals) {
        this.setMealsAndIncidentals(new KualiDecimal(localMeals));
    }
    
    /**
     * Sets the localMeals attribute value.
     * @param localMeals The localMeals to set.
     */
    public void setIncidentals(String incidentals) {
        this.setIncidentals(new KualiDecimal(incidentals));
    }
    
    /**
     * Sets the lodging attribute value.
     * @param lodging The lodging to set.
     */
    public void setLodging(String lodging) {
        this.setLodging(new KualiDecimal(lodging));
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = super.toStringMapper();
        
        map.put("effectiveDateAsString", this.getEffectiveDateAsString());
        map.put("seasonBeginDateAsString", this.getSeasonBeginDateAsString());
        map.put("seasonEndDateAsString", this.getSeasonEndDateAsString());
        
        return map;
    }
}
