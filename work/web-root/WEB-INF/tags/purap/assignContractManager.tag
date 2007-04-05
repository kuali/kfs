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

<c:set var="requisitionAttributes" value="${DataDictionary.KualiRequisitionDocument.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:tab tabTitle="Assign A Contract Manager" defaultOpen="true" tabErrorKey="${PurapConstants.ASSIGN_CONTRACT_MANAGER_TAB_ERRORS}">

    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Assign A Contract Manager</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Assign A Contract Manager">
			<c:if test="${empty KualiForm.document.assignContractManagerDetails}">
		        <th align=right valign=middle class="bord-l-b">
		            <div align="center"><br>There are no unassigned requisitions.<br></div>
		        </th>
			</c:if>
			<c:if test="${!empty KualiForm.document.assignContractManagerDetails}">
	            <tr>
	                <th align=center valign=middle class="bord-l-b">
	                    Contract Manager
	                </th>
	                <th align=center valign=middle class="bord-l-b">
	                    Req Number
	                </th>
	                <th align=center valign=middle class="bord-l-b">
	                    Delivery Campus
	                </th>
	                <th align=center valign=middle class="bord-l-b">
	                    Vendor Name
	                </th>
	                <th align=center valign=middle class="bord-l-b">
	                    General Desc
	                </th>
	                <th align=center valign=middle class="bord-l-b">
	                    Total
	                </th>
	                <th align=center valign=middle class="bord-l-b">
	                    Create Date
	                </th>
	                <th align=center valign=middle class="bord-l-b">
	                    First Item Description
	                </th>
	                <th align=center valign=middle class="bord-l-b">
	                    First Object Code
	                </th>
	            </tr>
	
		        <logic:iterate id="acmDetail" name="KualiForm" property="document.assignContractManagerDetails" indexId="ctr">
		            <tr>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].contractManagerCode" attributeEntry="${requisitionAttributes.contractManagerCode}" readOnly="${readOnly}" />
					        <c:if test="${!readOnly}" >
		                        <kul:lookup boClassName="org.kuali.module.vendor.bo.ContractManager" fieldConversions="contractManagerCode:document.assignContractManagerDetail[${ctr}].contractManagerCode" /></div>
					        </c:if>
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.purapDocumentIdentifier" attributeEntry="${requisitionAttributes.purapDocumentIdentifier}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.deliveryCampusCode" attributeEntry="${requisitionAttributes.deliveryCampusCode}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.vendorName" attributeEntry="${requisitionAttributes.vendorName}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.documentHeader.financialDocumentDescription" attributeEntry="${requisitionAttributes.documentHeader.financialDocumentDescription}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.documentHeader.financialDocumentTotalAmount" attributeEntry="${requisitionAttributes.documentHeader.financialDocumentTotalAmount}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    Create Date
		                </td>
		                <td align=left valign=middle class="datacell">
		                    1st Item Desc
		                </td>
		                <td align=left valign=middle class="datacell">
		                    1st Item Obj Code
		                </td>
		                <html:hidden property="document.assignContractManagerDetail[${ctr}].requisitionIdentifier" />
		            </tr>
		        </logic:iterate>
			</c:if>
        </table>
    </div>
</kul:tab>
