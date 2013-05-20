<%--
 Copyright 2006-2009 The Kuali Foundation
 
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

<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>

<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:if test="${!empty KualiForm.document.proposalNumber}">
	<kul:tab tabTitle="Organization" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_ORGANIZATION_ERRORS}">
		<div class="tab-container" align=center>
			<table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
				<tr>
					<td colspan="4" class="subhead">Organization</td>
				</tr>
				<tr>
					<th align=right valign=middle class="bord-l-b" style="width: 25%;">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.processingChartOfAccountCode}" />
						</div>
					</th>
					<td align=left valign=middle class="datacell" style="width: 25%;"><kul:htmlControlAttribute
							attributeEntry="${arDocHeaderAttributes.processingChartOfAccountCode}"
							property="document.accountsReceivableDocumentHeader.processingChartOfAccountCode" readOnly="true" /></td>
					<th align=right valign=middle class="bord-l-b" style="width: 25%;">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${documentAttributes.billByChartOfAccountCode}" />
						</div>
					</th>
					<td align=left valign=middle class="datacell" style="width: 25%;"><kul:htmlControlAttribute
							attributeEntry="${documentAttributes.billByChartOfAccountCode}" property="document.billByChartOfAccountCode" readOnly="true" /></td>
				</tr>
				<tr>
					<th align=right valign=middle class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.processingOrganizationCode}" />
						</div>
					</th>
					<td align=left valign=middle class="datacell"><kul:htmlControlAttribute attributeEntry="${arDocHeaderAttributes.processingOrganizationCode}"
							property="document.accountsReceivableDocumentHeader.processingOrganizationCode" readOnly="true" /></td>
					<th align=right valign=middle class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${documentAttributes.billedByOrganizationCode}" />
						</div>
					</th>
					<td align=left valign=middle class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.billedByOrganizationCode}"
							property="document.billedByOrganizationCode" readOnly="true" /> </td>
				</tr>
			</table>
		</div>
	</kul:tab>
</c:if>