/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.KualiCodeBase;

public class TypeRestrictionCode extends KualiCodeBase {

    private boolean permanentIndicator;

    /**
     * This method gets the permanentIndicator attribute
     * 
     * @return permanentIndicator
     */
    public boolean getPermanentIndicator() {
        return permanentIndicator;
    }

    /**
     * This method sets the permanentIndicator attribute
     * 
     * @param permanentIndicator The permanentIndicator to set.
     */
    public void setPermanentIndicator(boolean permanentIndicator) {
        this.permanentIndicator = permanentIndicator;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KUALICODEBASE_CODE, this.code);
        return m;
    }

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(code)) {
            return KFSConstants.EMPTY_STRING;
        }
        return super.getCodeAndDescription();
    }

}
