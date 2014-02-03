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
		<li><portal:portalLink displayTitle="true" title="Collection Activity Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CollectionActivityType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Collection Status"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CollectionStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Cost Category"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsCategories&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Customer"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.Customer&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Customer Address Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAddressType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Customer Invoice Item Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Customer Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Dunning Campaign"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.DunningCampaign&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Dunning Letter Templates"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Final Disposition"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.FinalDisposition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
					title="Invoice Indicator"
					url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.InvoiceIndicator&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Invoice Recurrence"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Invoice Template"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.InvoiceTemplate&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Milestone Schedule"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.MilestoneSchedule&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Organization Accounting Default"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Organization Options"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.OrganizationOptions&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Payment Medium"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.PaymentMedium&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true" title="Referral Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ReferralType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="Suspension Categories"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.SuspensionCategory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li><portal:portalLink displayTitle="true"
				title="System Information"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.SystemInformation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
