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

<channel:portalChannelTop channelTitle="Batch" />
<div class="body">
	<c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}">
		<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
			<strong>Accounts Receivable</strong><br/>
		    <ul class="chan">
					<li><portal:portalLink displayTitle="true" title="Customer XML Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=customerLoadInputFileType" /></li>
					<li><portal:portalLink displayTitle="true" title="Customer CSV Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=customerLoadCSVInputFileType" /></li>
					<li><portal:portalLink displayTitle="true" title="Lockbox Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=lockboxInputFileType" /></li>
							  
			  </ul>
		</c:if>
		<strong>Financial Processing</strong><br/>
	    <ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Procurement Card Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=procurementCardInputFileType" /></li>
		</ul>
		<strong>General Ledger</strong><br/>
	    <ul class="chan">
		    <li><portal:portalLink displayTitle="true" title="Collector Flat File Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorFlatFileInputFileType" /></li>
			<li><portal:portalLink displayTitle="true" title="Collector XML Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType" /></li>				
			<li><portal:portalLink displayTitle="true" title="Enterprise Feed Upload" url="${ConfigProperties.application.url}/batchUploadFileSet.do?methodToCall=start&batchUpload.batchInputTypeName=enterpriseFeederFileSetType" /></li>
			<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
				<li><portal:portalLink displayTitle="true" title="Labor Enterprise Feed Upload" url="${ConfigProperties.application.url}/laborBatchUploadFileSet.do?methodToCall=start&batchUpload.batchInputTypeName=laborEnterpriseFeederFileSetType" /></li>
			</c:if>
		</ul>
		<strong>Purchasing/Accounts Payable</strong><br/>
	    <ul class="chan">
		    <li><portal:portalLink displayTitle="true" title="Electronic Invoice Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=electronicInvoiceInputFileType" /></li>
	    	<c:if test="${!KualiConfigurationService.isProductionEnvironment}">
				<li><portal:portalLink displayTitle="true" title="Electronic Invoice Test File Generation" url="${ConfigProperties.application.url}/purapElectronicInvoiceTestFileGeneration.do" /></li>				
		    </c:if>
		</ul>
		<c:if test="${ConfigProperties.module.travel.enabled == 'true'}">
	    <strong>Travel</strong><br/>
		<ul class="chan">
		    <li><portal:portalLink displayTitle="true" title="Credit Card Data Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=creditCardDataXmlInputFileType" /></li>
		    <li><portal:portalLink displayTitle="true" title="Per Diem XML Batch Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=perDiemXmlInputFileType" /></li>
		    <li><portal:portalLink displayTitle="true" title="Per Diem TXT Batch Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=perDiemTxtInputFileType" /></li>
		    <li><portal:portalLink displayTitle="true" title="Travel Agency Data Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=agencyDataXmlInputFileType" /></li>
	    </ul>	
		</c:if>
		<strong>Vendor</strong><br/>
		<ul class="chan">
		    <li><portal:portalLink displayTitle="true" title="Vendor Exclusion Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=vendorExcludeInputFileType" /></li>
		</ul>
	</c:if>
	<strong>Batch/Scheduled Jobs</strong><br/>
    <ul class="chan">
    	<c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}">
        	<li><portal:portalLink displayTitle="true" title="Batch Semaphore File Upload" url="${ConfigProperties.application.url}/batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=semaphoreInputFileTypeError" /></li>
    	</c:if>
    	<li><portal:portalLink displayTitle="true" title="Batch File" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchFile&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    	<c:if test="${ConfigProperties.use.quartz.scheduling == 'true'}">
			<li><portal:portalLink displayTitle="true" title="Schedule" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchJobStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&conversionFields=name:name,group:group" /></li>
		</c:if>
		<c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}">
			<li><portal:portalLink displayTitle="true" title="Special Batch File Upload" url="${ConfigProperties.application.url}/batchFileUpload" /></li>
		</c:if>
	</ul>
	<c:if test="${ConfigProperties.module.access.security.enabled == 'true'}">
	<strong>Security</strong><br/>
    <ul class="chan">
    	<li><portal:portalLink displayTitle="true" title="Access Security Simulation" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sec.businessobject.AccessSecuritySimulation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	</ul>
	</c:if>
</div>
<channel:portalChannelBottom />
                
