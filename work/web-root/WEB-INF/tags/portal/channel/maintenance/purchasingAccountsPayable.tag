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

<channel:portalChannelTop channelTitle="Purchasing/Accounts Payable" />
<div class="body">
	<ul class="chan">
		<li>
			<portal:portalLink displayTitle="true" title="Billing Address"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.BillingAddress&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Campus Parameter"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.CampusParameter&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
        <li>
            <portal:portalLink displayTitle="true" title="Capital Asset System Type"
                url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.CapitalAssetSystemType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
        </li>		
        <li>
            <portal:portalLink displayTitle="true" title="Capital Asset Transaction Type"
                url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.CapitalAssetTransactionType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
        </li>
        <li>
            <portal:portalLink displayTitle="true" title="Capital Asset Transaction Type Rule"
                url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.CapitalAssetTransactionTypeRule&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
        </li>
        <li>
            <portal:portalLink displayTitle="true" title="Carrier"
                url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.Carrier&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
        </li>
		<li>
			<portal:portalLink displayTitle="true" title="Credit Memo Status"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.CreditMemoStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Delivery Required Date Reason"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.DeliveryRequiredDateReason&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Funding Source"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.FundingSource&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Item Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.ItemType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Method of PO Transmission"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderTransmissionMethod&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>		
		<li>
			<portal:portalLink displayTitle="true"
				title="Negative Payment Request Approval Limit"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.NegativePaymentRequestApprovalLimit&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Organization Parameter"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.OrganizationParameter&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Payment Request Auto Approve Exclusions"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.AutoApproveExclude&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Payment Request Status"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PaymentRequestStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Purchase Order Contract Language"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderContractLanguage&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Purchase Order Quote Status"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderQuoteStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Purchase Order Quote Language"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderQuoteLanguage&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
        <li>
        	<portal:portalLink displayTitle="true"
        		title="Purchase Order Quote Lists"
        		url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderQuoteList&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
        <li>
			<portal:portalLink displayTitle="true" title="Purchase Order Status"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Purchase Order Vendor Choice"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderVendorChoice&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Receiving Address"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.ReceivingAddress&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Recurring Payment Frequency"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.RecurringPaymentFrequency&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Recurring Payment Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.RecurringPaymentType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Requisition Source"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.RequisitionSource&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Requisition Status"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.RequisitionStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
        <li>
            <portal:portalLink displayTitle="true" title="Restricted Material"
                url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.RestrictedMaterial&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
        </li>		
		<li>
			<portal:portalLink displayTitle="true" title="Unit Of Measure"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.UnitOfMeasure&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Vendor Stipulation"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.VendorStipulation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
