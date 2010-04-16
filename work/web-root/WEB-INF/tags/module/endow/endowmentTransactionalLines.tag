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

<%@ attribute name="editingMode" required="false" description="used to decide if items may be edited" type="java.util.Map"%>
<%@ attribute name="isSource" required="true" %>
<%@ attribute name="isTarget" required="true" %>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Transaction Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ITEM_LINE_ERRORS}">

<c:if test="${isSource}" >
  <c:set var="lineAttributes" value="${DataDictionary.EndowmentSourceTransactionLine.attributes}" />
  <c:set var="newTransactionLine" value="newSourceTransactionLine" />
  <c:set var="methodToCallAdd" value="methodToCall.insertSourceTransactionLine" />
  <c:set var="methodToCallDelete" value="methodToCall.deleteSourceTransactionLine" />
  <c:set var="transLines" value="document.sourceTransactionLines"/>  
</c:if>
<c:if test="${isTarget}">
  <c:set var="lineAttributes" value="${DataDictionary.EndowmentTargetTransactionLine.attributes}" />
  <c:set var="newTransactionLine" value="newTargetTransactionLine" />
  <c:set var="methodToCallAdd" value="methodToCall.insertTargetTransactionLine" />
  <c:set var="methodToCallDelete" value="methodToCall.deleteTargetTransactionLine" />
  <c:set var="transLines" value="document.targetTransactionLines"/>
</c:if>

 <div class="tab-container" align=center>
	<h3>Transaction Lines</h3>
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Transaction Lines section">
	    
		<tr>
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.kemid}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.etranCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionLineDescription}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionIPIndicatorCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionAmount}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionUnits}"/>
            <c:if test="${not readOnly}">
                <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
            </c:if>
		</tr>
      
        <c:if test="${not readOnly}">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row"/>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" property="${newTransactionLine}.kemid" />
                    <kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.KEMID"
				                fieldConversions="kemid:${newTransactionLine}.kemid" />
				</td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.etranCode}" property="${newTransactionLine}.etranCode" />
                    <kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode"
				                fieldConversions="code:${newTransactionLine}.etranCode" />
                </td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionLineDescription}" property="${newTransactionLine}.transactionLineDescription" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionIPIndicatorCode}" property="${newTransactionLine}.transactionIPIndicatorCode" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionAmount}" property="${newTransactionLine}.transactionAmount" styleClass="right"/></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionUnits}" property="${newTransactionLine}.transactionUnits" styleClass="right"/></td>
                <td class="infoline"><div align="center"><html:image property="${methodToCallAdd}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add Transaction Line" title="add" styleClass="tinybutton"/></div></td>
            </tr>
        </c:if>
      
        <logic:iterate id="item" name="KualiForm" property="${transLines}" indexId="ctr">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}:" scope="row" ></kul:htmlAttributeHeaderCell>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" property="${transLines}[${ctr}].kemid" readOnly="${readOnly}"/>
                	<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.KEMID"
				                fieldConversions="kemid:${transLines}[${ctr}].kemid" />
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.etranCode}" property="${transLines}[${ctr}].etranCode" readOnly="${readOnly}"/>
                	<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode"
				                fieldConversions="code:${transLines}[${ctr}].etranCode" />
                </td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionLineDescription}" property="${transLines}[${ctr}].transactionLineDescription" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionIPIndicatorCode}" property="${transLines}[${ctr}].transactionIPIndicatorCode" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionAmount}" property="${transLines}[${ctr}].transactionAmount" readOnly="${readOnly}" styleClass="right"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionUnits}" property="${transLines}[${ctr}].transactionUnits" readOnly="${readOnly}" styleClass="right"/></td>              
                
                <c:if test="${not readOnly}">
                    <td class="datacell"><div align="center"><html:image property="${methodToCallDelete}.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Item ${ctr+1}" alt="Delete Item ${ctr+1}" styleClass="tinybutton"/></div></td>
                </c:if>
            </tr>
        </logic:iterate>
        
        <tr>
			<td class="total-line" colspan="5">
				&nbsp;
			</td>
			<td class="total-line">
				<strong>Total Income Amount:
					${KualiForm.document.totalIncomeAmount}</strong>
			</td>
			<td class="total-line">
			    <strong>Total Principal Amount:
					${KualiForm.document.totalPrincipalAmount}</strong>
			</td>
			<c:if test="${!readOnly}">
				<td class="total-line">
					&nbsp;
				</td>
			</c:if>
		</tr>
        <tr>
			<td class="total-line" colspan="5">
				&nbsp;
			</td>
			<td class="total-line">
				<strong>Total Income Units:
					${KualiForm.document.totalIncomeUnits}</strong>
			</td>
			<td class="total-line">
			    <strong>Total Principal Units:
					${KualiForm.document.totalPrincipalUnits}</strong>
			</td>
			<c:if test="${!readOnly}">
				<td class="total-line">
					&nbsp;
				</td>
			</c:if>
		</tr>
        
	</table>
</div>

</kul:tab>