<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="overrideTitle" required="false"
	description="The title to be used for this section." %>
<%@ attribute name="documentAttributes" required="false" type="java.util.Map" 
	description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="showAmount" required="false"
    type="java.lang.Boolean"
    description="show the amount if true else percent" %>
<%@ attribute name="showInvoiced" required="false"
    type="java.lang.Boolean"
    description="post the unitPrice into the extendedPrice field" %>
<%@ attribute name="extraHiddenItemFields" required="false"
              description="A comma seperated list of names to be added to the list of normally hidden fields
              for the existing misc items." %>
<%@ attribute name="specialItemTotalType" required="false" %>
<%@ attribute name="specialItemTotalOverride" required="false" fragment="true"
              description="Fragment of code to specify special item total line" %>
<%@ attribute name="descriptionFirst" required="false" type="java.lang.Boolean"
    description="Whether or not to show item description before extended price." %>
              
<c:if test="${empty overrideTitle}">
	<c:set var="overrideTitle" value="Additional Charges"/>
</c:if>

<c:set var="amendmentEntry"
	value="${(!empty KualiForm.editingMode['amendmentEntry'])}" />

<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentType}" />

<c:choose>
    <c:when test= "${fn:contains(documentType, 'PurchaseOrder')}">
        <c:set var="isATypeOfPODoc" value="true" />
    </c:when>
    <c:otherwise>
        <c:set var="isATypeOfPODoc" value="false" />
    </c:otherwise>
</c:choose>

<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
<c:set var="tabKey" value="${kfunc:generateTabKey(overrideTitle)}" />
<!--  hit form method to increment tab index -->
<c:set var="dummyIncrementer" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}" />

<%-- default to closed --%>
<c:choose>
	<c:when test="${empty currentTab}">
		<c:set var="isOpen" value="false" />
	</c:when>
	<c:when test="${!empty currentTab}">
		<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
	</c:when>
</c:choose>
	
<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

<tr>
	<td colspan="11" class="subhead">
		<span class="subhead-left"><c:out value="${overrideTitle}" /> &nbsp;</span>
		<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
			<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
				onclick="javascript: return toggleTab(document, '${tabKey}'); " />
		</c:if>
		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
				onclick="javascript: return toggleTab(document, '${tabKey}'); " />
		</c:if>
	</td>
</tr>

<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	<tbody style="display: none;" id="tab-${tabKey}-div">
</c:if>

<!-- 
<tr>
	<td colspan="11" class="subhead"><span class="subhead-left"><c:out value="${overrideTitle}" /></span></td>
</tr>
-->

<tr>
	<c:set var="typeColSpan" value="5" />
	<c:if test="${showInvoiced}">
		<c:set var="typeColSpan" value="3" />
	</c:if>
	
	<kul:htmlAttributeHeaderCell colspan="${typeColSpan}"
		attributeEntry="${itemAttributes.itemTypeCode}" />
	
	<c:if test="${showInvoiced}">
		<kul:htmlAttributeHeaderCell colspan="1"
			attributeEntry="${itemAttributes.originalAmountfromPO}" />
		<kul:htmlAttributeHeaderCell colspan="1"
			attributeEntry="${itemAttributes.poOutstandingAmount}" />
	</c:if>
	
<c:choose>
	<c:when test="${descriptionFirst}">
		<kul:htmlAttributeHeaderCell colspan="2"
			attributeEntry="${itemAttributes.itemDescription}" />
		<kul:htmlAttributeHeaderCell colspan="4"
			attributeEntry="${itemAttributes.extendedPrice}" />
	</c:when>
    <c:otherwise>
		<kul:htmlAttributeHeaderCell colspan="2"
			attributeEntry="${itemAttributes.extendedPrice}" />
		<kul:htmlAttributeHeaderCell colspan="4"
			attributeEntry="${itemAttributes.itemDescription}" />
	</c:otherwise>
</c:choose>	
</tr>

