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
/*
 * Created on Jul 9, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiInteger;

public class AchAccountNumber extends TimestampedBusinessObjectBase {

    private KualiInteger id;
    private String achBankAccountNbr;

    public AchAccountNumber() {
        super();
    }


    /**
     * Gets the id attribute.
     * 
     * @return Returns the id.
     */
    public KualiInteger getId() {
        return id;
    }


    /**
     * Sets the id attribute value.
     * 
     * @param id The id to set.
     */
    public void setId(KualiInteger id) {
        this.id = id;
    }


    /**
     * Gets the achBankAccountNbr attribute.
     * 
     * @return Returns the achBankAccountNbr.
     */
    public String getAchBankAccountNbr() {
        return achBankAccountNbr;
    }


    /**
     * Sets the achBankAccountNbr attribute value.
     * 
     * @param achBankAccountNbr The achBankAccountNbr to set.
     */
    public void setAchBankAccountNbr(String achBankAccountNbr) {
        this.achBankAccountNbr = achBankAccountNbr;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }
}
