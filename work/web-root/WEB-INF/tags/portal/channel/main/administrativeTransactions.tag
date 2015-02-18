<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Administrative Transactions" />
<div class="body">
	<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true' && ConfigProperties.contracts.grants.billing.enabled == 'true'}">
  		<strong>Accounts Receivable</strong></br>
  		<ul class="chan">	
			<li><portal:portalLink displayTitle="true" title="Generate Dunning Letters" url="${ConfigProperties.application.url}/arGenerateDunningLettersLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.GenerateDunningLettersLookupResult&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			<li><portal:portalLink displayTitle="true" title="Transmit Contracts & Grants Invoices" url="${ConfigProperties.application.url}/arTransmitContractsAndGrantsInvoices.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.TransmitContractsAndGrantsInvoicesLookupDataHolder&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  		</ul>
	</c:if>
	<c:if test="${ConfigProperties.module.capital.asset.enabled == 'true'}">
  		<strong>Capital Asset Builder</strong>
  		<ul class="chan">
	 		<li><portal:portalLink displayTitle="true" title="Capital Asset Builder AP Transactions" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableProcessingReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  	 		<li><portal:portalLink displayTitle="true" title="Capital Asset Builder GL Transactions" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
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
	<c:if test="${ConfigProperties.module.travel.enabled == 'true'}">	   
    <strong>Travel</strong>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Travel Agency Audit and Correction" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.tem.businessobject.AgencyStagingData&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    </ul>
    </c:if>
</div>
<channel:portalChannelBottom />