<logic:iterate indexId="ctr" name="KualiForm" property="document.items"
	id="itemLine">
	<%-- to ensure order this should pull out items from APC instead of this--%>
	<c:if test="${itemLine.itemType.itemTypeAboveTheLineIndicator != true}">
		<c:if test="${not empty specialItemTotalType and itemLine.itemTypeCode == specialItemTotalType }">
			  <c:if test="${!empty specialItemTotalOverride}">
      			<jsp:invoke fragment="specialItemTotalOverride"/>
  			  </c:if>
		</c:if>
		<tr>
			<td colspan="11" class="tab-subhead" style="border-right: none;">
			<kul:htmlControlAttribute
				attributeEntry="${itemAttributes.itemTypeCode}"
				property="document.item[${ctr}].itemType.itemTypeDescription"
				readOnly="${true}" /> <!-- TODO need the show/hide? --></td>
		</tr>
		<tr>
			<td class="infoline" colspan="${typeColSpan}">
			    <html:hidden property="document.item[${ctr}].itemIdentifier" /> 
			    <html:hidden property="document.item[${ctr}].purapDocumentIdentifier" />
			    <html:hidden property="document.item[${ctr}].versionNumber" /> 
			    <html:hidden property="document.item[${ctr}].itemTypeCode" /> 
			    <html:hidden property="document.item[${ctr}].itemType.itemTypeCode" /> 
			    <html:hidden property="document.item[${ctr}].itemType.itemTypeDescription" /> 
			    <html:hidden property="document.item[${ctr}].itemType.active" /> 
			    <html:hidden property="document.item[${ctr}].itemType.quantityBasedGeneralLedgerIndicator" />
 			    <html:hidden property="document.item[${ctr}].itemType.itemTypeAboveTheLineIndicator" />
                <c:if test="${isATypeOfPODoc}">
                    <html:hidden property="document.item[${ctr}].itemActiveIndicator" />
                    <html:hidden property="document.item[${ctr}].itemInvoicedTotalQuantity" />
                    <html:hidden property="document.item[${ctr}].itemInvoicedTotalAmount" />
                    <html:hidden property="document.item[${ctr}].itemReceivedTotalQuantity" />
                    <html:hidden property="document.item[${ctr}].itemOutstandingEncumberedQuantity" />
                    <html:hidden property="document.item[${ctr}].itemOutstandingEncumberedAmount" />
                </c:if> 
 				<c:forTokens var="hiddenField" items="${extraHiddenItemFields}" delims=",">
 					<html:hidden property="document.item[${ctr}].${hiddenField}" />
 				</c:forTokens>		    
				
			    <div align="right">
			        <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemTypeCode}" property="document.item[${ctr}].itemType.itemTypeDescription" readOnly="${true}" />:&nbsp;
			    </div>
			</td>
			<c:if test="${showInvoiced}">
				<td class="infoline" colspan="1">
			    	<kul:htmlControlAttribute
				    	attributeEntry="${itemAttributes.purchaseOrderItemUnitPrice}"
				    	property="document.item[${ctr}].purchaseOrderItemUnitPrice"
				    	readOnly="true" />
		    	</td>
		    	<td class="infoline" colspan="1">
			    	<kul:htmlControlAttribute
				    	attributeEntry="${itemAttributes.poOutstandingAmount}"
				    	property="document.item[${ctr}].poOutstandingAmount"
				    	readOnly="true" />	
				</td>			
			</c:if>
			<c:choose>
				<c:when test="${descriptionFirst}">
					<td class="infoline" colspan="2">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="document.item[${ctr}].itemDescription" readOnly="${not (fullEntryMode or amendmentEntry)}" />
					</td>
					<td class="infoline" colspan="4">
						<div align="right">
							<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitPrice}" property="document.item[${ctr}].itemUnitPrice" readOnly="${not (fullEntryMode or amendmentEntry)}" styleClass="amount" />
						</div>
					</td>
				</c:when>
    			<c:otherwise>
					<td class="infoline" colspan="2">
						<div align="right">
							<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitPrice}" property="document.item[${ctr}].itemUnitPrice" readOnly="${not (fullEntryMode or amendmentEntry)}" styleClass="amount" />
						</div>
					</td>
					<td class="infoline" colspan="4">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="document.item[${ctr}].itemDescription" readOnly="${not (fullEntryMode or amendmentEntry)}" />
					</td>
				</c:otherwise>
			</c:choose>
		</tr>

		<c:if test="${amendmentEntry}">
			<purap:purapGeneralAccounting
				editingMode="${KualiForm.accountingLineEditingMode}"
				editableAccounts="${KualiForm.editableAccounts}"
				sourceAccountingLinesOnly="true" optionalFields="accountLinePercent"
				extraHiddenFields=",accountIdentifier,itemIdentifier,amount"
				accountingLineAttributes="${accountingLineAttributes}"
				accountPrefix="document.item[${ctr}]." hideTotalLine="true"
				hideFields="amount" accountingAddLineIndex="${ctr}" 
				ctr="${ctr}"/>
		</c:if>
		
		<!-- KULPURAP-1500 -->
		<c:if test="${(((!empty KualiForm.editingMode['allowItemEntry']) && (!empty itemLine.itemUnitPrice)) || (empty KualiForm.editingMode['allowItemEntry']))}">
		    <c:if test="${(!amendmentEntry && KualiForm.document.statusCode!='AFOA') || (KualiForm.document.statusCode=='AFOA' && !empty KualiForm.document.items[ctr].itemUnitPrice)}">
			    <c:set var="optionalFields" value="accountLinePercent" />
			    <c:set var="extraHiddenFields" value=",accountIdentifier,itemIdentifier,amount" />
	    	    <c:set var="hideFields" value="amount" />
			    <c:if test="${showAmount}">
				    <c:set var="optionalFields" value="" />
				    <c:set var="extraHiddenFields" value=",accountIdentifier,itemIdentifier,accountLinePercent" />
				    <c:set var="hideFields" value="" />
			    </c:if>
			    <purap:purapGeneralAccounting editingMode="${KualiForm.editingMode}"
				    editableAccounts="${KualiForm.editableAccounts}"
				    sourceAccountingLinesOnly="true" optionalFields="${optionalFields}"
				    extraHiddenFields="${extraHiddenFields}"
				    accountingLineAttributes="${accountingLineAttributes}"
				    accountPrefix="document.item[${ctr}]." hideTotalLine="true"
				    hideFields="${hideFields}" accountingAddLineIndex="${ctr}" 
				    ctr="${ctr}" />
		    </c:if>
		</c:if>
	</c:if>
</logic:iterate>

<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	</tbody>
</c:if>
