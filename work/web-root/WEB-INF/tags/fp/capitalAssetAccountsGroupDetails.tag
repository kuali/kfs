<%--
 Copyright 2005-2009 The Kuali Foundation
 
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

<%@ tag description="render the given field in the capital asset info object"%>

<%@ attribute name="capitalAssetAccountsGroupDetails" required="true" type="java.lang.Object"
	description="The capital asset info object containing the accounting lines being displayed"%>
<%@ attribute name="capitalAssetAccountsGroupDetailsName" required="true" description="The name of the capital asset accounts group object"%>	
<%@ attribute name="readOnly" required="false" description="Whether the capital asset accounting lines should be read only" %>	
<%@ attribute name="capitalAssetAccountsGroupDetailsIndex" required="true" description="Gives the capital asset accounts group index" %>	
	
<c:set var="attributes" value="${DataDictionary.CapitalAssetAccountsGroupDetails.attributes}" />		

<c:if test="${not empty capitalAssetAccountsGroupDetails}">
	<table datatable style="border-top: 1px solid rgb(153, 153, 153); width: 78%;" cellpadding="0" cellspacing="0" summary="Asset for Accounting Lines">
		<tr>
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.capitalAssetAccountLineNumber}"
				useShortLabel="true" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.sequenceNumber}"
				useShortLabel="true" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.financialDocumentLineTypeCode}"
				useShortLabel="true" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.chartOfAccountsCode}"
				useShortLabel="true"
				hideRequiredAsterisk="true" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.accountNumber}"
				useShortLabel="true" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.subAccountNumber}"
				useShortLabel="true" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.financialObjectCode}"
				useShortLabel="true" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.financialSubObjectCode}"
				useShortLabel="true" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.projectCode}"
				useShortLabel="true" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.organizationReferenceId}"
				useShortLabel="true" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${attributes.amount}"
				useShortLabel="true" />
		</tr>
		<c:forEach items="${capitalAssetAccountsGroupDetails}" var="assetAccountsGroupLine" varStatus="status">
			<tr>
				<td class="datacell center">
					<div align="center" valign="middle">
						<kul:htmlControlAttribute attributeEntry="${attributes.capitalAssetAccountLineNumber}" property="${capitalAssetAccountsGroupDetailsName}[${status.index}].capitalAssetAccountLineNumber" readOnly="true"/>					
					</div>		            
				</td>
				<td class="datacell center">
					<div align="center" valign="middle">
						<kul:htmlControlAttribute attributeEntry="${attributes.sequenceNumber}" property="${capitalAssetAccountsGroupDetailsName}[${status.index}].sequenceNumber" readOnly="true"/>					
					</div>		            
				</td>
				<td class="datacell center"><div>
					<c:set var="lineType" value="${assetAccountsGroupLine.financialDocumentLineTypeCode}" />
					<c:if test="${lineType eq KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE}">
						<c:out value="${KFSConstants.SOURCE}" />
					</c:if>
					<c:if test="${lineType eq KFSConstants.TARGET_ACCT_LINE_TYPE_CODE}">
						<c:out value="${KFSConstants.TARGET}" />
					</c:if>
					</div></td>
					<td class="datacell center"><kul:htmlControlAttribute attributeEntry="${attributes.chartOfAccountsCode}" property="${capitalAssetAccountsGroupDetailsName}[${status.index}].chartOfAccountsCode" readOnly="true"/></td>
					<td class="datacell center"><kul:htmlControlAttribute attributeEntry="${attributes.accountNumber}" property="${capitalAssetAccountsGroupDetailsName}[${status.index}].accountNumber" readOnly="true"/></td>
					<td class="datacell center"><kul:htmlControlAttribute attributeEntry="${attributes.subAccountNumber}" property="${capitalAssetAccountsGroupDetailsName}[${status.index}].subAccountNumber" readOnly="true"/></td>
					<td class="datacell center"><kul:htmlControlAttribute attributeEntry="${attributes.financialObjectCode}" property="${capitalAssetAccountsGroupDetailsName}[${status.index}].financialObjectCode" readOnly="true"/></td>
					<td class="datacell center"><kul:htmlControlAttribute attributeEntry="${attributes.financialSubObjectCode}" property="${capitalAssetAccountsGroupDetailsName}[${status.index}].financialSubObjectCode" readOnly="true"/></td>
					<td class="datacell center"><kul:htmlControlAttribute attributeEntry="${attributes.projectCode}" property="${capitalAssetAccountsGroupDetailsName}[${status.index}].projectCode" readOnly="true"/></td>
					<td class="datacell center"><kul:htmlControlAttribute attributeEntry="${attributes.organizationReferenceId}" property="${capitalAssetAccountsGroupDetailsName}[${status.index}].organizationReferenceId" readOnly="true"/></td>
					<td class="datacell center">
						<div align="right" valign="middle">
							<kul:htmlControlAttribute attributeEntry="${attributes.amount}" property="${capitalAssetAccountsGroupDetailsName}[${status.index}].amount" readOnly="true"/>
						</div>							
					</td>
			</tr>
		</c:forEach>
	</table>
</c:if>	