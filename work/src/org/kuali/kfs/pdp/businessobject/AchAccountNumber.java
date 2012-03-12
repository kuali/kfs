/*
 * Copyright 2007 The Kuali Foundation
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
