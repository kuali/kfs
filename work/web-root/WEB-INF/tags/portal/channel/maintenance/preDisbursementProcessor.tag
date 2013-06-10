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

<channel:portalChannelTop channelTitle="Pre-Disbursement Processor" />
<div class="body">
    <ul class="chan">
	  <li><portal:portalLink displayTitle="true" title="Accounting Change Code" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.AccountingChangeCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="ACH Bank" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.ACHBank&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
      <li><portal:portalLink displayTitle="true" title="ACH Transaction Code" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.ACHTransactionCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="ACH Transaction Type" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.ACHTransactionType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
      <li><portal:portalLink displayTitle="true" title="Customer Profile" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.CustomerProfile&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="Disbursement Number Range" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.DisbursementNumberRange&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	  <li><portal:portalLink displayTitle="true" title="Disbursement Type" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.DisbursementType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="Format Checks/ACH" url="pdp/format.do?methodToCall=start"/></li>
      <li><portal:portalLink displayTitle="true" title="Format Reset" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.FormatProcess&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
      <li><portal:portalLink displayTitle="true" title="Format Summary" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PaymentProcess&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
	  <li><portal:portalLink displayTitle="true" title="Payee ACH Account" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PayeeACHAccount&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
	  <li><portal:portalLink displayTitle="true" title="Payee Type" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PayeeType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="Payment Bank History" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.BankChangeHistory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
	  <li><portal:portalLink displayTitle="true" title="Payment Change" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PaymentChangeCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
	  <li><portal:portalLink displayTitle="true" title="Payment File Batch Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=paymentInputFileType" /></li>
	  <li><portal:portalLink displayTitle="true" title="Payment Spreadsheet Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=researchParticipantInboundServiceInputType" /></li>	  
	  <li><portal:portalLink displayTitle="true" title="Payment Type" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PaymentType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>	  
	  <li><portal:portalLink displayTitle="true" title="Payment Status" url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PaymentStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="Search for Batch" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.Batch&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
      <li><portal:portalLink displayTitle="true" title="Search for Payment" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PaymentDetail&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
    </ul>
</div>
<channel:portalChannelBottom />
