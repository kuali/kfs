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
package org.kuali.kfs.module.cam.document.validation.impl;

import java.math.BigDecimal;

import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.util.distribution.AssetDistribution;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validate Asset Distribution (allocation) sums
 */
public class AssetPaymentAllocationValidation extends GenericValidation {

	private AssetPaymentService assetPaymentService;

	/**
	 * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
	 */
	@Override
    public boolean validate(AttributedDocumentEvent event) {
		boolean valid = true;

		AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();
		AssetDistribution distributor = assetPaymentDocument.getAssetPaymentDistributor();

		if (CamsPropertyConstants.AssetPaymentAllocation.ASSET_DISTRIBUTION_BY_PERCENTAGE_CODE.equals(assetPaymentDocument.getAssetPaymentAllocationTypeCode())) {
			valid &= validatePercentSum(assetPaymentDocument);
		} else if (CamsPropertyConstants.AssetPaymentAllocation.ASSET_DISTRIBUTION_BY_AMOUNT_CODE.equals(assetPaymentDocument.getAssetPaymentAllocationTypeCode())) {
			valid = validateAmountSum(assetPaymentDocument);
		}

		return valid;
	}

	/**
	 * Allocation by amount must sum to the accounting line total
	 *
	 * @param assetPaymentDocument
	 * @return true if the amounts are correct
	 */
	protected boolean validateAmountSum(AssetPaymentDocument assetPaymentDocument) {
	    KualiDecimal total = getAllocatedTotal(assetPaymentDocument);

		KualiDecimal sourceTotal = getSourceLinesTotal(assetPaymentDocument);

		if (!total.equals(sourceTotal)) {
			GlobalVariables.getMessageMap().putErrorForSectionId(CamsPropertyConstants.COMMON_ERROR_SECTION_ID, CamsKeyConstants.AssetPaymentAllocation.ERROR_AMOUNT_NOT_EQUAL);
			return false;
		}
		return true;
	}

	/**
	 * @param assetPaymentDocument
	 * @return sum of the source accounting lines amounts.
	 */
	private KualiDecimal getSourceLinesTotal(AssetPaymentDocument assetPaymentDocument) {
		KualiDecimal sourceTotal = KualiDecimal.ZERO;
		for (Object sal : assetPaymentDocument.getSourceAccountingLines()) {
			sourceTotal = sourceTotal.add(((AccountingLineBase) sal).getAmount());
		}
		return sourceTotal;
	}

	/**
	 *
	 * @param assetPaymentDocument
	 * @return sum of allocated user value.
	 */
	private KualiDecimal getAllocatedTotal(AssetPaymentDocument assetPaymentDocument) {
		KualiDecimal total = KualiDecimal.ZERO;

		for (AssetPaymentAssetDetail apad : assetPaymentDocument.getAssetPaymentAssetDetail()) {
		    //KFSCNTRB-1209: if the document is created by the system from fp/purap side then
		    //use the property allocatedAmount to sum up the amounts of the assets else
		    //use allocatedUserValue to sum up the total.
		    if (assetPaymentDocument.isAllocationFromFPDocuments()) {
		        total = total.add(apad.getAllocatedAmount());
		    } else {
		        total = total.add(apad.getAllocatedUserValue());
		    }
		}
		return total;
	}

	/**
	 * Allocation by percentages must total 100%
	 *
	 * @param assetPaymentDocument
	 * @return true if the percentage is correct
	 */
	private boolean validatePercentSum(AssetPaymentDocument assetPaymentDocument) {
        BigDecimal total = new BigDecimal(0d);
        for (AssetPaymentAssetDetail apad : assetPaymentDocument.getAssetPaymentAssetDetail()) {
            BigDecimal buggyFix = new BigDecimal("" + apad.getAllocatedUserValuePct().doubleValue());
            total = total.add(buggyFix);
        }

		if (total.doubleValue() != 100.00d) {
			GlobalVariables.getMessageMap().putErrorForSectionId(CamsPropertyConstants.COMMON_ERROR_SECTION_ID, CamsKeyConstants.AssetPaymentAllocation.ERROR_PERCENT_NOT_100);
			return false;
		}
		return true;
	}

	public AssetPaymentService getAssetPaymentService() {
		return assetPaymentService;
	}

	public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
		this.assetPaymentService = assetPaymentService;
	}

}
