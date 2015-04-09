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
package org.kuali.kfs.module.cam.batch.service;

import java.util.Collection;

import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;

public interface AssetDepreciationService {
    /**
     * This method runs depreciation process
     */
    public void runDepreciation();


    // CSU 6702 BEGIN
    /**
     * This method runs depreciation process
     */
    public void runYearEndDepreciation(Integer fiscalYearToDepreciate);
    // CSU 6702 END
    
    /**
     * Sets depreciation batch dao implementation
     * 
     * @param depreciationBatchDao
     */
    public void setDepreciationBatchDao(DepreciationBatchDao depreciationBatchDao);

    /**
     * This method retrieves a list of valid asset object codes for a particular fiscal year
     * 
     * @param fiscalYear
     * @return Collection<AssetObjectCode>
     */
    public Collection<AssetObjectCode> getAssetObjectCodes(Integer fiscalYear);

}
