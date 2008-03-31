/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.cams.document;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.financial.document.DisbursementVoucherDocument;

public class AssetPaymentDocument extends AccountingDocumentBase implements Copyable, AmountTotaling {
    private static Logger LOG = Logger.getLogger(AssetPaymentDocument.class);
    
    private Long   capitalAssetNumber;
    private String representativeUniversalIdentifier;
    private String organizationOwnerChartOfAccountsCode;
    private String organizationOwnerAccountNumber;
    private String agencyNumber;
    private String campusCode;
    private String buildingCode;
    private Integer nextCapitalAssetPaymentLineNumber;

    private DocumentHeader  documentHeader;
    private Account         organizationOwnerAccount;
    private Chart           organizationOwnerChartOfAccounts;
    private Campus          campus;
    private Building        building;
    private List<AssetPaymentDetail> assetPaymentDetails;
    private Asset asset;
    //private AssetPaymentDetail assetPaymentDetail;
    
    public AssetPaymentDocument() {
        super();
        LOG.info("*******AssetPaymentDocument - Constructor");
        assetPaymentDetails = new ArrayList<AssetPaymentDetail>();

        /*documentHeader;
        organizationOwnerAccount;
        organizationOwnerChartOfAccounts;
        campus;
        building;*/
        
        asset  = new Asset();

    }
    
    /**
     * Determines if the given AccountingLine (as a GeneralLedgerPostable) is a credit or a debit, in terms of GLPE generation
     * @see org.kuali.kfs.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPostable)
     */

    
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable)  {  
        // TODO Auto-generated method stub
        return false;
    }
    
    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class<AssetPaymentDetail> getSourceAccountingLineClass() {
        LOG.info("*******getSourceAccountingLineClass");
        return AssetPaymentDetail.class;
    }

        
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    public String getRepresentativeUniversalIdentifier() {
        return representativeUniversalIdentifier;
    }

    public void setRepresentativeUniversalIdentifier(String representativeUniversalIdentifier) {
        this.representativeUniversalIdentifier = representativeUniversalIdentifier;
    }

    public String getOrganizationOwnerChartOfAccountsCode() {
        return organizationOwnerChartOfAccountsCode;
    }

    public void setOrganizationOwnerChartOfAccountsCode(String organizationOwnerChartOfAccountsCode) {
        this.organizationOwnerChartOfAccountsCode = organizationOwnerChartOfAccountsCode;
    }

    public String getOrganizationOwnerAccountNumber() {
        return organizationOwnerAccountNumber;
    }

    public void setOrganizationOwnerAccountNumber(String organizationOwnerAccountNumber) {
        this.organizationOwnerAccountNumber = organizationOwnerAccountNumber;
    }

    public String getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public Integer getNextCapitalAssetPaymentLineNumber() {
        return nextCapitalAssetPaymentLineNumber;
    }

    public void setNextCapitalAssetPaymentLineNumber(Integer nextCapitalAssetPaymentLineNumber) {
        this.nextCapitalAssetPaymentLineNumber = nextCapitalAssetPaymentLineNumber;
    }

    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    public Account getOrganizationOwnerAccount() {
        return organizationOwnerAccount;
    }

    public void setOrganizationOwnerAccount(Account organizationOwnerAccount) {
        this.organizationOwnerAccount = organizationOwnerAccount;
    }

    public Chart getOrganizationOwnerChartOfAccounts() {
        return organizationOwnerChartOfAccounts;
    }

    public void setOrganizationOwnerChartOfAccounts(Chart organizationOwnerChartOfAccounts) {
        this.organizationOwnerChartOfAccounts = organizationOwnerChartOfAccounts;
    }

    public Campus getCampus() {
        return campus;
    }

    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<AssetPaymentDetail> getAssetPaymentDetails() {
        return assetPaymentDetails;
    }

    public void setAssetPaymentDetails(List<AssetPaymentDetail> assetPaymentDetails) {
        this.assetPaymentDetails = assetPaymentDetails;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    
    
    /**
     * Still need implement.
     * 
     * @param financialDocument submitted financial document
     * @param sequenceHelper helper class which will allows us to increment a reference without using an Integer
     * @return true if there are no issues creating GLPE's
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     *
    @Override
    public void processGenerateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        //TODO Still need to implement.
    } */       
}
