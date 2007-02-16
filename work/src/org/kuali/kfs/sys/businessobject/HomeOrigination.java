/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.kfs.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;


/**
 * 
 */
public class HomeOrigination extends PersistableBusinessObjectBase {

    private String finSystemHomeOriginationCode;
    private OriginationCode originationCode;
    

    /**
     * Default no-arg constructor.
     */
    public HomeOrigination() {
        super();
    }

    /**
     * Gets the finSystemHomeOriginationCode attribute.
     * 
     * @return Returns the finSystemHomeOriginationCode
     * 
     */
    public String getFinSystemHomeOriginationCode() {
        return finSystemHomeOriginationCode;
    }


    /**
     * Sets the finSystemHomeOriginationCode attribute.
     * 
     * @param finSystemHomeOriginationCode The finSystemHomeOriginationCode to set.
     * 
     */
    public void setFinSystemHomeOriginationCode(String finSystemHomeOriginationCode) {
        this.finSystemHomeOriginationCode = finSystemHomeOriginationCode;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("finSystemHomeOriginationCode", this.finSystemHomeOriginationCode);
        return m;
    }

    public OriginationCode getOriginationCode() {
        return originationCode;
    }

    public void setOriginationCode(OriginationCode originationCode) {
        this.originationCode = originationCode;
    }
}
