/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.businessobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

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
		return getAllocatedAmount().add(getPreviousTotalCostAmount());
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
