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

package org.kuali.kfs.integration.cab.businessobject;

import org.kuali.kfs.integration.cab.CapitalAssetBuilderAssetTransactionType;

/**
 * Asset Transaction Type Business Object.
 */
public class AssetTransactionType implements CapitalAssetBuilderAssetTransactionType {

    private String capitalAssetTransactionTypeCode;
    private String capitalAssetTransactionTypeDescription;
    private boolean capitalAssetNonquantityDrivenAllowIndicator;
    private String capitalAssetQuantitySubtypeRequiredText;
    private String capitalAssetNonquantitySubtypeRequiredText;
    private boolean active;

    /**
     * Constructs a CapitalAssetTransactionType.java.
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

    public void setCapitalAssetNonquantitySubtypeRequiredText(
            String capitalAssetNonquantitySubtypeRequiredText) {
        this.capitalAssetNonquantitySubtypeRequiredText = capitalAssetNonquantitySubtypeRequiredText;
    }

    public String getCapitalAssetQuantitySubtypeRequiredText() {
        return capitalAssetQuantitySubtypeRequiredText;
    }

    public void setCapitalAssetQuantitySubtypeRequiredText(
            String capitalAssetQuantitySubtypeRequiredText) {
        this.capitalAssetQuantitySubtypeRequiredText = capitalAssetQuantitySubtypeRequiredText;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void prepareForWorkflow() {}

    public void refresh() {}
}
