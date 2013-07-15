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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.fp.businessobject.CapitalAssetAccountsGroupDetails;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAsset;
import org.kuali.kfs.module.cab.document.service.GlLineService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.CamsConstants.DocumentTypeName;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.util.ObjectValueUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentHeaderService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class GlLineServiceImpl implements GlLineService {
    private static final String CAB_DESC_PREFIX = "CAB created for FP ";

    protected BusinessObjectService businessObjectService;
    protected AssetGlobalService assetGlobalService;
    protected ObjectTypeService objectTypeService;
    protected DocumentService documentService;
    protected ParameterService parameterService;
    protected ParameterEvaluatorService parameterEvaluatorService;
    protected DocumentHeaderService documentHeaderService;


    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#createAssetGlobalDocument(java.util.List,
     *      org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    @Override
    public Document createAssetGlobalDocument(GeneralLedgerEntry primary, Integer capitalAssetLineNumber) throws WorkflowException {
        // initiate a new document
        MaintenanceDocument document = (MaintenanceDocument) documentService.getNewDocument(DocumentTypeName.ASSET_ADD_GLOBAL);

        // create asset global
        AssetGlobal assetGlobal = createAssetGlobal(primary, document);
        assetGlobal.setCapitalAssetBuilderOriginIndicator(true);
        assetGlobal.setAcquisitionTypeCode(assetGlobalService.getNewAcquisitionTypeCode());

        updatePreTagInformation(primary, document, assetGlobal, capitalAssetLineNumber);

        assetGlobal.getAssetPaymentDetails().addAll(createAssetPaymentDetails(primary, document, 0, capitalAssetLineNumber));

        // save the document
        document.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_NEW_ACTION);
        document.getDocumentHeader().setDocumentDescription(CAB_DESC_PREFIX + primary.getDocumentNumber());
        document.getNewMaintainableObject().setBusinessObject(assetGlobal);
        document.getNewMaintainableObject().setBoClass(assetGlobal.getClass());

        documentService.saveDocument(document);

        //mark the capital asset as processed..
        markCapitalAssetProcessed(primary, capitalAssetLineNumber);

        deactivateGLEntries(primary, document, capitalAssetLineNumber);

        return document;
    }

    protected void markCapitalAssetProcessed(GeneralLedgerEntry primary, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(primary.getDocumentNumber(), capitalAssetLineNumber);
        //if it is create asset...
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            capitalAssetInformation.setCapitalAssetProcessedIndicator(true);
            businessObjectService.save(capitalAssetInformation);
        }
    }

    protected void deactivateGLEntries(GeneralLedgerEntry entry, Document document, Integer capitalAssetLineNumber) {
        //now deactivate the gl line..
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry.getDocumentNumber(), capitalAssetLineNumber);

        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            List<CapitalAssetAccountsGroupDetails> groupAccountingLines = capitalAssetInformation.getCapitalAssetAccountsGroupDetails();
            Collection<GeneralLedgerEntry> documentGlEntries = findAllGeneralLedgerEntry(entry.getDocumentNumber());
            for (CapitalAssetAccountsGroupDetails accountingLine : groupAccountingLines) {
                //find the matching GL entry for this accounting line.
                Collection<GeneralLedgerEntry> glEntries = findMatchingGeneralLedgerEntries(documentGlEntries, accountingLine);
                for (GeneralLedgerEntry glEntry : glEntries) {
                    KualiDecimal lineAmount = accountingLine.getAmount();

                    //update submitted amount on the gl entry and save the results.
                    createGeneralLedgerEntryAsset(glEntry, document, capitalAssetLineNumber);
                    updateTransactionSumbitGlEntryAmount(glEntry, lineAmount);
                }
            }
        }
    }

    /**
     * This method reads the pre-tag information and creates objects for asset global document
     *
     * @param entry GL Line
     * @param document Asset Global Maintenance Document
     * @param assetGlobal Asset Global Object
     */
    protected void updatePreTagInformation(GeneralLedgerEntry entry, MaintenanceDocument document, AssetGlobal assetGlobal, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry.getDocumentNumber(), capitalAssetLineNumber);
        //if it is create asset...
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR.equals(capitalAssetInformation.getCapitalAssetActionIndicator())) {
                List<CapitalAssetInformationDetail> capitalAssetInformationDetails = capitalAssetInformation.getCapitalAssetInformationDetails();
                for (CapitalAssetInformationDetail capitalAssetInformationDetail : capitalAssetInformationDetails) {
                    // This is not added to constructor in CAMS to provide module isolation from CAMS
                    AssetGlobalDetail assetGlobalDetail = new AssetGlobalDetail();
                    assetGlobalDetail.setDocumentNumber(document.getDocumentNumber());
                    assetGlobalDetail.setCampusCode(capitalAssetInformationDetail.getCampusCode());
                    assetGlobalDetail.setBuildingCode(capitalAssetInformationDetail.getBuildingCode());
                    assetGlobalDetail.setBuildingRoomNumber(capitalAssetInformationDetail.getBuildingRoomNumber());
                    assetGlobalDetail.setBuildingSubRoomNumber(capitalAssetInformationDetail.getBuildingSubRoomNumber());
                    assetGlobalDetail.setSerialNumber(capitalAssetInformationDetail.getCapitalAssetSerialNumber());
                    assetGlobalDetail.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
                    assetGlobalDetail.setCampusTagNumber(capitalAssetInformationDetail.getCapitalAssetTagNumber());

                    AssetGlobalDetail uniqueAsset = new AssetGlobalDetail();
                    ObjectValueUtils.copySimpleProperties(assetGlobalDetail, uniqueAsset);
                    assetGlobalDetail.getAssetGlobalUniqueDetails().add(uniqueAsset);
                    assetGlobal.getAssetSharedDetails().add(assetGlobalDetail);
                }

                assetGlobal.setVendorName(capitalAssetInformation.getVendorName());
                assetGlobal.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE);
                assetGlobal.setCapitalAssetTypeCode(capitalAssetInformation.getCapitalAssetTypeCode());
                assetGlobal.setManufacturerName(capitalAssetInformation.getCapitalAssetManufacturerName());
                assetGlobal.setManufacturerModelNumber(capitalAssetInformation.getCapitalAssetManufacturerModelNumber());
                assetGlobal.setCapitalAssetDescription(capitalAssetInformation.getCapitalAssetDescription());
            }
        }
    }

   /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findCapitalAssetInformation(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    @Override
    public CapitalAssetInformation findCapitalAssetInformation(String documentNumber, Integer capitalAssetLineNumber) {
        Map<String, String> primaryKeys = new HashMap<String, String>(2);
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, documentNumber);
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.ASSET_LINE_NUMBER, capitalAssetLineNumber.toString());

        CapitalAssetInformation assetInformation = businessObjectService.findByPrimaryKey(CapitalAssetInformation.class, primaryKeys);
        return assetInformation;
    }

    @Override
    public List<CapitalAssetInformation> findAllCapitalAssetInformation(String documentNumber) {
        Map<String, String> primaryKeys = new HashMap<String, String>(1);
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, documentNumber);

        List<CapitalAssetInformation> assetInformation = (List<CapitalAssetInformation>) businessObjectService.findMatchingOrderBy(CapitalAssetInformation.class, primaryKeys, CabPropertyConstants.CapitalAssetInformation.ACTION_INDICATOR, true);

        return assetInformation;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findCapitalAssetInformationForGLLine(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    @Override
    public List<CapitalAssetInformation> findCapitalAssetInformationForGLLine(GeneralLedgerEntry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, entry.getDocumentNumber());

        List<CapitalAssetInformation> assetInformation = (List<CapitalAssetInformation>) businessObjectService.findMatchingOrderBy(CapitalAssetInformation.class, primaryKeys, CabPropertyConstants.CapitalAssetInformation.ACTION_INDICATOR, true);

        List<CapitalAssetInformation> matchingAssets = new ArrayList<CapitalAssetInformation>();

        for (CapitalAssetInformation capitalAsset : assetInformation) {
            addToCapitalAssets(matchingAssets, capitalAsset, entry);
        }

        return matchingAssets;
    }

    /**
     * Compares the gl line to the group accounting lines in each capital asset and
     * when finds a match, adds the capital asset to the list of matching assets
     * @param matchingAssets
     * @param capitalAsset
     * @param entry
     * @param capitalAssetLineType
     */
    protected void addToCapitalAssets(List<CapitalAssetInformation> matchingAssets, CapitalAssetInformation capitalAsset, GeneralLedgerEntry entry) {
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();

        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
            if (groupAccountLine.getDocumentNumber().equals(entry.getDocumentNumber()) &&
                    groupAccountLine.getChartOfAccountsCode().equals(entry.getChartOfAccountsCode()) &&
                    groupAccountLine.getAccountNumber().equals(entry.getAccountNumber()) &&
                    groupAccountLine.getFinancialObjectCode().equals(entry.getFinancialObjectCode())) {
                matchingAssets.add(capitalAsset);
                break;
            }
        }
    }

    @Override
    public long findUnprocessedCapitalAssetInformation( String documentNumber ) {
        Map<String, String> fieldValues = new HashMap<String, String>(2);
        fieldValues.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, documentNumber);
        fieldValues.put(CabPropertyConstants.CapitalAssetInformation.ASSET_PROCESSED_IND, KFSConstants.CapitalAssets.CAPITAL_ASSET_PROCESSED_IND);

        return businessObjectService.countMatching(CapitalAssetInformation.class, fieldValues);
    }

    @Override
    public Collection<GeneralLedgerEntry> findMatchingGeneralLedgerEntries( Collection<GeneralLedgerEntry> allGLEntries, CapitalAssetAccountsGroupDetails accountingDetails ) {
        Collection<GeneralLedgerEntry> matchingGLEntries = new ArrayList<GeneralLedgerEntry>();

        for ( GeneralLedgerEntry entry : allGLEntries ) {
            if ( doesGeneralLedgerEntryMatchAssetAccountingDetails(entry, accountingDetails) ) {
                matchingGLEntries.add(entry);
            }
        }

        return matchingGLEntries;
    }


    protected boolean doesGeneralLedgerEntryMatchAssetAccountingDetails( GeneralLedgerEntry entry, CapitalAssetAccountsGroupDetails accountingDetails ) {
        // this method will short-circuit and return false as soon as possible

        // sanity check - the arguments should already have the same document number
        if ( !StringUtils.equals( entry.getDocumentNumber(), accountingDetails.getDocumentNumber() ) ) {
            return false;
        }

        // required attributes - easy to compare
        if ( !StringUtils.equals( entry.getAccountNumber(), accountingDetails.getAccountNumber() ) ) {
            return false;
        }
        if ( !StringUtils.equals( entry.getFinancialObjectCode(), accountingDetails.getFinancialObjectCode() ) ) {
            return false;
        }
        if ( !StringUtils.equals( entry.getChartOfAccountsCode(), accountingDetails.getChartOfAccountsCode() ) ) {
            return false;
        }
        // account for blank equaling null
        if ( !StringUtils.equals( entry.getOrganizationReferenceId(), accountingDetails.getOrganizationReferenceId() ) ) {
            if ( StringUtils.isBlank( entry.getOrganizationReferenceId() )
                    && StringUtils.isBlank( accountingDetails.getOrganizationReferenceId() ) ) {
                // this is a match, keep going
            } else {
                return false;
            }
        }
        // optional attributes - need to account for blank being equivalent to dashes
        // it's always dashes on the CAB GL Entry table - but could be blank on the accounting details
        if ( !StringUtils.equals( entry.getSubAccountNumber(), accountingDetails.getSubAccountNumber() ) ) {
            if ( StringUtils.equals( entry.getSubAccountNumber(), KFSConstants.getDashSubAccountNumber() )
                    && StringUtils.isBlank( accountingDetails.getSubAccountNumber() ) ) {
                // this is a match, keep going
            } else {
                return false;
            }
        }
        if ( !StringUtils.equals( entry.getFinancialSubObjectCode(), accountingDetails.getFinancialSubObjectCode() ) ) {
            if ( StringUtils.equals( entry.getFinancialSubObjectCode(), KFSConstants.getDashFinancialSubObjectCode() )
                    && StringUtils.isBlank( accountingDetails.getFinancialSubObjectCode() ) ) {
                // this is a match, keep going
            } else {
                return false;
            }
        }
        if ( !StringUtils.equals( entry.getProjectCode(), accountingDetails.getProjectCode() ) ) {
            if ( StringUtils.equals( entry.getProjectCode(), KFSConstants.getDashProjectCode() )
                    && StringUtils.isBlank( accountingDetails.getProjectCode() ) ) {
                // this is a match, keep going
            } else {
                return false;
            }
        }

        /* KFSCNTRB-1657
         * The following code assumes CapitalAssetAccountsGroupDetails.financialDocumentLineTypeCode corresponds to D/C code in GL entry,
         * but that is inaccurate. In fact, financialDocumentLineTypeCode being F/T has nothing to do with D/C code; rather it comes from whether
         * the asset accounting line was from a source or target accounting line on the FP document.
         * On the other hand, the D/C code in GL entry is decided by the amount: positive amount corresponds to D; negative to C.
         * At least this is what's observed through tracing here.
         * Removing the following logic as it is unnecessary for matching the asset accounting line with GL entry, the rest of the criteria is good enough.
         */
/*        // compare lineTypeCode to debitCreditCode
        String capitalAssetLineTypeCode = KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE;
        if (!StringUtils.equals(capitalAssetLineTypeCode, accountingDetails.getFinancialDocumentLineTypeCode())) {
            return false;
        }
*/
        return true;
    }

    @Override
    public Collection<GeneralLedgerEntry> findAllGeneralLedgerEntry(String documentNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>(1);
        fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, documentNumber);

        return businessObjectService.findMatching(GeneralLedgerEntry.class, fieldValues);
    }

    protected void createGeneralLedgerEntryAsset(GeneralLedgerEntry entry, Document document, Integer capitalAssetLineNumber) {
        // KFSMI-9645 : check if the document is already referenced to prevent an OJB locking error
        for ( GeneralLedgerEntryAsset glEntryAsset : entry.getGeneralLedgerEntryAssets() ) {
            if ( glEntryAsset.getCapitalAssetManagementDocumentNumber().equals(document.getDocumentNumber() )
                    && glEntryAsset.getCapitalAssetBuilderLineNumber().equals(capitalAssetLineNumber ) ) {
                // an object with this key already exists, abort and don't attempt to add another
                return;
            }
        }
        // If we get here, add a child record with the document number
        GeneralLedgerEntryAsset entryAsset = new GeneralLedgerEntryAsset();
        entryAsset.setGeneralLedgerAccountIdentifier(entry.getGeneralLedgerAccountIdentifier());
        entryAsset.setCapitalAssetBuilderLineNumber(capitalAssetLineNumber);
        entryAsset.setCapitalAssetManagementDocumentNumber(document.getDocumentNumber());
        entry.getGeneralLedgerEntryAssets().add(entryAsset);
    }


    /**
     * Creates asset global
     *
     * @param entry GeneralLedgerEntry
     * @param maintDoc MaintenanceDocument
     * @return AssetGlobal
     */
    protected AssetGlobal createAssetGlobal(GeneralLedgerEntry entry, MaintenanceDocument maintDoc) {
        AssetGlobal assetGlobal = new AssetGlobal();
        assetGlobal.setOrganizationOwnerChartOfAccountsCode(entry.getChartOfAccountsCode());
        assetGlobal.setOrganizationOwnerAccountNumber(entry.getAccountNumber());
        assetGlobal.setDocumentNumber(maintDoc.getDocumentNumber());
        assetGlobal.setConditionCode(CamsConstants.Asset.CONDITION_CODE_E);

        // CSU 6702 BEGIN
        //year end changes
        String docType = DocumentTypeName.ASSET_ADD_GLOBAL;
        ParameterEvaluator evaluator = parameterEvaluatorService.getParameterEvaluator(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES, docType);
        if (evaluator.evaluationSucceeds()) {
            Integer closingYear = new Integer(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));
            if (entry.getUniversityFiscalYear().equals(closingYear + 1)) {
                //default asset global year end accounting period drop down to current period instead of closing period(period 13)
                assetGlobal.setUniversityFiscalPeriodName("");
            }
        }
        // CSU 6702 END

        return assetGlobal;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#createAssetPaymentDocument(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry, java.lang.Integer)
     */
    @Override
    public Document createAssetPaymentDocument(GeneralLedgerEntry primaryGlEntry, Integer capitalAssetLineNumber) throws WorkflowException {
        // Find out the GL Entry
        // initiate a new document
        AssetPaymentDocument document = (AssetPaymentDocument) documentService.getNewDocument(DocumentTypeName.ASSET_PAYMENT);
        document.setCapitalAssetBuilderOriginIndicator(true);

        //populate the capital asset line distribution amount code to the payment document.
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(primaryGlEntry.getDocumentNumber(), capitalAssetLineNumber);

        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            // If this was a shell Capital Asset Information record (for example GL Entries from enterprise feed or Vendor Credit Memo)
            // setup asset allocation info accordingly so it can be changed on Asset Payment Document
            if (ObjectUtils.isNull(capitalAssetInformation.getDistributionAmountCode())) {
                document.setAssetPaymentAllocationTypeCode(KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE);
                document.setAllocationFromFPDocuments(false);
            } else {
                document.setAssetPaymentAllocationTypeCode(capitalAssetInformation.getDistributionAmountCode());
                document.setAllocationFromFPDocuments(true);
            }
        }

        document.getDocumentHeader().setDocumentDescription(CAB_DESC_PREFIX + primaryGlEntry.getDocumentNumber());
        updatePreTagInformation(primaryGlEntry, document, capitalAssetLineNumber);

        // Asset Payment Detail - sourceAccountingLines on the document....
        document.getSourceAccountingLines().addAll(createAssetPaymentDetails(primaryGlEntry, document, 0, capitalAssetLineNumber));

        KualiDecimal assetAmount = KualiDecimal.ZERO;

        List<SourceAccountingLine> sourceAccountingLines = document.getSourceAccountingLines();
        for (SourceAccountingLine sourceAccountingLine : sourceAccountingLines) {
            assetAmount = assetAmount.add(sourceAccountingLine.getAmount());
        }

        List<AssetPaymentAssetDetail> assetPaymentDetails = document.getAssetPaymentAssetDetail();
        for (AssetPaymentAssetDetail assetPaymentDetail : assetPaymentDetails) {
            assetPaymentDetail.setAllocatedAmount(assetAmount);
        }

        // Asset payment asset detail
        // save the document
        documentService.saveDocument(document);
        markCapitalAssetProcessed(primaryGlEntry, capitalAssetLineNumber);
        deactivateGLEntries(primaryGlEntry, document, capitalAssetLineNumber);

        return document;
    }

    /**
     * Updates pre tag information received from FP document
     *
     * @param entry GeneralLedgerEntry
     * @param document AssetPaymentDocument
     */
    protected void updatePreTagInformation(GeneralLedgerEntry entry, AssetPaymentDocument document, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry.getDocumentNumber(), capitalAssetLineNumber);
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            //if it is modify asset...
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equals(capitalAssetInformation.getCapitalAssetActionIndicator())) {
                AssetPaymentAssetDetail assetPaymentAssetDetail = new AssetPaymentAssetDetail();
                assetPaymentAssetDetail.setDocumentNumber(document.getDocumentNumber());
                // get the allocated amount for the capital asset....
                assetPaymentAssetDetail.setCapitalAssetNumber(capitalAssetInformation.getCapitalAssetNumber());
                assetPaymentAssetDetail.setAllocatedAmount(KualiDecimal.ZERO);
                assetPaymentAssetDetail.setAllocatedUserValue(assetPaymentAssetDetail.getAllocatedAmount());
                assetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentAssetDetail.ASSET);
                Asset asset = assetPaymentAssetDetail.getAsset();
                if (ObjectUtils.isNotNull(asset)) {
                    assetPaymentAssetDetail.setPreviousTotalCostAmount(asset.getTotalCostAmount() != null ? asset.getTotalCostAmount() : KualiDecimal.ZERO);
                    document.getAssetPaymentAssetDetail().add(assetPaymentAssetDetail);
                }
            }
        }
    }

    /**
     * Creates asset payment details based on accounting lines distributed
     * for the given capital asset.
     * @param entry
     * @param document
     * @param seqNo
     * @param capitalAssetLineNumber
     * @return List<AssetPaymentDetail>
     */
    protected List<AssetPaymentDetail> createAssetPaymentDetails(GeneralLedgerEntry entry, Document document, int seqNo, Integer capitalAssetLineNumber) {
        List<AssetPaymentDetail> appliedPayments = new ArrayList<AssetPaymentDetail>();
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry.getDocumentNumber(), capitalAssetLineNumber);

        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            List<CapitalAssetAccountsGroupDetails> groupAccountingLines = capitalAssetInformation.getCapitalAssetAccountsGroupDetails();
            Integer paymentSequenceNumber = 1;

            for (CapitalAssetAccountsGroupDetails accountingLine : groupAccountingLines) {
                AssetPaymentDetail detail = new AssetPaymentDetail();
                //TODO
                // sub-object code, as well as sub-account, project code, and org ref id, shall not be populated from GL entry;
                // instead, they need to be passed from the original FP document for each individual accounting line to be stored in CapitalAssetAccountsGroupDetails,
                // and copied into each corresponding accounting line in Asset Payment here.

                detail.setDocumentNumber(document.getDocumentNumber());
                detail.setSequenceNumber(paymentSequenceNumber++);
                detail.setPostingYear(entry.getUniversityFiscalYear());
                detail.setPostingPeriodCode(entry.getUniversityFiscalPeriodCode());
                detail.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
                detail.setAccountNumber(replaceFiller(accountingLine.getAccountNumber()));
                detail.setSubAccountNumber(replaceFiller(accountingLine.getSubAccountNumber()));
                detail.setFinancialObjectCode(replaceFiller(accountingLine.getFinancialObjectCode()));
                detail.setFinancialSubObjectCode(replaceFiller(accountingLine.getFinancialSubObjectCode()));
                detail.setProjectCode(replaceFiller(accountingLine.getProjectCode()));
                detail.setOrganizationReferenceId(replaceFiller(accountingLine.getOrganizationReferenceId()));
                //detail.setAmount(KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode) ? accountingLine.getAmount().negated() : accountingLine.getAmount());
                detail.setAmount(getAccountingLineAmountForPaymentDetail(entry, accountingLine));
                detail.setExpenditureFinancialSystemOriginationCode(replaceFiller(entry.getFinancialSystemOriginationCode()));
                detail.setExpenditureFinancialDocumentNumber(entry.getDocumentNumber());
                detail.setExpenditureFinancialDocumentTypeCode(replaceFiller(entry.getFinancialDocumentTypeCode()));
                detail.setExpenditureFinancialDocumentPostedDate(entry.getTransactionDate());
                detail.setPurchaseOrderNumber(replaceFiller(entry.getReferenceFinancialDocumentNumber()));
                detail.setTransferPaymentIndicator(false);
                detail.refreshNonUpdateableReferences();
                appliedPayments.add(detail);
            }
        }

        return appliedPayments;
    }

    /**
     *
     * @param entry GL entry
     * @param accountingLine accounting line in the capital asset
     * @return accountingLineAmount
     */
    protected KualiDecimal getAccountingLineAmountForPaymentDetail(GeneralLedgerEntry entry, CapitalAssetAccountsGroupDetails accountingLine) {
        KualiDecimal accountLineAmount = accountingLine.getAmount();

        List<String> expenseObjectTypes = objectTypeService.getExpenseAndTransferObjectTypesForPayments();
        List<String> incomeObjectTypes = objectTypeService.getIncomeAndTransferObjectTypesForPayments();

        //we are dealing with error correction document so the from amount line should become positive.
        if (isDocumentAnErrorCorrection(entry)) {
            if (KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE.equals(accountingLine.getFinancialDocumentLineTypeCode())) {
                return accountLineAmount.negated();
            }

            return accountLineAmount;
        }

        if (expenseObjectTypes.contains(entry.getFinancialObjectTypeCode()) &&
                KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE.equals(accountingLine.getFinancialDocumentLineTypeCode()) &&
                KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode()) &&
                accountLineAmount.compareTo(KualiDecimal.ZERO) > 0) {
            return accountLineAmount.negated();
        }

        if (incomeObjectTypes.contains(entry.getFinancialObjectTypeCode()) && accountLineAmount.compareTo(KualiDecimal.ZERO) > 0) {
            return accountLineAmount.negated();
        }

        return accountLineAmount;
    }

    /**
     * determines if the document is an error correction document...
     * @param entry
     * @return true if the document is an error correction else false
     */
    protected boolean isDocumentAnErrorCorrection(GeneralLedgerEntry entry) {
        DocumentHeader docHeader = documentHeaderService.getDocumentHeaderById(entry.getDocumentNumber());
        FinancialSystemDocumentHeader fsDocumentHeader = (FinancialSystemDocumentHeader) docHeader;

        return fsDocumentHeader != null && StringUtils.isNotBlank(fsDocumentHeader.getFinancialDocumentInErrorNumber());
    }
    /**
     * updates the submit amount by the amount on the accounting line.  When submit amount equals
     * transaction ledger amount, the activity status code is marked as in route status.
     * @param matchingGLEntry
     * @param accountLineAmount
     */
    protected void updateTransactionSumbitGlEntryAmount(GeneralLedgerEntry matchingGLEntry, KualiDecimal accountLineAmount) {
        //update submitted amount on the gl entry and save the results.
        KualiDecimal submitTotalAmount = KualiDecimal.ZERO;
        if (ObjectUtils.isNotNull(matchingGLEntry.getTransactionLedgerSubmitAmount())) {
            submitTotalAmount = matchingGLEntry.getTransactionLedgerSubmitAmount();
        }

        matchingGLEntry.setTransactionLedgerSubmitAmount(submitTotalAmount.add(accountLineAmount.abs()));

        if (matchingGLEntry.getTransactionLedgerSubmitAmount().equals(matchingGLEntry.getTransactionLedgerEntryAmount())) {
            matchingGLEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.ENROUTE);
        }

        //save the updated gl entry in CAB
        businessObjectService.save(matchingGLEntry);
    }

    /**
     * NOTE: This method is not used anywhere in project.
     * Creates asset payment detail based on GL line. to CAB
     *
     * @param entry GeneralLedgerEntry
     * @param document Document
     * @return AssetPaymentDetail
     */
    protected AssetPaymentDetail createAssetPaymentDetail(GeneralLedgerEntry entry, Document document, int seqNo, Integer capitalAssetLineNumber) {
        // This is not added to constructor in CAMS to provide module isolation from CAMS
        AssetPaymentDetail detail = new AssetPaymentDetail();
        detail.setDocumentNumber(document.getDocumentNumber());
        detail.setSequenceNumber(seqNo);
        detail.setPostingYear(entry.getUniversityFiscalYear());
        detail.setPostingPeriodCode(entry.getUniversityFiscalPeriodCode());
        detail.setChartOfAccountsCode(entry.getChartOfAccountsCode());
        detail.setAccountNumber(replaceFiller(entry.getAccountNumber()));
        detail.setSubAccountNumber(replaceFiller(entry.getSubAccountNumber()));
        detail.setFinancialObjectCode(replaceFiller(entry.getFinancialObjectCode()));
        detail.setFinancialSubObjectCode(replaceFiller(entry.getFinancialSubObjectCode()));
        detail.setProjectCode(replaceFiller(entry.getProjectCode()));
        detail.setOrganizationReferenceId(replaceFiller(entry.getOrganizationReferenceId()));
        KualiDecimal capitalAssetAmount = getCapitalAssetAmount(entry, capitalAssetLineNumber);
        detail.setAmount(capitalAssetAmount);
        detail.setExpenditureFinancialSystemOriginationCode(replaceFiller(entry.getFinancialSystemOriginationCode()));
        detail.setExpenditureFinancialDocumentNumber(entry.getDocumentNumber());
        detail.setExpenditureFinancialDocumentTypeCode(replaceFiller(entry.getFinancialDocumentTypeCode()));
        detail.setExpenditureFinancialDocumentPostedDate(entry.getTransactionDate());
        detail.setPurchaseOrderNumber(replaceFiller(entry.getReferenceFinancialDocumentNumber()));
        detail.setTransferPaymentIndicator(false);
        return detail;
    }

    /**
     * retrieves the amount from the capital asset
     * @param entry
     * @param capitalAssetLineNumber
     * @return capital asset amount.
     */
    protected KualiDecimal getCapitalAssetAmount(GeneralLedgerEntry entry, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry.getDocumentNumber(), capitalAssetLineNumber);
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            return capitalAssetInformation.getCapitalAssetLineAmount();
        }

        return KualiDecimal.ZERO;
    }

    /**
     * If the value contains only the filler characters, then return blank
     *
     * @param val Value
     * @return blank if value if a filler
     */
    protected String replaceFiller(String val) {
        if (val == null) {
            return "";
        }
        char[] charArray = val.trim().toCharArray();
        for (char c : charArray) {
            if (c != '-') {
                return val;
            }
        }
        return "";
    }

    /**
     * Setup shell Capital Asset Information where it doesn't already exist (for example GL Entries
     * from enterprise feed or Vendor Credit Memo)
     *
     * @param entry
     */
    @Override
    public void setupCapitalAssetInformation(GeneralLedgerEntry entry) {
        List<CapitalAccountingLines> capitalAccountingLines;

        // get all related entries and create capital asset record for each
        Collection<GeneralLedgerEntry> glEntries = findAllGeneralLedgerEntry(entry.getDocumentNumber());
        int nextCapitalAssetLineNumber = 1;
        for (GeneralLedgerEntry glEntry: glEntries) {
            capitalAccountingLines = new ArrayList<CapitalAccountingLines>();
            createCapitalAccountingLine(capitalAccountingLines, glEntry, null);
            createNewCapitalAsset(capitalAccountingLines,entry.getDocumentNumber(),null,nextCapitalAssetLineNumber);
            nextCapitalAssetLineNumber++;
        }

    }

    private List<CapitalAccountingLines> createCapitalAccountingLine(List<CapitalAccountingLines> capitalAccountingLines, GeneralLedgerEntry entry, String distributionAmountCode) {
        Integer sequenceNumber = capitalAccountingLines.size() + 1;

        //capital object code so we need to build the capital accounting line...
        CapitalAccountingLines cal = addCapitalAccountingLine(capitalAccountingLines, entry);
        cal.setDistributionAmountCode(distributionAmountCode);
        capitalAccountingLines.add(cal);

        return capitalAccountingLines;
    }

    /**
     * convenience method to add a new capital accounting line to the collection of capital
     * accounting lines.
     *
     * @param capitalAccountingLines
     * @param entry
     * @return
     */
    protected CapitalAccountingLines addCapitalAccountingLine(List<CapitalAccountingLines> capitalAccountingLines, GeneralLedgerEntry entry) {
        CapitalAccountingLines cal = new CapitalAccountingLines();
        String capitalAssetLineType = KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode()) ? KFSConstants.SOURCE : KFSConstants.TARGET;
        cal.setLineType(capitalAssetLineType);
        cal.setSequenceNumber(entry.getTransactionLedgerEntrySequenceNumber());
        cal.setChartOfAccountsCode(entry.getChartOfAccountsCode());
        cal.setAccountNumber(entry.getAccountNumber());
        cal.setSubAccountNumber(entry.getSubAccountNumber());
        cal.setFinancialObjectCode(entry.getFinancialObjectCode());
        cal.setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        cal.setProjectCode(entry.getProjectCode());
        cal.setOrganizationReferenceId(entry.getOrganizationReferenceId());
        cal.setFinancialDocumentLineDescription(entry.getTransactionLedgerEntryDescription());
        cal.setAmount(entry.getAmount());
        cal.setAccountLinePercent(null);
        cal.setSelectLine(false);

        return cal;
    }

    /**
     * helper method to add accounting details for this new capital asset record
     *
     * @param capitalAccountingLines
     * @param currentCapitalAssetInformation
     * @param documentNumber
     * @param actionType
     * @param nextCapitalAssetLineNumnber
     */
    protected void createNewCapitalAsset(List<CapitalAccountingLines> capitalAccountingLines, String documentNumber, String actionType, Integer nextCapitalAssetLineNumber) {
        CapitalAssetInformation capitalAsset = new CapitalAssetInformation();
        capitalAsset.setCapitalAssetLineAmount(KualiDecimal.ZERO);
        capitalAsset.setDocumentNumber(documentNumber);
        capitalAsset.setCapitalAssetLineNumber(nextCapitalAssetLineNumber);
        capitalAsset.setCapitalAssetActionIndicator(actionType);
        capitalAsset.setCapitalAssetProcessedIndicator(false);

        KualiDecimal capitalAssetLineAmount = KualiDecimal.ZERO;
        //now setup the account line information associated with this capital asset
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            capitalAsset.setDistributionAmountCode(capitalAccountingLine.getDistributionAmountCode());
            createCapitalAssetAccountingLinesDetails(capitalAccountingLine, capitalAsset);
            capitalAssetLineAmount = capitalAssetLineAmount.add(capitalAccountingLine.getAmount());
        }

        capitalAsset.setCapitalAssetLineAmount(capitalAssetLineAmount);

        businessObjectService.save(capitalAsset);
    }

    /**
     *
     *
     * @param capitalAccountingLine
     * @param capitalAsset
     */
    protected void createCapitalAssetAccountingLinesDetails(CapitalAccountingLines capitalAccountingLine, CapitalAssetInformation capitalAsset) {
        //now setup the account line information associated with this capital asset
        CapitalAssetAccountsGroupDetails capitalAssetAccountLine = new CapitalAssetAccountsGroupDetails();
        capitalAssetAccountLine.setDocumentNumber(capitalAsset.getDocumentNumber());
        capitalAssetAccountLine.setChartOfAccountsCode(capitalAccountingLine.getChartOfAccountsCode());
        capitalAssetAccountLine.setAccountNumber(capitalAccountingLine.getAccountNumber());
        capitalAssetAccountLine.setSubAccountNumber(capitalAccountingLine.getSubAccountNumber());
        capitalAssetAccountLine.setFinancialDocumentLineTypeCode(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE);
        capitalAssetAccountLine.setCapitalAssetAccountLineNumber(getNextAccountingLineNumber(capitalAccountingLine, capitalAsset));
        capitalAssetAccountLine.setCapitalAssetLineNumber(capitalAsset.getCapitalAssetLineNumber());
        capitalAssetAccountLine.setFinancialObjectCode(capitalAccountingLine.getFinancialObjectCode());
        capitalAssetAccountLine.setFinancialSubObjectCode(capitalAccountingLine.getFinancialSubObjectCode());
        capitalAssetAccountLine.setProjectCode(capitalAccountingLine.getProjectCode());
        capitalAssetAccountLine.setOrganizationReferenceId(capitalAccountingLine.getOrganizationReferenceId());
        capitalAssetAccountLine.setSequenceNumber(capitalAccountingLine.getSequenceNumber());
        capitalAssetAccountLine.setAmount(capitalAccountingLine.getAmount());
        capitalAsset.getCapitalAssetAccountsGroupDetails().add(capitalAssetAccountLine);
    }

    /**
     * calculates the next accounting line number for accounts details for each capital asset.
     * Goes through the current records and gets the last accounting line number.
     *
     * @param capitalAsset
     * @return nextAccountingLineNumber
     */
    protected Integer getNextAccountingLineNumber(CapitalAccountingLines capitalAccountingLine, CapitalAssetInformation capitalAsset) {
        Integer nextAccountingLineNumber = 0;
        List<CapitalAssetAccountsGroupDetails> capitalAssetAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
        for (CapitalAssetAccountsGroupDetails capitalAssetAccountLine : capitalAssetAccountLines) {
            nextAccountingLineNumber = capitalAssetAccountLine.getCapitalAssetAccountLineNumber();
        }

        return ++nextAccountingLineNumber;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setAssetGlobalService(AssetGlobalService assetGlobalService) {
        this.assetGlobalService = assetGlobalService;
    }

    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }

    public void setDocumentHeaderService(DocumentHeaderService documentHeaderService) {
        this.documentHeaderService = documentHeaderService;
    }
}
