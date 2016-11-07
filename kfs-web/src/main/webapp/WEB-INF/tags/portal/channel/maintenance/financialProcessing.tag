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

<channel:portalChannelTop channelTitle="Financial Processing" />
<div class="body">

    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Cash Drawer" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.CashDrawer&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
 		<li><portal:portalLink displayTitle="true" title="Credit Card Type" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.CreditCardType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Credit Card Vendor" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.CreditCardVendor&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Payment Reason" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.PaymentReasonCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Tax Income Class" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.TaxIncomeClassCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Travel Expense Type" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.TravelExpenseTypeCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Travel Mileage Rate" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.TravelMileageRate&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Travel Per Diem" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.TravelPerDiem&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Fiscal Year Function Control" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.FiscalYearFunctionControl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>				
		<li><portal:portalLink displayTitle="true" title="Function Control Code" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.FunctionControlCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>				
        <li><portal:portalLink displayTitle="true" title="Non-Resident Alien Tax Percent" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Payment Method" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.fp.businessobject.PaymentMethod&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li> 
		<li><portal:portalLink displayTitle="true" title="Procurement Card Defaults" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.fp.businessobject.ProcurementCardDefault&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
   </ul>
</div>
<channel:portalChannelBottom />
                
