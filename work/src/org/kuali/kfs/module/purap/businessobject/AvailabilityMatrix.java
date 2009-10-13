/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.businessobject;

import org.kuali.kfs.module.purap.PurapConstants;

public enum AvailabilityMatrix {
    TRAN_TYPE_ONE_NEW ("capitalAssetTransactionTypeCode", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.EACH),
    TRAN_TYPE_ONE_MOD ("capitalAssetTransactionTypeCode", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.EACH),
    TRAN_TYPE_IND_NEW ("capitalAssetTransactionTypeCode", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.EACH),
    TRAN_TYPE_IND_MOD ("capitalAssetTransactionTypeCode", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.EACH),
    TRAN_TYPE_MULT_NEW ("capitalAssetTransactionTypeCode", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.EACH),
    TRAN_TYPE_MULT_MOD ("capitalAssetTransactionTypeCode", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.EACH),
    
    ASSET_NUMBER_ONE_NEW ("itemCapitalAssets.capitalAssetNumber", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    ASSET_NUMBER_ONE_MOD ("itemCapitalAssets.capitalAssetNumber", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.ONCE),
    ASSET_NUMBER_IND_NEW ("itemCapitalAssets.capitalAssetNumber", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    ASSET_NUMBER_IND_MOD ("itemCapitalAssets.capitalAssetNumber", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.EACH),
    ASSET_NUMBER_MULT_NEW ("itemCapitalAssets.capitalAssetNumber", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    ASSET_NUMBER_MULT_MOD ("itemCapitalAssets.capitalAssetNumber", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.ONCE),

    COMMENTS_ONE_NEW ("capitalAssetNoteText", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    COMMENTS_ONE_MOD ("capitalAssetNoteText", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.ONCE),
    COMMENTS_IND_NEW ("capitalAssetNoteText", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.EACH),
    COMMENTS_IND_MOD ("capitalAssetNoteText", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.EACH),
    COMMENTS_MULT_NEW ("capitalAssetNoteText", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    COMMENTS_MULT_MOD ("capitalAssetNoteText", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.ONCE),

    NOT_CURRENT_FY_ONE_NEW ("capitalAssetNotReceivedCurrentFiscalYearIndicator", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    NOT_CURRENT_FY_ONE_MOD ("capitalAssetNotReceivedCurrentFiscalYearIndicator", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    NOT_CURRENT_FY_IND_NEW ("capitalAssetNotReceivedCurrentFiscalYearIndicator", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.EACH),
    NOT_CURRENT_FY_IND_MOD ("capitalAssetNotReceivedCurrentFiscalYearIndicator", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    NOT_CURRENT_FY_MULT_NEW ("capitalAssetNotReceivedCurrentFiscalYearIndicator", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    NOT_CURRENT_FY_MULT_MOD ("capitalAssetNotReceivedCurrentFiscalYearIndicator", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    
    ASSET_TYPE_ONE_NEW ("capitalAssetTypeCode", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    ASSET_TYPE_ONE_MOD ("capitalAssetTypeCode", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    ASSET_TYPE_IND_NEW ("capitalAssetTypeCode", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.EACH),
    ASSET_TYPE_IND_MOD ("capitalAssetTypeCode", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    ASSET_TYPE_MULT_NEW ("capitalAssetTypeCode", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    ASSET_TYPE_MULT_MOD ("capitalAssetTypeCode", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),

    MANUFACTURER_ONE_NEW ("capitalAssetManufacturerName", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    MANUFACTURER_ONE_MOD ("capitalAssetManufacturerName", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    MANUFACTURER_IND_NEW ("capitalAssetManufacturerName", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.EACH),
    MANUFACTURER_IND_MOD ("capitalAssetManufacturerName", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    MANUFACTURER_MULT_NEW ("capitalAssetManufacturerName", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    MANUFACTURER_MULT_MOD ("capitalAssetManufacturerName", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),

    MODEL_ONE_NEW ("capitalAssetModelDescription", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    MODEL_ONE_MOD ("capitalAssetModelDescription", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    MODEL_IND_NEW ("capitalAssetModelDescription", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.EACH),
    MODEL_IND_MOD ("capitalAssetModelDescription", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    MODEL_MULT_NEW ("capitalAssetModelDescription", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    MODEL_MULT_MOD ("capitalAssetModelDescription", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),

    DESCRIPTION_ONE_NEW ("capitalAssetSystemDescription", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    DESCRIPTION_ONE_MOD ("capitalAssetSystemDescription", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    DESCRIPTION_IND_NEW ("capitalAssetSystemDescription", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    DESCRIPTION_IND_MOD ("capitalAssetSystemDescription", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    DESCRIPTION_MULT_NEW ("capitalAssetSystemDescription", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    DESCRIPTION_MULT_MOD ("capitalAssetSystemDescription", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    
    LOC_QUANTITY_ONE_NEW ("capitalAssetLocations.itemQuantity", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    LOC_QUANTITY_ONE_MOD ("capitalAssetLocations.itemQuantity", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    LOC_QUANTITY_IND_NEW ("capitalAssetLocations.itemQuantity", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.EACH),
    LOC_QUANTITY_IND_MOD ("capitalAssetLocations.itemQuantity", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    LOC_QUANTITY_MULT_NEW ("capitalAssetLocations.itemQuantity", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    LOC_QUANTITY_MULT_MOD ("capitalAssetLocations.itemQuantity", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),

    LOC_ADDRESS_ONE_NEW ("capitalAssetLocations.capitalAssetLine1Address", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    LOC_ADDRESS_ONE_MOD ("capitalAssetLocations.capitalAssetLine1Address", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    LOC_ADDRESS_IND_NEW ("capitalAssetLocations.capitalAssetLine1Address", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.EACH),
    LOC_ADDRESS_IND_MOD ("capitalAssetLocations.capitalAssetLine1Address", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    LOC_ADDRESS_MULT_NEW ("capitalAssetLocations.capitalAssetLine1Address", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    LOC_ADDRESS_MULT_MOD ("capitalAssetLocations.capitalAssetLine1Address", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),

    HOW_MANY_ASSETS_ONE_NEW ("capitalAssetCountAssetNumber", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.ONCE),
    HOW_MANY_ASSETS_ONE_MOD ("capitalAssetCountAssetNumber", PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    HOW_MANY_ASSETS_IND_NEW ("capitalAssetCountAssetNumber", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    HOW_MANY_ASSETS_IND_MOD ("capitalAssetCountAssetNumber", PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    HOW_MANY_ASSETS_MULT_NEW ("capitalAssetCountAssetNumber", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.NEW, PurapConstants.CapitalAssetAvailability.NONE),
    HOW_MANY_ASSETS_MULT_MOD ("capitalAssetCountAssetNumber", PurapConstants.CapitalAssetSystemTypes.MULTIPLE, PurapConstants.CapitalAssetSystemStates.MODIFY, PurapConstants.CapitalAssetAvailability.NONE),
    
    ;
        
    public final String fieldName;
    public final String systemType;
    public final String systemState;
    public final String availableValue;
    

    private AvailabilityMatrix(String fieldName, String systemType, String systemState, String value) {
        this.fieldName = fieldName;
        this.systemType = systemType;
        this.systemState = systemState;
        this.availableValue = value;
    }
    
}
