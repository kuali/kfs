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

<channel:portalChannelTop channelTitle="Reports" />
<div class="body">
	<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
		<strong>Accounts Receivable</strong></br>
	    <ul class="chan">	
			<li><portal:portalLink displayTitle="true" title="Billing Statement" url="arCustomerStatement.do?methodToCall=start" /></li>
	   		<li><portal:portalLink displayTitle="true" title="Customer Aging Report" url="arCustomerAgingReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail" /></li> 		
	   		<li><portal:portalLink displayTitle="true" title="Customer Invoice" url="arCustomerInvoice.do?methodToCall=start" /></li>
		</ul>
	</c:if>
	<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
		<strong>Effort Certification</strong></br>
	    <ul class="chan">	
			<li>
				<portal:portalLink displayTitle="true" title="Duplicate Certifications Report"
					url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.DuplicateCertificationsReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			
			<li>
				<portal:portalLink displayTitle="true" title="Effort Certification Extract Build"
					url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			
			<li>
				<portal:portalLink displayTitle="true" title="Outstanding Certifications by Chart/Org/Report"
					url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.OutstandingCertificationsByOrganization&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			
			<li>
				<portal:portalLink displayTitle="true" title="Outstanding Certifications By Report"
					url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.OutstandingCertificationsByReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</ul>
	</c:if>
	<c:if test="${ConfigProperties.module.endowment.enabled == 'true'}">
		<strong>Endowments</strong></br>
	    <ul class="chan">	
			<li><portal:portalLink displayTitle="true" title="Asset Statement" url="reportEndowAssetStatement.do?methodToCall=start"/></li>
			<li><portal:portalLink displayTitle="true" title="Transaction Statement" url="reportEndowTransactionStatement.do?methodToCall=start"/></li>
			<li><portal:portalLink displayTitle="true" title="Transaction Summary" url="reportEndowTransactionSummary.do?methodToCall=start"/></li>		
			<li><portal:portalLink displayTitle="true" title="Trial Balance" url="reportEndowTrialBalance.do?methodToCall=start"/></li>
		</ul>
	</c:if>
	<strong>General Ledger</strong></br>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Trial Balance" url="glTrialBalance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.TrialBalanceReport&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	</ul>
</div>
<channel:portalChannelBottom />
						
						
						
						
						
						
