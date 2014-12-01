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

<channel:portalChannelTop channelTitle="Accounts Receivable" />
<div class="body">
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Customer" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.Customer&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Customer Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Customer Address Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAddressType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Customer Invoice Item Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>        
		<li><portal:portalLink displayTitle="true" title="Invoice Recurrence" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>        
        <li><portal:portalLink displayTitle="true" title="Organization Options" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.OrganizationOptions&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Organization Accounting Default" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Payment Medium" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.PaymentMedium&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="System Information" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.SystemInformation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>   
   	</ul>
</div>
<channel:portalChannelBottom />
