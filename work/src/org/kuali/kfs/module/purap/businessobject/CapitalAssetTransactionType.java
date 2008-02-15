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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Capital Asset Transaction Type Business Object.
 */
public class CapitalAssetTransactionType extends PersistableBusinessObjectBase {

    private String capitalAssetTransactionTypeCode;
    private String capitalAssetTransactionTypeDescription;
    private boolean capitalAssetTransactionTypeServiceIndicator;
    private boolean active;

    /**
     * Default constructor.
     */
    public CapitalAssetTransactionType() {

    }
    
    /**
     * Constructs a CapitalAssetTransactionType.java.
     * @param capitalAssetTransactionTypeCode
     */
    public CapitalAssetTransactionType(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    public String getCapitalAssetTransactionTypeCode() {
        return capitalAssetTransactionTypeCode;
    }

    public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    public String getCapitalAssetTransactionTypeDescription() {
        return capitalAssetTransactionTypeDescription;
    }

    public void setCapitalAssetTransactionTypeDescription(String capitalAssetTransactionTypeDescription) {
        this.capitalAssetTransactionTypeDescription = capitalAssetTransactionTypeDescription;
    }

    public boolean getCapitalAssetTransactionTypeServiceIndicator() {
        return capitalAssetTransactionTypeServiceIndicator;
    }

    public void setCapitalAssetTransactionTypeServiceIndicator(boolean capitalAssetTransactionTypeServiceIndicator) {
        this.capitalAssetTransactionTypeServiceIndicator = capitalAssetTransactionTypeServiceIndicator;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("capitalAssetTransactionTypeCode", this.capitalAssetTransactionTypeCode);
        return m;
    }
}
