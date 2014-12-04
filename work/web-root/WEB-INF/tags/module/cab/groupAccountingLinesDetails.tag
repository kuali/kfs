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
<%@ attribute name="capitalAssetInformation" required="true" type="java.lang.Object" description="The group accounting lines for each capital asset that will be shown"%>
<c:set var="groupAccountingLinesAttributes" value="${DataDictionary.CapitalAssetAccountsGroupDetails.attributes}" />
<%@ attribute name="capitalAssetPosition" required="true" description="The index of the CAB Capital Asset"%>
<%@ attribute name="showViewButton" required="true" description="To show the view/hide button for payments"%>

<c:set var="docPos" value="0" />
<c:set var="linePos" value="0" />

<c:set var="docPos" value="${docPos+1}" />

<div class="tab-container" align="center">
<table width="80%" cellpadding="0" cellspacing="0" class="datatable"  align="center"
	       style="width: 85%; text-align: left;">	
	<tr>
		<td class="tab-subhead"  width="100%" colspan="15">Accounting Lines Amount Distributions</td>
	</tr>
	<tr>
		<c:set var="tabKey" value="payment-${capitalAssetPosition}"/>
		<html:hidden property="tabStates(${tabKey})" value="CLOSE" />
		<td colspan="7" style="padding:0px; border-style:none;">
		<table class="datatable" cellpadding="0" cellspacing="0" align="center"
	       style="width: 100%; text-align: left;">
	       <c:if test="${showViewButton == true}" >
				<tr>
					<td colspan="11" class="tab-subhead" style="border-right: medium none;">
					<html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif"
			                                    property="methodToCall.toggleTab.tab${tabKey}"
			                                    title="toggle"
			                                    alt="show"
			                                    styleClass="tinybutton"
			                                    styleId="tab-${tabKey}-imageToggle"
			                                    onclick="javascript: return toggleTab(document, '${tabKey}'); "/>
			            Payments
					</td>
				</tr>
			<tbody  style="display: none;" id="tab-${tabKey}-div">
 		</c:if>			
			<tr align="center">
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.capitalAssetAccountLineNumber}"
					useShortLabel="true" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.sequenceNumber}"
					useShortLabel="true" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.financialDocumentLineTypeCode}"
					useShortLabel="true" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.chartOfAccountsCode}"
					useShortLabel="true"
					hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.accountNumber}"
					useShortLabel="true" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.subAccountNumber}"
					useShortLabel="true" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.financialObjectCode}"
					useShortLabel="true" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.financialSubObjectCode}"
					useShortLabel="true" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.projectCode}"
					useShortLabel="true" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.organizationReferenceId}"
					useShortLabel="true" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${groupAccountingLinesAttributes.amount}"
					useShortLabel="true" />
			</tr>
			<c:forEach items="${capitalAssetInformation.capitalAssetAccountsGroupDetails}" var="accountLine" >
				<tr>
					<td class="infoline" align="center">
						<div align="center" valign="middle">
							${accountLine.capitalAssetAccountLineNumber}&nbsp;
						</div></td>
					<td class="infoline">
						<div align="center" valign="middle">
							${accountLine.sequenceNumber}&nbsp;
						</div></td>
					<td class="infoline">
						<div>
				        	<c:set var="lineType" value="${accountLine.financialDocumentLineTypeCode}" />
				            <c:if test="${lineType eq KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE}">
					        	<c:out value="${KFSConstants.SOURCE}" />
					        </c:if>
				            <c:if test="${lineType eq KFSConstants.TARGET_ACCT_LINE_TYPE_CODE}">
					            <c:out value="${KFSConstants.TARGET}" />
					        </c:if>
				       </div></td>					
					<td class="infoline">${accountLine.chartOfAccountsCode}&nbsp;</td>
					<td class="infoline">${accountLine.accountNumber}&nbsp;</td>
					<td class="infoline">${accountLine.subAccountNumber}&nbsp;</td>
					<td class="infoline">${accountLine.financialObjectCode}&nbsp;</td>
					<td class="infoline">${accountLine.financialSubObjectCode}&nbsp;</td>
					<td class="infoline">${accountLine.projectCode}&nbsp;</td>
					<td class="infoline">${accountLine.organizationReferenceId}&nbsp;</td>
					<td class="infoline"><div align="right" valign="middle">${accountLine.amount}&nbsp;</div></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		</td>
	</tr>
</table>
</div>
