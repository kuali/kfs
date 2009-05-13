<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Administrative Transactions" />
<div class="body">
  <strong>Capital Asset Builder</strong>
  <ul class="chan">
	 <li><portal:portalLink displayTitle="true" title="Capital Asset Builder AP Transactions" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableProcessingReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  	 <li><portal:portalLink displayTitle="true" title="Capital Asset Builder GL Transactions" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  </ul>

  <strong>Capital Asset Management</strong>
  <ul class="chan">
     <li><portal:portalLink displayTitle="true" title="Asset Manual Payment" url="camsAssetPayment.do?methodToCall=docHandler&command=initiate&docTypeName=MPAY" /></li>
  	 <li><portal:portalLink displayTitle="true" title="Barcode Inventory Process" url="uploadBarcodeInventoryFile.do?methodToCall=start&batchUpload.batchInputTypeName=assetBarcodeInventoryInputFileType" /></li>			 
  </ul>

  <strong>Effort Certification</strong>
  <ul class="chan">		
	 <li><portal:portalLink displayTitle="true" title="Effort Certification Recreate" url="effortCertificationRecreate.do?methodToCall=docHandler&command=initiate&docTypeName=ECD" /></li>
  </ul>
	<strong>Financial Processing</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Cash Management" url="financialCashManagement.do?methodToCall=docHandler&command=initiate&docTypeName=CMD" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Correction Process" url="generalLedgerCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=GLCP" /></li>									
		<li><portal:portalLink displayTitle="true" title="Journal Voucher" url="financialJournalVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=JV" /></li>
		<li><portal:portalLink displayTitle="true" title="Non-Check Disbursement" url="financialNonCheckDisbursement.do?methodToCall=docHandler&command=initiate&docTypeName=ND" /></li>
		<li><portal:portalLink displayTitle="true" title="Service Billing" url="financialServiceBilling.do?methodToCall=docHandler&command=initiate&docTypeName=SB" /></li>
    </ul>
    <strong>Labor Distribution</strong><br />
    <ul class="chan">
    	<li>
			<portal:portalLink displayTitle="true" title="Labor Journal Voucher"
				url="laborJournalVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=LLJV" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Labor Ledger Correction Process"
				url="laborLedgerCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=LLCP" />
		</li>
    </ul>
	<strong>System</strong>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Electronic Fund Transfer" url="electronicFundTransfer.do?methodToCall=start" /></li>
	</ul>
</div>
<channel:portalChannelBottom />
