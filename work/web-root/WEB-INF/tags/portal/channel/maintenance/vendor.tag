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

<channel:portalChannelTop channelTitle="Vendor" />
<div class="body">
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Address Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.AddressType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Commodity Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.CommodityCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Contact Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.ContactType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Contract Manager" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.ContractManager&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Cost Source" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.PurchaseOrderCostSource&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Ownership Type Category" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.OwnershipCategory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Ownership Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.OwnershipType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Payment Terms Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.PaymentTermType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Phone Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.PhoneType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Shipping Payment Terms" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.ShippingPaymentTerms&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Shipping Special Conditions" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.ShippingSpecialCondition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Shipping Title" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.ShippingTitle&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Supplier Diversity" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.SupplierDiversity&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Vendor Inactive Reason" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.VendorInactiveReason&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>     
        <li><portal:portalLink displayTitle="true" title="Vendor Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.VendorType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>     
    </ul>
</div>
<channel:portalChannelBottom />

