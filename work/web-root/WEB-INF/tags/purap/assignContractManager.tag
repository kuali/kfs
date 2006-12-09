<%--
 Copyright 2006 The Kuali Foundation.
 
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

<!--  c:set var="assignContractManagerAttributes" value="${DataDictionary.AssignContractManager.attributes}" / -->
<c:set var="requisitionAttributes" value="${DataDictionary.KualiRequisitionDocument.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:tab tabTitle="Assign A Contract Manager" defaultOpen="true" tabErrorKey="${PurapConstants.ASSIGN_CONTRACT_MANAGER_TAB_ERRORS}">

    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Assign A Contract Manager</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Assign A Contract Manager">

		      1<kul:displayIfErrors keyMatch="${PurapConstants.ASSIGN_CONTRACT_MANAGER_ERRORS}">
				  <tr>
		    		<th colspan=3>
		        		2<kul:errors keyMatch="${PurapConstants.ASSIGN_CONTRACT_MANAGER_ERRORS}" />
		    		</th>
				  </tr>    
    		  </kul:displayIfErrors>
	
			<c:if test="${empty KualiForm.document.unassignedRequisitions}">
		        <th align=right valign=middle class="bord-l-b">
		            <div align="center"><br>There are no unassigned requisitions.<br></div>
		        </th>
			</c:if>
			<c:if test="${!empty KualiForm.document.unassignedRequisitions}">
	            <tr>
	            	<!-- Contract Manager -->
	                <th align=center valign=middle class="bord-l-b">
	                    Contract Manager
	                </th>
	            	<!-- Req Number -->
	                <th align=center valign=middle class="bord-l-b">
	                    Req Number
	                </th>
	            	<!-- Delivery Campus -->
	                <th align=center valign=middle class="bord-l-b">
	                    Delivery Campus
	                </th>
	            	<!-- Vendor Name -->
	                <th align=center valign=middle class="bord-l-b">
	                    Vendor Name
	                </th>
	            	<!-- General Desc -->
	                <th align=center valign=middle class="bord-l-b">
	                    General Desc
	                </th>
	            	<!-- Total -->
	                <th align=center valign=middle class="bord-l-b">
	                    Total
	                </th>
	            	<!-- Doc Create Date -->
	                <th align=center valign=middle class="bord-l-b">
	                    Create Date
	                </th>
	            	<!-- First Item Description -->
	                <th align=center valign=middle class="bord-l-b">
	                    First Item Description
	                </th>
	            	<!-- First Object Code -->
	                <th align=center valign=middle class="bord-l-b">
	                    First Object Code
	                </th>
	            </tr>
	
		        <logic:iterate id="requisitions" name="KualiForm" property="document.unassignedRequisitions" indexId="ctr">
		            <tr>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.unassignedRequisition[${ctr}].contractManagerCode" attributeEntry="${requisitionAttributes.contractManagerCode}" readOnly="${readOnly}" />
					        <c:if test="${!readOnly}" >
		                        <kul:lookup boClassName="org.kuali.module.purap.bo.ContractManager" fieldConversions="contractManagerCode:document.unassignedRequisition[${ctr}].contractManagerCode" /></div>
					        </c:if>
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.unassignedRequisition[${ctr}].identifier" attributeEntry="${requisitionAttributes.identifier}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.unassignedRequisition[${ctr}].deliveryCampusCode" attributeEntry="${requisitionAttributes.deliveryCampusCode}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.unassignedRequisition[${ctr}].vendorName" attributeEntry="${requisitionAttributes.vendorName}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.unassignedRequisition[${ctr}].documentHeader.financialDocumentDescription" attributeEntry="${requisitionAttributes.documentHeader.financialDocumentDescription}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.unassignedRequisition[${ctr}].documentHeader.financialDocumentTotalAmount" attributeEntry="${requisitionAttributes.documentHeader.financialDocumentTotalAmount}" readOnly="true" />
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
		                <html:hidden property="document.unassignedRequisition[${ctr}].identifier" />
		            </tr>
		        </logic:iterate>
			</c:if>
        </table>
    </div>
</kul:tab>
