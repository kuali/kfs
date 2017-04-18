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

<channel:portalChannelTop channelTitle="Pre-Disbursement Processor" />
<div class="body">
    <ul class="chan">
	  <li><portal:portalLink displayTitle="true" title="Accounting Change Code" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.AccountingChangeCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="ACH Bank" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.ACHBank&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
      <li><portal:portalLink displayTitle="true" title="ACH Transaction Code" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.ACHTransactionCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="ACH Transaction Type" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.ACHTransactionType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
      <li><portal:portalLink displayTitle="true" title="Customer Profile" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.CustomerProfile&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="Disbursement Number Range" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.DisbursementNumberRange&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	  <li><portal:portalLink displayTitle="true" title="Disbursement Type" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.DisbursementType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="Format Checks/ACH" url="${ConfigProperties.application.url}/pdp/format.do?methodToCall=start"/></li>
      <li><portal:portalLink displayTitle="true" title="Format Reset" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.FormatProcess&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
      <li><portal:portalLink displayTitle="true" title="Format Summary" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PaymentProcess&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
	  <li><portal:portalLink displayTitle="true" title="Payee ACH Account" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.pdp.businessobject.PayeeACHAccount&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
	  <li><portal:portalLink displayTitle="true" title="Payee Type" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PayeeType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="Payment Bank History" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.BankChangeHistory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
	  <li><portal:portalLink displayTitle="true" title="Payment Change" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PaymentChangeCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
	  <c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}">
		  <li><portal:portalLink displayTitle="true" title="Payment File Batch Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=paymentInputFileType" /></li>
		  <li><portal:portalLink displayTitle="true" title="Payment Spreadsheet Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=researchParticipantInboundServiceInputType" /></li>	  
	  </c:if>
	  <li><portal:portalLink displayTitle="true" title="Payment Type" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PaymentType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>	  
	  <li><portal:portalLink displayTitle="true" title="Payment Status" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PaymentStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
      <li><portal:portalLink displayTitle="true" title="Search for Batch" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.Batch&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
      <li><portal:portalLink displayTitle="true" title="Search for Payment" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.pdp.businessobject.PaymentDetail&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
    </ul>
</div>
<channel:portalChannelBottom />
