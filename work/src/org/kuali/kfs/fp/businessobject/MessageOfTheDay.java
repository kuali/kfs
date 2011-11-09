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

import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a "message of the day" object.  These messages are displayed on the associated 
 * portal page and may contain phrases, informational messages or any other message deemed worthy.
 */
public class MessageOfTheDay extends PersistableBusinessObjectBase {

    private String financialSystemOriginationCode;
    private String financialSystemMessageOfTheDayText;

    private OriginationCode financialSystemOrigination;

    /**
     * Default constructor.
     */
    public MessageOfTheDay() {

    }

    /**
     * Gets the financialSystemOriginationCode attribute.
     * 
     * @return Returns the financialSystemOriginationCode
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Sets the financialSystemOriginationCode attribute.
     * 
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     */
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }


    /**
     * Gets the financialSystemMessageOfTheDayText attribute.
     * 
     * @return Returns the financialSystemMessageOfTheDayText
     */
    public String getFinancialSystemMessageOfTheDayText() {
        return financialSystemMessageOfTheDayText;
    }

    /**
     * Sets the financialSystemMessageOfTheDayText attribute.
     * 
     * @param financialSystemMessageOfTheDayText The financialSystemMessageOfTheDayText to set.
     */
    public void setFinancialSystemMessageOfTheDayText(String financialSystemMessageOfTheDayText) {
        this.financialSystemMessageOfTheDayText = financialSystemMessageOfTheDayText;
    }

    /**
     * Gets the financialSystemOrigination attribute.
     * 
     * @return Returns the financialSystemOrigination.
     */
    public OriginationCode getFinancialSystemOrigination() {
        return financialSystemOrigination;
    }

    /**
     * Sets the financialSystemOrigination attribute value.
     * 
     * @param financialSystemOrigination The financialSystemOrigination to set.
     * @deprecated
     */
    public void setFinancialSystemOrigination(OriginationCode financialSystemOrigination) {
        this.financialSystemOrigination = financialSystemOrigination;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialSystemOriginationCode", this.financialSystemOriginationCode);
        return m;
    }

}
