/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.KualiCodeBase;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class TicklerKEMID extends PersistableBusinessObjectBase implements Inactivateable
{
    private String number;
    private String kemId;
    private boolean active;
    
    private KEMID kemIdLookup;
 
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap<String, String> toStringMapper() 
    {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.TICKLER_NUMBER,getNumber());
        m.put(EndowPropertyConstants.TICKLER_KEMID,getKemId());
        return m;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getKemId() {
        return kemId;
    }

    public void setKemId(String kemId) {
        this.kemId = kemId;
    }

    public KEMID getKemIdLookup() {
        return kemIdLookup;
    }

    public void setKemIdLookup(KEMID kemIdLookup) {
        this.kemIdLookup = kemIdLookup;
    }
    
}

