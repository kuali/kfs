<%--
 Copyright 2007 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Administrative Transactions" />
<div class="body">
	<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
  		<strong>Accounts Receivable</strong></br>
  		<ul class="chan">	
			<li><portal:portalLink displayTitle="true" title="Generate Dunning Letters" url="${ConfigProperties.application.url}/arGenerateDunningLettersLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.GenerateDunningLettersLookupResult&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			<li><portal:portalLink displayTitle="true" title="Transmit Contracts & Grants Invoices" url="${ConfigProperties.application.url}/arTransmitContractsAndGrantsInvoices.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.TransmitContractsAndGrantsInvoicesLookupDataHolder&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  		</ul>
	</c:if>
	<c:if test="${ConfigProperties.module.capital.asset.enabled == 'true'}">
  		<strong>Capital Asset Builder</strong>
  		<ul class="chan">
	 		<li><portal:portalLink displayTitle="true" title="Capital Asset Builder AP Transactions" url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableProcessingReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  	 		<li><portal:portalLink displayTitle="true" title="Capital Asset Builder GL Transactions" url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  		</ul>

  		<strong>Capital Asset Management</strong>
  		<ul class="chan">
     		<li><portal:portalLink displayTitle="true" title="Asset Manual Payment" url="${ConfigProperties.application.url}/camsAssetPayment.do?methodToCall=docHandler&command=initiate&docTypeName=MPAY" /></li>
  	 		<li><portal:portalLink displayTitle="true" title="Barcode Inventory Process" url="${ConfigProperties.application.url}/uploadBarcodeInventoryFile.do?methodToCall=start&batchUpload.batchInputTypeName=assetBarcodeInventoryInputFileType" /></li>			 
  		</ul>
		</c:if>
	<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
  		<strong>Effort Certification</strong>
  		<ul class="chan">		
	 		<li><portal:portalLink displayTitle="true" title="Effort Certification Recreate" url="${ConfigProperties.application.url}/effortCertificationRecreate.do?methodToCall=docHandler&command=initiate&docTypeName=ECD" /></li>
  		</ul>
	</c:if>
	<strong>Financial Processing</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Cash Management" url="${ConfigProperties.application.url}/financialCashManagement.do?methodToCall=docHandler&command=initiate&docTypeName=CMD" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Correction Process" url="${ConfigProperties.application.url}/generalLedgerCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=GLCP" /></li>									
		<li><portal:portalLink displayTitle="true" title="Journal Voucher" url="${ConfigProperties.application.url}/financialJournalVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=JV" /></li>
		<li><portal:portalLink displayTitle="true" title="Non-Check Disbursement" url="${ConfigProperties.application.url}/financialNonCheckDisbursement.do?methodToCall=docHandler&command=initiate&docTypeName=ND" /></li>
		<li><portal:portalLink displayTitle="true" title="Service Billing" url="${ConfigProperties.application.url}/financialServiceBilling.do?methodToCall=docHandler&command=initiate&docTypeName=SB" /></li>
    </ul>
	<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">    
    	<strong>Labor Distribution</strong><br />
    	<ul class="chan">
    		<li>
				<portal:portalLink displayTitle="true" title="Labor Journal Voucher" url="${ConfigProperties.application.url}/laborJournalVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=LLJV" />
			</li>
			<li>
				<portal:portalLink displayTitle="true" title="Labor Ledger Correction Process" url="${ConfigProperties.application.url}/laborLedgerCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=LLCP" />
			</li>
    	</ul>    
	</c:if>    
	<strong>System</strong>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Electronic Payment Claim" url="${ConfigProperties.application.url}/electronicFundTransfer.do?methodToCall=start" /></li>
	</ul>
	<c:if test="${ConfigProperties.module.endowment.enabled == 'true'}">	   
	    <strong>Endowment</strong><br />
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title="Corporate Reorganization" url="${ConfigProperties.application.url}/endowCorporateReorganizationDocument.do?methodToCall=docHandler&command=initiate&docTypeName=ECR" /></li>
	        <li><portal:portalLink displayTitle="true" title="Corpus Adjustment" url="${ConfigProperties.application.url}/endowCorpusAdjustmentDocument.do?methodToCall=docHandler&command=initiate&docTypeName=ECA" /></li>
	        <li><portal:portalLink displayTitle="true" title="Holding Adjustment" url="${ConfigProperties.application.url}/endowHoldingAdjustmentDocument.do?methodToCall=docHandler&command=initiate&docTypeName=EHA" /></li>         
	        <li><portal:portalLink displayTitle="true" title="Holding History Value Adjustment" url="${ConfigProperties.application.url}/endowHoldingHistoryValueAdjustmentDocument.do?methodToCall=docHandler&command=initiate&docTypeName=EHVA" /></li>
	        <li><portal:portalLink displayTitle="true" title="Holding Tax Lot Rebalance" url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	        <li><portal:portalLink displayTitle="true" title="Unit/Share Adjustment" url="${ConfigProperties.application.url}/endowEndowmentUnitShareAdjustmentDocument.do?methodToCall=docHandler&command=initiate&docTypeName=EUSA" /></li> 
	    </ul>
    </c:if>
	<c:if test="${ConfigProperties.module.travel.enabled == 'true'}">	   
    <strong>Travel</strong>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Travel Agency Audit and Correction" url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.tem.businessobject.AgencyStagingData&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    </ul>
    </c:if>
</div>
<channel:portalChannelBottom />
