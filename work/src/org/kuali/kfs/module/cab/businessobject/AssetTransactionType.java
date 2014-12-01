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
