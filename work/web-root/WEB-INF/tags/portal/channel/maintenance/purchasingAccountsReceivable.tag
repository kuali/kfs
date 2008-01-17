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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Accounts Receivable" />
<div class="body">
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Customer" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.ar.bo.Customer&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Customer Address Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.ar.bo.CustomerAddressType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Organization Options" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.ar.bo.OrganizationOptions&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Organization Accounting Defaults" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.ar.bo.OrganizationAccountingDefault&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Invoice Item Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.ar.bo.CustomerInvoiceItemCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Payment Medium" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.ar.bo.PaymentMedium&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="System Information" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.ar.bo.SystemInformation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    </ul>
</div>
<channel:portalChannelBottom />