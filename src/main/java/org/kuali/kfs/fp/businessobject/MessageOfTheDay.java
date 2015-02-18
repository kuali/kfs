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
