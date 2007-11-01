<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Financial Processing" />
<div class="body">

    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Bank" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.Bank&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Bank Account" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.BankAccount&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
 		<li><portal:portalLink displayTitle="true" title="Credit Card Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.CreditCardType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Credit Card Vendor" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.CreditCardVendor&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Documentation Location" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.DisbursementVoucherDocumentationLocation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Ownership Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.OwnershipTypeCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>        
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Payment Reason" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.PaymentReasonCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Disbursement Voucher Tax Control" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TaxControlCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Tax Income Class" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TaxIncomeClassCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Travel Expense Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TravelExpenseTypeCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Travel Mileage Rate" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TravelMileageRate&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Travel Per Diem" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TravelPerDiem&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Wire Charge" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.WireCharge&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>        
		<li><portal:portalLink displayTitle="true" title="Fiscal Year Function Control" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.FiscalYearFunctionControl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>				
		<li><portal:portalLink displayTitle="true" title="Function Control Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.FunctionControlCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>				
        <li><portal:portalLink displayTitle="true" title="Non-Resident Alien Tax Percent" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.NonResidentAlienTaxPercent&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Service Billing Control" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.ServiceBillingControl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    </ul>
</div>
<channel:portalChannelBottom />
                