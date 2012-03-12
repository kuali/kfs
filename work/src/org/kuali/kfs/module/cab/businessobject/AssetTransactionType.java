/*
 * Copyright 2006-2008 The Kuali Foundation
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

package org.kuali.kfs.module.cab.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cab.CapitalAssetBuilderAssetTransactionType;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Asset Transaction Type Business Object.
 */
public class AssetTransactionType extends PersistableBusinessObjectBase implements CapitalAssetBuilderAssetTransactionType, MutableInactivatable {

    private String capitalAssetTransactionTypeCode;
    private String capitalAssetTransactionTypeDescription;
    private boolean capitalAssetNonquantityDrivenAllowIndicator;
    private String capitalAssetQuantitySubtypeRequiredText;
    private String capitalAssetNonquantitySubtypeRequiredText;
    private boolean active;

    /**
     * Default constructor.
     */
    public AssetTransactionType() {

    }

    /**
     * Constructs a CapitalAssetTransactionType.java.
     * 
     * @param capitalAssetTransactionTypeCode
     */
    public AssetTransactionType(String capitalAssetTransactionTypeCode) {
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

    public boolean getCapitalAssetNonquantityDrivenAllowIndicator() {
        return capitalAssetNonquantityDrivenAllowIndicator;
    }

    public void setCapitalAssetNonquantityDrivenAllowIndicator(boolean capitalAssetNonquantityDrivenAllowIndicator) {
        this.capitalAssetNonquantityDrivenAllowIndicator = capitalAssetNonquantityDrivenAllowIndicator;
    }

    public String getCapitalAssetNonquantitySubtypeRequiredText() {
        return capitalAssetNonquantitySubtypeRequiredText;
    }

    public void setCapitalAssetNonquantitySubtypeRequiredText(String capitalAssetNonquantitySubtypeRequiredText) {
        this.capitalAssetNonquantitySubtypeRequiredText = capitalAssetNonquantitySubtypeRequiredText;
    }

    public String getCapitalAssetQuantitySubtypeRequiredText() {
        return capitalAssetQuantitySubtypeRequiredText;
    }

    public void setCapitalAssetQuantitySubtypeRequiredText(String capitalAssetQuantitySubtypeRequiredText) {
        this.capitalAssetQuantitySubtypeRequiredText = capitalAssetQuantitySubtypeRequiredText;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("capitalAssetTransactionTypeCode", this.capitalAssetTransactionTypeCode);
        return m;
    }
}
