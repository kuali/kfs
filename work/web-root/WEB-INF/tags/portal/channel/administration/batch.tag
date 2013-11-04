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

<channel:portalChannelTop channelTitle="Batch" />
<div class="body">
	<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
		<strong>Accounts Receivable</strong><br/>
	    <ul class="chan">
				<li><portal:portalLink displayTitle="true" title="Customer XML Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=customerLoadInputFileType" /></li>
				<li><portal:portalLink displayTitle="true" title="Customer CSV Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=customerLoadCSVInputFileType" /></li>
				<li><portal:portalLink displayTitle="true" title="Lockbox Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=lockboxInputFileType" /></li>
						  
		  </ul>
	</c:if>
	<strong>Financial Processing</strong><br/>
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Procurement Card Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=procurementCardInputFileType" /></li>
	</ul>
	<strong>General Ledger</strong><br/>
    <ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Collector Flat File Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorFlatFileInputFileType" /></li>
		<li><portal:portalLink displayTitle="true" title="Collector XML Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType" /></li>				
		<li><portal:portalLink displayTitle="true" title="Enterprise Feed Upload" url="batchUploadFileSet.do?methodToCall=start&batchUpload.batchInputTypeName=enterpriseFeederFileSetType" /></li>
		<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
			<li><portal:portalLink displayTitle="true" title="Labor Enterprise Feed Upload" url="laborBatchUploadFileSet.do?methodToCall=start&batchUpload.batchInputTypeName=laborEnterpriseFeederFileSetType" /></li>
		</c:if>
	</ul>
	<strong>Purchasing/Accounts Payable</strong><br/>
    <ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Electronic Invoice Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=electronicInvoiceInputFileType" /></li>
    	<c:if test="${!KualiConfigurationService.isProductionEnvironment}">
			<li><portal:portalLink displayTitle="true" title="Electronic Invoice Test File Generation" url="purapElectronicInvoiceTestFileGeneration.do" /></li>				
	    </c:if>
	</ul>
	<c:if test="${ConfigProperties.module.travel.enabled == 'true'}">
    <strong>Travel</strong><br/>
	<ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Credit Card Data Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=creditCardDataXmlInputFileType" /></li>
	    <li><portal:portalLink displayTitle="true" title="Per Diem XML Batch Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=perDiemXmlInputFileType" /></li>
	    <li><portal:portalLink displayTitle="true" title="Per Diem TXT Batch Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=perDiemTxtInputFileType" /></li>
	    <li><portal:portalLink displayTitle="true" title="Travel Agency Data Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=agencyDataXmlInputFileType" /></li>
    </ul>	
	</c:if>
	<strong>Vendor</strong><br/>
	<ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Vendor Exclusion Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=vendorExcludeInputFileType" /></li>
	</ul>
	<strong>Batch/Scheduled Jobs</strong><br/>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Batch Semaphore File Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=semaphoreInputFileTypeError" /></li>
    	<li><portal:portalLink displayTitle="true" title="Batch File" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchFile&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    	<c:if test="${ConfigProperties.use.quartz.scheduling == 'true'}">
			<li><portal:portalLink displayTitle="true" title="Schedule" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchJobStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&conversionFields=name:name,group:group" /></li>
		</c:if>
		<li><portal:portalLink displayTitle="true" title="Special Batch File Upload" url="batchFileUpload" /></li>
	</ul>
	<c:if test="${ConfigProperties.module.access.security.enabled == 'true'}">
	<strong>Security</strong><br/>
    <ul class="chan">
    	<li><portal:portalLink displayTitle="true" title="Access Security Simulation" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sec.businessobject.AccessSecuritySimulation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	</ul>
	</c:if>
</div>
<channel:portalChannelBottom />
                
