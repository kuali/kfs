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
<%@ attribute name="specialItemTotalType" required="false" %>
<%@ attribute name="specialItemTotalOverride" required="false" fragment="true"
              description="Fragment of code to specify special item total line" %>
<%@ attribute name="descriptionFirst" required="false" type="java.lang.Boolean"
    description="Whether or not to show item description before extended price." %>
<%@ attribute name="mainColumnCount" required="true" %>
<%@ attribute name="colSpanDescription" required="true" %>
<%@ attribute name="colSpanExtendedPrice" required="true" %>
<%@ attribute name="colSpanItemType" required="true" %>
<%@ attribute name="colSpanBlank" required="true" %>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />

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
	
<tr>
	<td colspan="${mainColumnCount}" class="subhead">
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
	<td colspan="${mainColumnCount}" class="subhead"><span class="subhead-left"><c:out value="${overrideTitle}" /></span></td>
</tr>
-->

<tr>
	<c:set var="colSpanDescription" value="${colSpanDescription}" />
	<c:set var="colSpanExtendedPrice" value="${colSpanExtendedPrice}" />
	<c:set var="colSpanItemType" value="${colSpanItemType}" />
	<c:set var="colSpanBlank" value="${colSpanBlank}" />
	
	<kul:htmlAttributeHeaderCell colspan="${colSpanItemType}"
		attributeEntry="${itemAttributes.itemTypeCode}" />
	
	<c:if test="${showInvoiced}">
		<kul:htmlAttributeHeaderCell 
			attributeEntry="${itemAttributes.originalAmountfromPO}" />
		<kul:htmlAttributeHeaderCell
			attributeEntry="${itemAttributes.poOutstandingAmount}" />
	</c:if>
	
	<c:choose>
		<c:when test="${descriptionFirst}">
			<kul:htmlAttributeHeaderCell colspan="${colSpanDescription}"
				attributeEntry="${itemAttributes.itemDescription}" />
			<kul:htmlAttributeHeaderCell colspan="${colSpanExtendedPrice}"
				attributeEntry="${itemAttributes.extendedPrice}" />
			<th colspan="${colSpanBlank}">&nbsp;</th>
		</c:when>
	    <c:otherwise>
			<kul:htmlAttributeHeaderCell colspan="${colSpanExtendedPrice}"
				attributeEntry="${itemAttributes.extendedPrice}" />
			<kul:htmlAttributeHeaderCell colspan="${colSpanDescription}"
				attributeEntry="${itemAttributes.itemDescription}" />
			<th colspan="${colSpanBlank}">&nbsp;</th>
		</c:otherwise>
	</c:choose>	
</tr>

<logic:iterate indexId="ctr" name="KualiForm" property="document.items"
	id="itemLine">
	<%-- to ensure order this should pull out items from APC instead of this--%>
	<c:if test="${itemLine.itemType.additionalChargeIndicator == true}">
		<c:if test="${not empty specialItemTotalType and itemLine.itemTypeCode == specialItemTotalType }">
			  <c:if test="${!empty specialItemTotalOverride}">
      			<jsp:invoke fragment="specialItemTotalOverride"/>
  			  </c:if>
		</c:if>
		<tr>
			<td colspan="${mainColumnCount}" class="tab-subhead" style="border-right: none;">
			<kul:htmlControlAttribute
				attributeEntry="${itemAttributes.itemTypeCode}"
				property="document.item[${ctr}].itemType.itemTypeDescription"
				readOnly="${true}" /> <!-- TODO need the show/hide? --></td>
		</tr>
		<tr>
			<td class="infoline" colspan="${colSpanItemType}">
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
					<td class="infoline" colspan="${colSpanDescription}">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="document.item[${ctr}].itemDescription" readOnly="${not (fullEntryMode or amendmentEntry)}" />
					</td>
					<td class="infoline" colspan="${colSpanExtendedPrice}">
						<div align="right">
							<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitPrice}" property="document.item[${ctr}].itemUnitPrice" readOnly="${not (fullEntryMode or amendmentEntry)}" styleClass="amount" />
						</div>
					</td>
					<td class="infoline" colspan="${colSpanBlank}">
						&nbsp;
					</td>
				</c:when>
    			<c:otherwise>
					<td class="infoline" colspan="${colSpanExtendedPrice}">
						<div align="right">
							<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitPrice}" property="document.item[${ctr}].itemUnitPrice" readOnly="${not (fullEntryMode or amendmentEntry)}" styleClass="amount" />
						</div>
					</td>
					<td class="infoline" colspan="${colSpanDescription}">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="document.item[${ctr}].itemDescription" readOnly="${not (fullEntryMode or amendmentEntry)}" />
					</td>
					<td class="infoline" colspan="${colSpanBlank}">
						&nbsp;
					</td>
				</c:otherwise>
			</c:choose>
		</tr>

		<c:if test="${amendmentEntry}">
			<purap:purapGeneralAccounting
				accountPrefix="document.item[${ctr}]." 
				itemColSpan="${mainColumnCount}"/>
		</c:if>
		
		<!-- KULPURAP-1500 -->
		<c:if test="${(((!empty KualiForm.editingMode['allowItemEntry']) && (!empty itemLine.itemUnitPrice)) || (empty KualiForm.editingMode['allowItemEntry']))}">
		    <c:if test="${(!amendmentEntry && KualiForm.document.statusCode!='AFOA') || (KualiForm.document.statusCode=='AFOA' && !empty KualiForm.document.items[ctr].itemUnitPrice)}">
			    <c:if test="${showAmount}">
			    </c:if>
			    <purap:purapGeneralAccounting 
				    accountPrefix="document.item[${ctr}]."
				    itemColSpan="${mainColumnCount}" />
		    </c:if>
		</c:if>
	</c:if>
</logic:iterate>

<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	</tbody>
</c:if>
