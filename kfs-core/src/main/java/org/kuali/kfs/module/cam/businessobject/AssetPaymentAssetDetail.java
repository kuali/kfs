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
package org.kuali.kfs.module.cam.businessobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class AssetPaymentAssetDetail extends PersistableBusinessObjectBase {
    private String documentNumber;
    private Long capitalAssetNumber;
    private KualiDecimal previousTotalCostAmount;
    private KualiDecimal allocatedAmount = KualiDecimal.ZERO;
    private KualiDecimal allocatedUserValue = KualiDecimal.ZERO;
    private BigDecimal allocatedUserValuePct = BigDecimal.ZERO;

    private Asset asset;
    private List<AssetPaymentDetail> assetPaymentDetails;
    private FinancialSystemDocumentHeader documentHeader;

    public AssetPaymentAssetDetail() {
        this.assetPaymentDetails = new ArrayList<AssetPaymentDetail>();
        this.documentHeader = new FinancialSystemDocumentHeader();
    }

    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public KualiDecimal getPreviousTotalCostAmount() {
        return previousTotalCostAmount;
    }

    public void setPreviousTotalCostAmount(KualiDecimal previousTotalCostAmount) {
        this.previousTotalCostAmount = previousTotalCostAmount;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.documentNumber != null) {
            m.put("documentNumber", this.documentNumber.toString());
        }
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        return m;
    }

    public List<AssetPaymentDetail> getAssetPaymentDetails() {
        return assetPaymentDetails;
    }

    public void setAssetPaymentDetails(List<AssetPaymentDetail> assetPaymentDetails) {
        this.assetPaymentDetails = assetPaymentDetails;
    }

    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * Get the allocated amount
     */
    public KualiDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    /**
     * Set the allocated amount
     */
    public void setAllocatedAmount(KualiDecimal allocatedAmount) {
        if (allocatedAmount == null) {
            this.allocatedAmount = KualiDecimal.ZERO;
        } else {
            this.allocatedAmount = allocatedAmount;
        }
    }

	/**
	 * Set the value the user allocates when editable
	 */
	public void setAllocatedUserValue(KualiDecimal allocatedUserValue) {
        if (allocatedUserValue == null) {
            this.allocatedUserValue = KualiDecimal.ZERO;
        } else {
            this.allocatedUserValue = allocatedUserValue;
        }
		setAllocatedAmount(allocatedUserValue);
	}

	/**
	 * Get the value the user allocates when editable
	 */
	public KualiDecimal getAllocatedUserValue() {
		return allocatedUserValue;
	}

    /**
     * Return the New total allocation amount
     * @return
     */
    public KualiDecimal getNewTotal() {
        KualiDecimal previousCostAmount = getPreviousTotalCostAmount();
        // KFSCNTRB-1667: if previous cost doesn't exist, regard the previous total as 0 amount
        if (ObjectUtils.isNull(previousCostAmount)) {
            previousCostAmount = new KualiDecimal(0);
        }
        return getAllocatedAmount().add(previousCostAmount);
    }

    /**
     * Return the percent invariant value if percentages are used
     */
    public BigDecimal getAllocatedUserValuePct() {
        return allocatedUserValuePct;
    }

    /**
     * Sets the percent invariant value if percentages are used
     */
    public void setAllocatedUserValuePct(BigDecimal allocatedUserValuePct) {
        this.allocatedUserValuePct = allocatedUserValuePct;
    }
}
