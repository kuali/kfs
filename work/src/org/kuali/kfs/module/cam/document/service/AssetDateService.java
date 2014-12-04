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
package org.kuali.kfs.module.cam.document.service;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetDepreciationConvention;
import org.kuali.kfs.module.cam.businessobject.AssetType;

public interface AssetDateService {

    /**
     * Last inventory Date shall be updated whenever changes made to location or tag number
     */
    void checkAndUpdateLastInventoryDate(Asset copyAsset, Asset newAsset);

    /**
     * In Asset Edit, DepreciationDate shall be updated whenever In-service Date changed.
     */
    void checkAndUpdateDepreciationDate(Asset copyAsset, Asset newAsset);

    /**
     * In Asset Edit, Fiscal year and period code shall be updated whenever In-service Date changed.
     */
    void checkAndUpdateFiscalYearAndPeriod(Asset copyAsset, Asset newAsset);

    /**
     * This method computes the depreciation date based on input parameters
     *
     * @param assetType Asset Type Object
     * @param depreciationConvention Depreciation Convention for the asset
     * @param inServiceDate Current in-service date value
     * @return Computed Asset Depreciation Date
     */
    java.sql.Date computeDepreciationDate(AssetType assetType, AssetDepreciationConvention depreciationConvention, java.sql.Date inServiceDate);

    java.sql.Date computeDepreciationDateForPeriod13(String depreciationConventionCode, java.sql.Date inServiceDate);

}
