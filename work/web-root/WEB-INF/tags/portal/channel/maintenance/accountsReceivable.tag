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

<channel:portalChannelTop channelTitle="Accounts Receivable" />
<div class="body">
	<ul class="chan">
		<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
			<li><portal:portalLink displayTitle="true" title="Collection Activity Type"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CollectionActivityType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			<li><portal:portalLink displayTitle="true" title="Collection Status"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CollectionStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			<li><portal:portalLink displayTitle="true" title="Cost Category"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CostCategory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</c:if>
		<li><portal:portalLink displayTitle="true" title="Customer"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.Customer&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Customer Address Type"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAddressType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Customer Invoice Item Code"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Customer Type"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
			<li><portal:portalLink displayTitle="true" title="Dunning Campaign"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.DunningCampaign&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			<li><portal:portalLink displayTitle="true" title="Dunning Letter Template"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			<li><portal:portalLink displayTitle="true" title="Final Disposition"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.FinalDisposition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</c:if>
		<li><portal:portalLink displayTitle="true"
				title="Invoice Recurrence"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
			<li><portal:portalLink displayTitle="true"
					title="Invoice Template"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.InvoiceTemplate&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			<li><portal:portalLink displayTitle="true"
					title="Method of Invoice Transmission"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.InvoiceTransmissionMethod&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			<li><portal:portalLink displayTitle="true"
					title="Milestone Schedule"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.MilestoneSchedule&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</c:if>
		<li><portal:portalLink displayTitle="true"
				title="Organization Accounting Default"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Organization Options"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.OrganizationOptions&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Payment Medium"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.PaymentMedium&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
			<li><portal:portalLink displayTitle="true"
					title="Predetermined Billing Schedule"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			<li><portal:portalLink displayTitle="true" title="Referral Type"
					url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ReferralType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</c:if>
		<li><portal:portalLink displayTitle="true"
				title="System Information"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.SystemInformation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
