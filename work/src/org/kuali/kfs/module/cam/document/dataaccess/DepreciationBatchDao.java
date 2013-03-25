/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.dataaccess;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Interface declaring DAO methods required by CAMS depreciation batch job
 */
public interface DepreciationBatchDao {

    /**
     * Updates payments as batch, columns updated are accumulated depreciation amount and current period column decided by fiscal
     * period
     *
     * @param assetPayments Batch of asset payments
     * @param fiscalPeriod Current fiscal period
     */
    public void updateAssetPayments(List<AssetPaymentInfo> assetPayments, Integer fiscalPeriod);

    /**
     * Sum all period column and set as previous year value and then reset period columns with zero dollar
     *
     * @param fiscalMonth Fiscal period
     * @throws Exception
     */
    public void resetPeriodValuesWhenFirstFiscalPeriod(Integer fiscalPeriod) throws Exception;

    /**
     * Updates depreciation and service date for all the assets created after last fiscal period date
     *
     * @param fiscalMonth fiscal month
     * @param fiscalYear fiscal year
     */
    public void updateAssetsCreatedInLastFiscalPeriod(Integer fiscalMonth, Integer fiscalYear);

    /**
     * Saves a batch of GL Pending entries
     *
     * @param glPendingEntries GLPE list to be saved
     */
    public void savePendingGLEntries(final List<GeneralLedgerPendingEntry> glPendingEntries);

    /**
     * Gets the list of depreciable asset payment list and corresponding details
     *
     * @param fiscalYear Fiscal year
     * @param fiscalMonth Fiscal period
     * @param depreciationDate Depreciation Date
     * @return List found matching depreciation criteria
     */
    public Collection<AssetPaymentInfo> getListOfDepreciableAssetPaymentInfo(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate);

    /**
     * Counts the number of assets which has (SUM(Primary Depreciation Amount - Accumulated Depreciation) - Salvage Amount) is zero
     *
     * @return count of assets matching condition
     */
    public Integer getFullyDepreciatedAssetCount();

    /**
     * Primary depreciation base amount for assets with Salvage Value depreciation method code.
     *
     * @return Map
     */
    public Map<Long, KualiDecimal> getPrimaryDepreciationBaseAmountForSV();

    /**
     * Retrieves asset and asset payment count eligible for depreciation
     *
     * @param fiscalYear
     * @param fiscalMonth
     * @param depreciationDate
     * @param inincludePending
     * @return
     */
    Object[] getAssetAndPaymentCount(Integer fiscalYear, Integer fiscalMonth, final Calendar depreciationDate, boolean includePending);

    /**
     * This method...
     *
     * @param fiscalYear
     * @param fiscalMonth
     * @param depreciationDate
     * @return
     */
    Object[] getFederallyOwnedAssetAndPaymentCount(Integer fiscalYear, Integer fiscalMonth, final Calendar depreciationDate);

    /**
     * Transfer document locked count
     *
     * @return
     */
    Integer getTransferDocLockedAssetCount();

    /**
     * Retirement document locked count
     *
     * @return
     */
    Integer getRetireDocLockedAssetCount();

    /**
     * Returns the list of locked asset by pending transfer and retirement documents
     *
     * @return
     */
    public Set<Long> getLockedAssets();

    // CSU 6702 BEGIN
    /**
     * No explanation provided
     * @param fiscalYear
     * @param fiscalMonth
     * @param depreciationDate
     * @param includeRetired
     * @return
     */
    public Collection<AssetPaymentInfo> getListOfDepreciableAssetPaymentInfoYearEnd(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate, boolean includeRetired);
    // CSU 6702 END

    /**
     * Depreciation (end of year) – Period 13 assets incorrect depreciation start date
     * <P> Get assets in accordance with,
     * <ul>
     * <li>Asset type has valid depreciation life time limit;
     * <li>Asset is created in period 13 of current fiscal year;
     * <li>Asset has depreciation convention restricted by parameter
     * <li>Asset are movable assets. Movable assets are defined by system parameter MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES
     * </ul>
     * @param lastFiscalYearDate
     * @param movableEquipmentObjectSubTypes
     * @param depreciationConventionCd
     * @return
     */
    public List<Map<String, Object>> getAssetsByDepreciationConvention(Date lastFiscalYearDate, List<String> movableEquipmentObjectSubTypes, String depreciationConventionCd);

    /**
     * Depreciation (end of year) – Period 13 assets incorrect depreciation start date
     * <P> Update asset in service date and depreciation date
     * @param selectedAssets
     * @param inServiceDate
     * @param depreciationDate
     */
    public void updateAssetInServiceAndDepreciationDate(List<String>selectedAssets, Date inServiceDate, Date depreciationDate);
}
