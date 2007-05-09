<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>
<%@ taglib tagdir="/WEB-INF/tags/fin" prefix="fin"%>

<%@ attribute name="itemAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>

		<table cellpadding="0" cellspacing="0" class="datatable" colspan="12"
			summary="Misc Items Section">
			<tr>
			  <td colspan="12" class="subhead">
			    <span class="subhead-left">Misc Items</span>
			  </td>		
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" colspan="3" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${itemAttributes.itemTypeCode}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${itemAttributes.itemDescription}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${itemAttributes.itemUnitPrice}" />
				<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" colspan="3" />
			</tr>
			<logic:iterate indexId="ctr" name="KualiForm"
				property="document.items" id="itemLine">
<%-- to ensure order this should pull out items from APC instead of this--%>				
				<c:if test="${itemLine.itemType.itemTypeAboveTheLineIndicator != true}">
				<tr>

					<kul:htmlAttributeHeaderCell scope="row" colspan="3">
						<html:hidden property="document.item[${ctr}].itemIdentifier" />
						<html:hidden property="document.item[${ctr}].versionNumber" />
						<html:hidden property="document.item[${ctr}].itemTypeCode" />
						<html:hidden property="document.item[${ctr}].itemType.itemTypeCode" />
						<div align="center">
							&nbsp;
						</div>
					</kul:htmlAttributeHeaderCell>

					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemTypeCode}"
							property="document.item[${ctr}].itemType.itemTypeDescription"
							readOnly="${true}" />
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemDescription}"
							property="document.item[${ctr}].itemDescription"
							readOnly="${not fullEntryMode}" />
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemUnitPrice}"
							property="document.item[${ctr}].itemUnitPrice"
							readOnly="${not fullEntryMode}" />
					</td>
					<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" colspan="3" />
				</tr>
				
				<tr>
					<td width="100%" colspan="12">

						<fin:accountingLines editingMode="${KualiForm.editingMode}"
							editableAccounts="${KualiForm.editableAccounts}"
							sourceAccountingLinesOnly="true"
							optionalFields="accountLinePercent"
							extraHiddenFields=",accountIdentifier,itemIdentifier"
							accountingLineAttributes="${accountingLineAttributes}"
							accountPrefix="document.item[${ctr}]." hideTotalLine="true"
							hideFields="amount" accountingAddLineIndex="${ctr}" /> 

					</td>
				</tr>
			</c:if>
			</logic:iterate>
		</table>
