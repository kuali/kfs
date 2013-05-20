<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<%@ attribute name="editingMode" required="true" description="used to decide if items may be edited" type="java.util.Map"%>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Final Invoice Reversal Entries" defaultOpen="true" tabErrorKey="document.invoiceEntries">
	<c:set var="attributes" value="${DataDictionary.FinalInvoiceReversalEntry.attributes}" />
	<div class="tab-container" align=center>
		<h3>Entries</h3>
		<table cellpadding=0 class="datatable" summary="Final Invoice Reversal Entries">

			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
				<kul:htmlAttributeHeaderCell attributeEntry="${attributes.invoiceDocumentNumber}" />
				<c:if test="${not readOnly}">
					<kul:htmlAttributeHeaderCell literalLabel="Actions" />
				</c:if>
			</tr>
			<c:if test="${not readOnly}">
				<tr>
					<kul:htmlAttributeHeaderCell literalLabel="add:" scope="row" />
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${attributes.invoiceDocumentNumber}" property="invoiceEntry.invoiceDocumentNumber"
							readOnly="${readOnly}" /></td>
					<td class="infoline">
						<div align="center">
							<html:image property="methodToCall.addInvoiceEntry" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
								alt="Add an Invoice Entry" title="Add an Invoice Entry" styleClass="tinybutton" />
						</div>
					</td>
				</tr>
			</c:if>
			<logic:iterate id="FinalInvoiceReversalEntry" name="KualiForm" property="document.invoiceEntries" indexId="ctr">
				<tr>
					<kul:htmlAttributeHeaderCell literalLabel="${ctr+1}:" scope="row" />

					<td class="datacell"><kul:htmlControlAttribute attributeEntry="${attributes.invoiceDocumentNumber}"
							property="document.invoiceEntries[${ctr}].invoiceDocumentNumber" readOnly="true" /></td>
					<c:if test="${not readOnly}">
						<td class="datacell">
							<div align="center">
								<html:image property="methodToCall.deleteInvoiceEntry.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
									alt="Delete an Invoice Entry" title="Delete an Invoice Entry" styleClass="tinybutton" />
							</div>
						</td>
					</c:if>
				</tr>
			</logic:iterate>
		</table>
	</div>
</kul:tab>
