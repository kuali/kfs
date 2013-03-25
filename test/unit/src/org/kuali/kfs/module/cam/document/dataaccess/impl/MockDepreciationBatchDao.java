/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.dataaccess.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MockDepreciationBatchDao implements DepreciationBatchDao {
    private DepreciationBatchDao impl;
    private List<String> assetPaymentsStr = new ArrayList<String>();

    @Override
    public void updateAssetPayments(List<AssetPaymentInfo> assetPayments, Integer fiscalMonth) {
        impl.updateAssetPayments(assetPayments, fiscalMonth);
        for (AssetPaymentInfo assetPaymentInfo : assetPayments) {
            String t = fiscalMonth + "-" + assetPaymentInfo.getCapitalAssetNumber() + "-" + assetPaymentInfo.getPaymentSequenceNumber() + "-" + assetPaymentInfo.getTransactionAmount().bigDecimalValue();
            this.assetPaymentsStr.add(t);
        }
    }

    @Override
    public Integer getFullyDepreciatedAssetCount() {
        return impl.getFullyDepreciatedAssetCount();
    }

    @Override
    public Collection<AssetPaymentInfo> getListOfDepreciableAssetPaymentInfo(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate) {
        return impl.getListOfDepreciableAssetPaymentInfo(fiscalYear, fiscalMonth, depreciationDate);
    }

    @Override
    public void resetPeriodValuesWhenFirstFiscalPeriod(Integer fiscalPeriod) throws Exception {
        impl.resetPeriodValuesWhenFirstFiscalPeriod(fiscalPeriod);
    }

    @Override
    public void savePendingGLEntries(List<GeneralLedgerPendingEntry> glPendingEntries) {
        impl.savePendingGLEntries(glPendingEntries);

    }

    @Override
    public void updateAssetsCreatedInLastFiscalPeriod(Integer fiscalMonth, Integer fiscalYear) {
        impl.updateAssetsCreatedInLastFiscalPeriod(fiscalMonth, fiscalYear);

    }

    @Override
    public Map<Long, KualiDecimal> getPrimaryDepreciationBaseAmountForSV() {
        return impl.getPrimaryDepreciationBaseAmountForSV();
    }

    /**
     * Gets the assetPaymentsStr attribute.
     *
     * @return Returns the assetPaymentsStr.
     */
    public List<String> getAssetPaymentsStr() {
        return assetPaymentsStr;
    }

    /**
     * Sets the assetPaymentsStr attribute value.
     *
     * @param assetPaymentsStr The assetPaymentsStr to set.
     */
    public void setAssetPaymentsStr(List<String> assetPaymentsStr) {
        this.assetPaymentsStr = assetPaymentsStr;
    }

    /**
     * Gets the impl attribute.
     *
     * @return Returns the impl.
     */
    public DepreciationBatchDao getImpl() {
        return impl;
    }

    /**
     * Sets the impl attribute value.
     *
     * @param impl The impl to set.
     */
    public void setImpl(DepreciationBatchDao impl) {
        this.impl = impl;
    }

    @Override
    public Object[] getAssetAndPaymentCount(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate, boolean includePending) {
        return impl.getAssetAndPaymentCount(fiscalYear, fiscalMonth, depreciationDate, includePending);
    }

    @Override
    public Object[] getFederallyOwnedAssetAndPaymentCount(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate) {
        return impl.getFederallyOwnedAssetAndPaymentCount(fiscalYear, fiscalMonth, depreciationDate);
    }

    @Override
    public Integer getRetireDocLockedAssetCount() {
        return impl.getRetireDocLockedAssetCount();
    }

    @Override
    public Integer getTransferDocLockedAssetCount() {
        return impl.getTransferDocLockedAssetCount();
    }

    @Override
    public Set<Long> getLockedAssets() {
        return impl.getLockedAssets();
    }

    @Override
    public Collection<AssetPaymentInfo> getListOfDepreciableAssetPaymentInfoYearEnd(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate, boolean includeRetired) {
        return impl.getListOfDepreciableAssetPaymentInfoYearEnd(fiscalYear, fiscalMonth, depreciationDate, includeRetired);
    }

    @Override
    public List<Map<String, Object>> getAssetsByDepreciationConvention(Date lastFiscalYearDate, List<String> movableEquipmentObjectSubTypes, String depreciationConventionCd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateAssetInServiceAndDepreciationDate(List<String> selectedAssets, Date inServiceDate, Date depreciationDate) {
        // TODO Auto-generated method stub

    }
}
