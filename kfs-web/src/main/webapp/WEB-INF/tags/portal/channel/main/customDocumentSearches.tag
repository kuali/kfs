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

<channel:portalChannelTop channelTitle="Custom Document Searches" />
<div class="body">
	<c:if test="${ConfigProperties.module.capital.asset.enabled == 'true'}">
	    <strong>Capital Asset Management</strong><br/>
	    <ul class="chan">
			<li><portal:portalLink displayTitle="true" title='Asset Maintenance' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=CAMM'/></li>
		</ul>
	</c:if>
	<c:if test="${ConfigProperties['module.contracts.and.grants.enabled'] == 'true'} && 
		${ConfigProperties['module.external.kuali.coeus.enabled'] == 'false'}">
		<strong>Contracts & Grants</strong><br/>
	   	 <ul class="chan">
			<li><portal:portalLink displayTitle="true" title='Proposals' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=PRPL'/></li>
		</ul>
	</c:if>
	<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
	 	<strong>Effort Certification</strong><br/>
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title='Effort Certification' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=ECD'/></li>
	    </ul>
	</c:if>    
	<strong>Financial Processing</strong><br/>
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Disbursement Vouchers" url="${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=DV" /></li>
	</ul>
	<c:if test="${ConfigProperties.module.purchasing.enabled == 'true'}">
		<strong>Purchasing/Accounts Payable</strong><br/>
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title='Payment Requests' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=PREQ'/></li>
	        <li><portal:portalLink displayTitle="true" title='Purchase Orders' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=PO'/></li>
	        <li><portal:portalLink displayTitle="true" title='Receiving' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=RCV'/></li>
	        <li><portal:portalLink displayTitle="true" title='Requisitions' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=REQS'/></li>
	        <li><portal:portalLink displayTitle="true" title='Vendor Credit Memos' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=CM'/></li>
	     </ul>
	</c:if>
	<c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}">
	    <c:if test="${ConfigProperties.module.travel.enabled == 'true'}">
		    <strong>Travel</strong><br/>
	        <ul class="chan">
	        	<li><portal:portalLink displayTitle="true" title="Corporate Card Application" url="${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=CCAP&methodToCall=search" /></li>
		        <li><portal:portalLink displayTitle="true" title="CTS Card Application" url="${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=CTAP&methodToCall=search" /></li>
	            <li><portal:portalLink displayTitle="true" title='Entertainment Reimbursement' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=ENT&methodToCall=search'/></li>
	            <li><portal:portalLink displayTitle="true" title='Moving and Relocation Reimbursement' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=RELO&methodToCall=search'/></li>            
	            <li><portal:portalLink displayTitle="true" title='Travel Authorization' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=TA&statusCodeForMultiboxSearching=OPEN&methodToCall=search'/></li>
	            <li><portal:portalLink displayTitle="true" title='Travel Reimbursement' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=TR&methodToCall=search'/></li>
	        </ul>
	    </c:if>
    </c:if>
    </div>
<channel:portalChannelBottom />
