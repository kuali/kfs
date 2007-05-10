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
<%@ taglib tagdir="/WEB-INF/tags/purap" prefix="purap"%>

<%@ attribute name="displayRequisitionFields" required="false"
	description="Boolean to indicate if REQ specific fields should be displayed"%>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>

<kul:tab tabTitle="Items" defaultOpen="${not displayRequisitionFields}">
	<div class="tab-container" align=center>
	<table cellpadding="0" cellspacing="0" class="datatable"
		summary="Items Section">
		<tr>
			<td colspan="12" class="subhead"><span class="subhead-left">Add
			Item</span> <span class="subhead-right"> <html:image
				property="methodToCall.setupAccountDistribution"
				src="images/tinybutton-setaccdist.gif"
				alt="setup account distribution" title="setup account distribution"
				styleClass="tinybutton" /> <html:image
				property="methodToCall.removeAccounts"
				src="images/tinybutton-remaccitems.gif"
				alt="remove accounts from all items"
				title="remove accounts from all items" styleClass="tinybutton" /> </span></td>
		</tr>
		<tr>
			<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemLineNumber}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemTypeCode}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemQuantity}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemCatalogNumber}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemDescription}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemUnitPrice}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.extendedPrice}" />
			<%--<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemRestrictedIndicator}"/>--%>
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" />
			<kul:htmlAttributeHeaderCell literalLabel="Actions" />
		</tr>
		<!--  if fullEntryMode, then display the addLine -->
		<c:if test="${fullEntryMode}">
			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="add:" scope="row" />
				<td class="infoline"><kul:htmlControlAttribute
					attributeEntry="${itemAttributes.itemLineNumber}"
					property="newPurchasingItemLine.itemLineNumber"
					readOnly="${not fullEntryMode}" /></td>
				<td class="infoline"><kul:htmlControlAttribute
					attributeEntry="${itemAttributes.itemTypeCode}"
					property="newPurchasingItemLine.itemTypeCode"
					readOnly="${not fullEntryMode}" /></td>
				<td class="infoline"><kul:htmlControlAttribute
					attributeEntry="${itemAttributes.itemQuantity}"
					property="newPurchasingItemLine.itemQuantity"
					readOnly="${not fullEntryMode}" /></td>
				<td class="infoline"><kul:htmlControlAttribute
					attributeEntry="${itemAttributes.itemUnitOfMeasureCode}"
					property="newPurchasingItemLine.itemUnitOfMeasureCode"
					readOnly="${not fullEntryMode}" /></td>
				<td class="infoline"><kul:htmlControlAttribute
					attributeEntry="${itemAttributes.itemCatalogNumber}"
					property="newPurchasingItemLine.itemCatalogNumber"
					readOnly="${not fullEntryMode}" /></td>
				<td class="infoline"><kul:htmlControlAttribute
					attributeEntry="${itemAttributes.itemDescription}"
					property="newPurchasingItemLine.itemDescription"
					readOnly="${not fullEntryMode}" /></td>
				<td class="infoline"><kul:htmlControlAttribute
					attributeEntry="${itemAttributes.itemUnitPrice}"
					property="newPurchasingItemLine.itemUnitPrice"
					readOnly="${not fullEntryMode}" /></td>
				<td class="infoline"><kul:htmlControlAttribute
					attributeEntry="${itemAttributes.extendedPrice}"
					property="newPurchasingItemLine.extendedPrice" readOnly="${true}" />
				</td>
				<%--<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemRestrictedIndicator}" property="newPurchasingItemLine.itemRestrictedIndicator" readOnly="${not fullEntryMode}"/></td>--%>
				<td class="infoline"><kul:htmlControlAttribute
					attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}"
					property="newPurchasingItemLine.itemAssignedToTradeInIndicator"
					readOnly="${not fullEntryMode}" /></td>
				<td class="infoline">
				<div align="center"><html:image
					property="methodToCall.addItem" src="images/tinybutton-add1.gif"
					alt="Insert an Item" title="Add an Item" styleClass="tinybutton" />
				</div>
				</td>
			</tr>
		</c:if>
		<!-- End of if fullEntryMode, then display the addLine -->
		<c:if
			test="${fn:length(KualiForm.document.items)>fn:length(KualiForm.document.belowTheLineTypes)}">
	</table>

    <purap:accountdistribution
		accountingLineAttributes="${accountingLineAttributes}" />

	<table class="datatable" style="width: 100%;">

		<tr>
			<td colspan="12" class="subhead"><span class="subhead-left">Current
			Items</span></td>
		</tr>
		<tr>
			<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemLineNumber}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemTypeCode}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemQuantity}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemCatalogNumber}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemDescription}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemUnitPrice}" />
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.extendedPrice}" />
			<%--<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemRestrictedIndicator}"/>--%>
			<kul:htmlAttributeHeaderCell
				attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" />
			<kul:htmlAttributeHeaderCell literalLabel="Actions" />
		</tr>
		</c:if>

		<logic:iterate indexId="ctr" name="KualiForm"
			property="document.items" id="itemLine">
			<c:if
				test="${itemLine.itemType.itemTypeAboveTheLineIndicator == true}">


				<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}"
					scope="request" />
				<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}"
					scope="request" />

				<c:set var="currentTab" value="${KualiForm.tabStateJstl}" />

				<%-- default to closed --%>
				<c:choose>
					<c:when test="${empty currentTab}">
						<c:set var="isOpen" value="false" />
					</c:when>
					<c:when test="${!empty currentTab}">
						<c:set var="isOpen" value="${currentTab.open}" />
					</c:when>
				</c:choose>


				<html:hidden property="tabState[${currentTabIndex}].open"
					value="${isOpen}" />

				<tr>
					<td class="total-line" colspan="12" style="padding: 0px;">
					<table class="datatable" style="width: 100%;">
						<tr>
							<td colspan="4" class="tab-subhead" style="border-right: none;">Item ${ctr}
							<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
								<html:image
									property="methodToCall.toggleTab.tab${currentTabIndex}"
									src="images/tinybutton-hide.gif" alt="hide" title="toggle"
									styleClass="tinybutton"
									styleId="tab-${currentTabIndex}-imageToggle"
									onclick="javascript: return toggleTab(document, ${currentTabIndex}); " />
							</c:if> <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
								<html:image
									property="methodToCall.toggleTab.tab${currentTabIndex}"
									src="images/tinybutton-show.gif" alt="show" title="toggle"
									styleClass="tinybutton"
									styleId="tab-${currentTabIndex}-imageToggle"
									onclick="javascript: return toggleTab(document, ${currentTabIndex}); " />
							</c:if></td>
						</tr>
					</table>

					<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
						<div style="display: block;" id="tab-${currentTabIndex}-div">
					</c:if> <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
						<div style="display: none;" id="tab-${currentTabIndex}-div">
					</c:if>
					<table class="datatable" style="width: 100%;">
						<tr>

							<kul:htmlAttributeHeaderCell scope="row">
								<html:hidden property="document.item[${ctr}].itemIdentifier" />
								<html:hidden property="document.item[${ctr}].versionNumber" />
								<html:hidden property="document.item[${ctr}].itemTypeCode" />
								<html:hidden
									property="document.item[${ctr}].itemType.itemTypeCode" />
								<html:hidden
									property="document.item[${ctr}].itemType.itemTypeDescription" />
								<html:hidden property="document.item[${ctr}].itemType.active" />
								<html:hidden
									property="document.item[${ctr}].itemType.quantityBasedGeneralLedgerIndicator" />
								<html:hidden
									property="document.item[${ctr}].itemType.itemTypeAboveTheLineIndicator" />
								<div align="center">&nbsp;</div>
							</kul:htmlAttributeHeaderCell>

							<td class="infoline"><html:hidden write="true"
								property="document.item[${ctr}].itemLineNumber" /> <c:if
								test="${fullEntryMode}">
								<html:image property="methodToCall.editItem"
									src="images/purap-up.gif" alt="Move Item Up"
									title="Move Item Up" styleClass="tinybutton" />
								<html:image property="methodToCall.editItem"
									src="images/purap-down.gif" alt="Move Item Down"
									title="Move Item Down" styleClass="tinybutton" />
							</c:if></td>
							<td class="infoline"><kul:htmlControlAttribute
								attributeEntry="${itemAttributes.itemTypeCode}"
								property="document.item[${ctr}].itemTypeCode"
								readOnly="${not fullEntryMode}" /></td>
							<td class="infoline"><kul:htmlControlAttribute
								attributeEntry="${itemAttributes.itemQuantity}"
								property="document.item[${ctr}].itemQuantity"
								readOnly="${not fullEntryMode}" /></td>
							<td class="infoline"><kul:htmlControlAttribute
								attributeEntry="${itemAttributes.itemUnitOfMeasureCode}"
								property="document.item[${ctr}].itemUnitOfMeasureCode"
								readOnly="${not fullEntryMode}" /></td>
							<td class="infoline"><kul:htmlControlAttribute
								attributeEntry="${itemAttributes.itemCatalogNumber}"
								property="document.item[${ctr}].itemCatalogNumber"
								readOnly="${not fullEntryMode}" /></td>
							<td class="infoline"><kul:htmlControlAttribute
								attributeEntry="${itemAttributes.itemDescription}"
								property="document.item[${ctr}].itemDescription"
								readOnly="${not fullEntryMode}" /></td>
							<td class="infoline"><kul:htmlControlAttribute
								attributeEntry="${itemAttributes.itemUnitPrice}"
								property="document.item[${ctr}].itemUnitPrice"
								readOnly="${not fullEntryMode}" /></td>
							<td class="infoline"><kul:htmlControlAttribute
								attributeEntry="${itemAttributes.extendedPrice}"
								property="document.item[${ctr}].extendedPrice"
								readOnly="${true}" /></td>
							<%--<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemRestrictedIndicator}" property="newPurchasingItemLine.itemRestrictedIndicator" /></td>--%>
							<td class="infoline"><kul:htmlControlAttribute
								attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}"
								property="newPurchasingItemLine.itemAssignedToTradeInIndicator"
								readOnly="${not fullEntryMode}" /></td>
							<c:if test="${fullEntryMode}">
								<td class="infoline">
								<div align="center"><html:image
									property="methodToCall.deleteItem.line${ctr}"
									src="images/tinybutton-delete1.gif" alt="Delete Item ${ctr+1}"
									title="Delete Item ${ctr+1}" styleClass="tinybutton" /></div>
								</td>
							</c:if>
							<c:if test="${(not fullEntryMode)}">
								<td class="infoline">
								<div align="center">&nbsp;</div>
								</td>
							</c:if>
						</tr>

						<tr>
							<td width="100%" colspan="12"><purap:puraccountingLines
								editingMode="${KualiForm.editingMode}"
								editableAccounts="${KualiForm.editableAccounts}"
								sourceAccountingLinesOnly="true"
								optionalFields="accountLinePercent"
								extraHiddenFields=",accountIdentifier,itemIdentifier"
								accountingLineAttributes="${accountingLineAttributes}"
								accountPrefix="document.item[${ctr}]." hideTotalLine="true"
								hideFields="amount" accountingAddLineIndex="${ctr}" /></td>
						</tr>
					</table>
			</c:if>
	</div>
	</td>
	</tr>

	</logic:iterate>
	<purap:miscitems itemAttributes="${itemAttributes}"
		accountingLineAttributes="${accountingLineAttributes}" />

	<!-- BEGIN TOTAL SECTION -->
	<tr>
		<th height=30 colspan="12">&nbsp;
		</td>
	</tr>

	<tr>
		<td colspan="12" class="subhead"><span class="subhead-left">Totals</span>
		<span class="subhead-right">&nbsp;</span></td>
	</tr>

	<tr>
		<th align=right width='75%' colspan=8 scope="row"><b>TOTAL:</b></th>
		<td align=left valign=middle colspan=4 class="datacell"><b>$${KualiForm.document.totalDollarAmount}</b>
		<td>
	</tr>

	<c:if
		test="${KualiForm.document.documentHeader.workflowDocument.documentType == 'KualiRequisitionDocument'}">
		<tr>
			<th align=right width='75%' colspan=8 scope="row">APO Limit:</th>
			<td align=left valign=middle colspan=4 class="datacell">
			$${KualiForm.document.organizationAutomaticPurchaseOrderLimit}
			<td><html:hidden
				property="document.organizationAutomaticPurchaseOrderLimit" />
		</tr>
	</c:if>
	<!-- END TOTAL SECTION -->

	</table>

	</div>
</kul:tab>
