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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
			  description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>	

taxLotLines

<kul:tab tabTitle="Tax Lot Lines" defaultOpen="false"
	tabErrorKey="${KFSConstants.TAX_LOT_LINE_DOCUMENT_ERRORS}">
	<div class="tab-container" align=center>
			<h3>Tax Lot Lines</h3>
		<table cellpadding="0" cellspacing="0" summary="Asset Increase Details">

       <logic:iterate id="item" name="KualiForm" property="taxLotLines" indexId="ctr">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}:" scope="row">
                    <%-- Outside this th, these hidden fields would be invalid HTML. --%>
                    <html:hidden property="document.item[${ctr}].itemSequenceId" />
                    <html:hidden property="document.item[${ctr}].versionNumber" />
                </kul:htmlAttributeHeaderCell>
  
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemStockNumber}" property="${transLines[ctr]]}.kemid" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemStockDescription}" property="document.item[${ctr}].itemStockDescription" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemQuantity}" property="document.item[${ctr}].itemQuantity" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.unitOfMeasureCode}" property="document.item[${ctr}].unitOfMeasureCode" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemUnitAmount}" property="document.item[${ctr}].itemUnitAmount" readOnly="${readOnly}" styleClass="amount"/></td>
                <td class="datacell">$${KualiForm.document.items[ctr].total}</td> <%-- EL doesn't quash items' plural like Struts does. --%>
                <c:if test="${not readOnly}">
                    <td class="datacell"><div align="center"><html:image property="methodToCall.deleteItem.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Item ${ctr+1}" alt="Delete Item ${ctr+1}" styleClass="tinybutton"/></div></td>
                </c:if>
            </tr>
        </logic:iterate>
 
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.transactionSubTypeCode}"
					horizontal="true" width="50%" />

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.transactionSubTypeCode}"
						property="document.transactionSubTypeCode"/>
				</td>
			</tr>
			
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.transactionSourceTypeCode}"
					horizontal="true" width="50%" />

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.transactionSourceTypeCode}"
						property="document.transactionSourceTypeCode"
						readOnly="true"/>
				</td>
			</tr>
			
		</table>
	</div>
</kul:tab>
