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
package org.kuali.kfs.module.cam.document.service;

import java.lang.reflect.InvocationTargetException;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAllocationType;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;

public interface AssetPaymentService {

	/**
	 * Finds out the maximum value of payment sequence for an asset
	 * 
	 * @param assetPayment
	 *            Asset Payment
	 * @return Maximum sequence value of asset payment within an asset
	 */
	Integer getMaxSequenceNumber(Long capitalAssetNumber);

	/**
	 * Checks if asset payment is federally funder or not
	 * 
	 * @param assetPayment
	 *            Payment record
	 * @return True if financial object sub type code indicates federal
	 *         contribution
	 */
	boolean isPaymentFederalOwned(AssetPayment assetPayment);

	/**
	 * Checks active status of financial object of the payment
	 * 
	 * @param assetPayment
	 *            Payment record
	 * @return True if object is active
	 */
	boolean isPaymentFinancialObjectActive(AssetPayment assetPayment);

	/**
	 * Stores the approved asset payment detail records in the asset payment
	 * table, and updates the total cost of the asset in the asset table
	 * 
	 * @param assetPaymentDetail
	 */
	void processApprovedAssetPayment(AssetPaymentDocument assetPaymentDocument);

	/**
	 * This method uses reflection and performs below steps on all Amount fields
	 * <li>If it is a depreciation field, then reset the value to null, so that
	 * they don't get copied to offset payments</li> <li>If it is an amount
	 * field, then reverse the amount by multiplying with -1</li>
	 * 
	 * @param offsetPayment
	 *            Offset payment
	 * @param reverseAmount
	 *            true if amounts needs to be multiplied with -1
	 * @param nullPeriodDepreciation
	 *            true if depreciation period amount needs to be null
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	void adjustPaymentAmounts(AssetPayment assetPayment, boolean reverseAmount, boolean nullPeriodDepreciation) throws IllegalAccessException, InvocationTargetException;

	/**
	 * Checks if payment is eligible for GL posting
	 * 
	 * @param assetPayment
	 *            AssetPayment
	 * @return true if elgible for GL posting
	 */
	boolean isPaymentEligibleForGLPosting(AssetPayment assetPayment);

	/**
	 * Checks if object sub type is non depreciable federally owned
	 * 
	 * @param string
	 *            objectSubType
	 * @return true if is NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES
	 */
	boolean isNonDepreciableFederallyOwnedObjSubType(String objectSubType);

	/**
	 * sets in an assetPaymentDetail BO the posting year and posting period that
	 * is retrived from the university date table using the asset payment posted
	 * date as a key.
	 * 
	 * @param assetPaymentDetail
	 * @return boolean
	 */
	boolean extractPostedDatePeriod(AssetPaymentDetail assetPaymentDetail);

	/**
	 * Returns asset payment details quantity
	 * 
	 * @param assetGlobal
	 * @return Integer
	 */
	Integer getAssetPaymentDetailQuantity(AssetGlobal assetGlobal);

	/**
	 * Validates the assets inputed in the asset payment document
	 * 
	 * @param errorPath
	 * @param asset
	 * @return
	 */
	boolean validateAssets(String errorPath, Asset asset);

	/**
	 * This method determines whether or not an asset has different object sub
	 * type codes in its documents.
	 * 
	 * @return true when the asset has payments with object codes that point to
	 *         different object sub type codes
	 */
	boolean hasDifferentObjectSubTypes(AssetPaymentDocument document);

	/**
	 * Check if payment is eligible for CAPITALIZATION GL posting.
	 * 
	 * @param assetPayment
	 * @return
	 */
	boolean isPaymentEligibleForCapitalizationGLPosting(AssetPayment assetPayment);

	/**
	 * Check if payment is eligible for ACCUMMULATE_DEPRECIATION GL posting.
	 * 
	 * @param assetPayment
	 * @return
	 */
	boolean isPaymentEligibleForAccumDeprGLPosting(AssetPayment assetPayment);

	/**
	 * Check if payment is eligible for OFFSET_AMOUNT GL posting.
	 * 
	 * @param assetPayment
	 * @return
	 */
	boolean isPaymentEligibleForOffsetGLPosting(AssetPayment assetPayment);

	/**
	 * Return the AssetPaymentDistributionType associated with the provided
	 * code.
	 * 
	 * 
	 * @param distributionCode
	 * @return AssetPaymentDistributionType
	 */
	AssetPaymentAllocationType getAssetDistributionType(String distributionCode);
}
