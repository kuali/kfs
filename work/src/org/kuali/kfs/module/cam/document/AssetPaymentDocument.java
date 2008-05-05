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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.core.bo.Campus;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPaymentAccountingLineParser;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.labor.bo.LaborJournalVoucherAccountingLineParser;

public class AssetPaymentDocument extends AccountingDocumentBase implements Copyable, AmountTotaling {
    private static Logger LOG = Logger.getLogger(AssetPaymentDocument.class);

    private Long capitalAssetNumber;
    private String representativeUniversalIdentifier;
    private String organizationOwnerChartOfAccountsCode;
    private String organizationOwnerAccountNumber;
    private String agencyNumber;
    private String campusCode;
    private String buildingCode;
    private Integer nextCapitalAssetPaymentLineNumber;

    private Account organizationOwnerAccount;
    private Chart organizationOwnerChartOfAccounts;
    private Campus campus;
    private Building building;
    private List<AssetPaymentDetail> assetPaymentDetail;
    private Asset asset;

    public AssetPaymentDocument() {
        super();
        assetPaymentDetail = new ArrayList<AssetPaymentDetail>();
        asset = new Asset();
    }

    /**
     * Determines if the given AccountingLine (as a GeneralLedgerPostable) is a credit or a debit, in terms of GLPE generation
     * @see org.kuali.kfs.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return false;
    }


    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class<AssetPaymentDetail> getSourceAccountingLineClass() {
        return AssetPaymentDetail.class;
    }

    /**
     * 
     * @see org.kuali.kfs.document.AccountingDocumentBase#addSourceAccountingLine(org.kuali.kfs.bo.SourceAccountingLine)
     */
    @Override
    public void addSourceAccountingLine(SourceAccountingLine line) {
        Calendar calendar = new GregorianCalendar();
        java.sql.Date systemDate = new java.sql.Date(calendar.getTime().getTime());
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) line;
        assetPaymentDetail.setFinancialDocumentLineNumber(this.getNextSourceLineNumber());
        assetPaymentDetail.setPaymentApplicationDate(systemDate);

        line = (SourceAccountingLine) assetPaymentDetail;

        this.sourceAccountingLines.add(line);
        this.nextSourceLineNumber = new Integer(this.getNextSourceLineNumber().intValue() + 1);
        this.setNextCapitalAssetPaymentLineNumber(this.nextSourceLineNumber);
    }


    /**
     * 
     * @see org.kuali.kfs.document.GeneralLedgerPostingDocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        //Update asset payment table with the approved asset detail records.
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            SpringContext.getBean(AssetPaymentService.class).processApprovedAssetPayment(this);
        }
    }

    /**
     * 
     * @see org.kuali.kfs.document.AccountingDocumentBase#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        // This is an empty method in order to prevent kuali from generating a gl pending entry record.     
    }

/**
 * 
 * @see org.kuali.kfs.document.AccountingDocumentBase#getAccountingLineParser()
 */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new AssetPaymentAccountingLineParser();
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

    public List<AssetPaymentDetail> getAssetPaymentDetail() {
        return assetPaymentDetail;
    }

    public void setAssetPaymentDetail(List<AssetPaymentDetail> assetPaymentDetail) {
        this.assetPaymentDetail = assetPaymentDetail;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }
}