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

<%@ attribute name="displayRequisitionFields" required="false" description="Boolean to indicate if REQ specific fields should be displayed"%>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's accounting line fields."%>
<%@ attribute name="extraHiddenItemFields" required="false"
              description="A comma seperated list of names to be added to the list of normally hidden fields
              for the existing misc items." %>

<script language="JavaScript" type="text/javascript" src="dwr/interface/CommodityCodeService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/vendor/objectInfo.js"></script>

<c:set var="amendmentEntry"	value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />
<c:set var="amendmentEntryWithUnpaidPreqOrCM" value="${(amendmentEntry && (KualiForm.document.containsUnpaidPaymentRequestsOrCreditMemos))}" />
<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentType}" />

<c:choose>
    <c:when test= "${fn:contains(documentType, 'PurchaseOrder')}">
        <c:set var="isATypeOfPODoc" value="true" />
        <c:set var="acctExtraHiddenFields" value=",accountIdentifier,itemIdentifier,amount,itemAccountOutstandingEncumbranceAmount" />
    </c:when>
    <c:otherwise>
        <c:set var="isATypeOfPODoc" value="false" />
        <c:set var="acctExtraHiddenFields" value=",accountIdentifier,itemIdentifier,amount" />
    </c:otherwise>
</c:choose>

<c:choose>
	<c:when test= "${fn:contains(documentType, 'PurchaseOrder') or fn:contains(documentType, 'Requisition')}">
        <c:set var="isATypeofPurDoc" value="true" />
    </c:when>
    <c:otherwise>
        <c:set var="isATypeofPurDoc" value="false" />
    </c:otherwise>
</c:choose>

<kul:tab tabTitle="Items" defaultOpen="true" tabErrorKey="${PurapConstants.ITEM_TAB_ERRORS}">
	<div class="tab-container" align=center>
    <!--  if (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator)), then display the addLine -->
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
		<c:if test="${(fullEntryMode or amendmentEntry)}">
			<tr>
				<td colspan="6" class="subhead"><span class="subhead-left">Add Item</span></td>
				<td colspan="5" class="subhead" align="right" nowrap="nowrap" style="border-left: none;">
					<SCRIPT type="text/javascript">
                		<!--
                  		function hideImport() {
                      		document.getElementById("showLink").style.display="inline";
                      		document.getElementById("uploadDiv").style.display="none";
                  		}
                  		function showImport() {
                      		document.getElementById("showLink").style.display="none";
                      		document.getElementById("uploadDiv").style.display="inline";
                  		}
                  		document.write(
                    		'<a id="showLink" href="#" onclick="showImport();return false;">' +
                      		'<img src="${ConfigProperties.externalizable.images.url}tinybutton-importlines.gif" title="import items from file" alt="import items from file"' +
                      		'     width=72 height=15 border=0 align="right" class="det-button">' +
                    		'<\/a>' +
                    		'<div id="uploadDiv" style="display:none;" >' +
                      		'<html:file size="30" property="itemImportFile" />' +
                      		'<html:image property="methodToCall.importItems" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
                                    styleClass="tinybutton" alt="add imported items" title="add imported items" />' +
                      		'<html:image property="methodToCall.cancel" src="${ConfigProperties.externalizable.images.url}tinybutton-cancelimport.gif"
                                    styleClass="tinybutton" alt="cancel import" title="cancel import" onclick="hideImport();return false;" />' +
                    		'<\/div>');
                		//-->
            		</SCRIPT>
					<NOSCRIPT>
						Import lines
						<html:file size="30" property="itemImportFile" style="font:10px;height:16px;" />
						<html:image property="methodToCall.importItems" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add imported items" title="add imported items" />
					</NOSCRIPT>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTypeCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.commodityCode}" nowrap="true" />
				<kul:htmlAttributeHeaderCell> * <kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemDescription}" /></kul:htmlAttributeHeaderCell>
				<kul:htmlAttributeHeaderCell nowrap="true"> * <kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemUnitPrice}" /></kul:htmlAttributeHeaderCell>				
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" nowrap="true" />
				<c:if test="${displayRequisitionFields}">
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemRestrictedIndicator}" nowrap="true" />
				</c:if>
				<!--  kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" / -->
				<!-- TODO: PHASE 2B -->
				<kul:htmlAttributeHeaderCell literalLabel="Actions" colspan="2" nowrap="true"/>
			</tr>
			<tr>
                <td class="infoline">
                    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemLineNumber}" property="newPurchasingItemLine.itemLineNumber" readOnly="${true}"/>
                </td>
				<td class="infoline">
				    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemTypeCode}" property="newPurchasingItemLine.itemTypeCode" />
			    </td>
				<td class="infoline">
				    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemQuantity}" property="newPurchasingItemLine.itemQuantity" />
			    </td>
				<td class="infoline">
				    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" property="newPurchasingItemLine.itemUnitOfMeasureCode" />
			    </td>
				<td class="infoline">
				    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemCatalogNumber}" property="newPurchasingItemLine.itemCatalogNumber" />
			    </td>
                <td class="infoline">
                    <c:set var="commodityCodeField"  value="newPurchasingItemLine.purchasingCommodityCode" />
                    <c:set var="commodityDescriptionField"  value="newPurchasingItemLine.commodityCode.commodityDescription" />
                    <kul:htmlControlAttribute attributeEntry="${itemAttributes.commodityCode}" 
                        property="${commodityCodeField}" 
                        onblur="loadCommodityCodeInfo( '${commodityCodeField}', '${commodityDescriptionField}' );${onblur}" readOnly="${readOnly}" />
                    <kul:lookup boClassName="org.kuali.module.vendor.bo.CommodityCode" 
                            fieldConversions="purchasingCommodityCode:newPurchasingItemLine.purchasingCommodityCode"
                            lookupParameters="'Y':active"/>   
                            
                    <div id="newPurchasingItemLine.commodityCode.commodityDescription.div" class="fineprint">
                        <html:hidden write="true" property="${commodityDescriptionField}"/>&nbsp;        
                    </div>         
                </td>			    
				<td class="infoline">
				   <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="newPurchasingItemLine.itemDescription" />
			    </td>
				<td class="infoline">
				    <div align="right">
				        <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitPrice}" property="newPurchasingItemLine.itemUnitPrice" />
					</div>
				</td>
				<td class="infoline">
 				    <div align="right">
 				        <kul:htmlControlAttribute attributeEntry="${itemAttributes.extendedPrice}" property="newPurchasingItemLine.extendedPrice" readOnly="true" />
					</div>
				</td>
				<c:if test="${displayRequisitionFields}">
					<td class="infoline">
  					    <div align="center">
  					        <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemRestrictedIndicator}" property="newPurchasingItemLine.itemRestrictedIndicator" />
						</div>
					</td>
				</c:if>
				<!-- TODO: PHASE 2B -->
				<!-- td class="infoline"><div align="center"><kul:htmlControlAttribute
					attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}"
					property="newPurchasingItemLine.itemAssignedToTradeInIndicator" /></div></td -->
				<td class="infoline" colspan="2">
				    <div align="center">
				        <html:image property="methodToCall.addItem" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert an Item" title="Add an Item" styleClass="tinybutton" />
				    </div>
				</td>
				
				<html:hidden property="newPurchasingItemLine.documentNumber" value="${KualiForm.document.documentNumber}" />
			</tr>
		</c:if>
		<!-- End of if (fullEntryMode or amendmentEntry), then display the addLine -->


		<tr>
			<th height=30 colspan="11">
			    <purap:accountdistribution accountingLineAttributes="${accountingLineAttributes}" 
			        itemAttributes="${itemAttributes}"/>
		    </th>
		</tr>


		<!-- what is the purpose of this c:if? would it be better to still dipslay the section header with message that there are not items -->
		<tr>
			<td colspan="11" class="subhead">
			    <span class="subhead-left">Current Items</span>
			</td>
		</tr>

		<c:if test="${fn:length(KualiForm.document.items) > fn:length(KualiForm.document.belowTheLineTypes)}">
				<!-- TODO: PHASE 2B -->
				<c:if test="${isATypeOfPODoc}">
                    <c:if test="${((documentType != 'PurchaseOrderDocument') && !(fullEntryMode or amendmentEntry))}">
                        <kul:htmlAttributeHeaderCell literalLabel="Inactive"/>
                    </c:if>
                    <kul:htmlAttributeHeaderCell literalLabel="Amount Paid" />
                </c:if>
			</tr>
		</c:if>

		<c:if test="${!(fn:length(KualiForm.document.items) > fn:length(KualiForm.document.belowTheLineTypes))}">
			<tr>
				<th height=30 colspan="11">No items added to document</th>
			</tr>
		</c:if>

		<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
			<c:if test="${itemLine.itemType.itemTypeAboveTheLineIndicator == true}">
				<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
				<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
				
				<c:choose>
 				    <c:when test="${itemLine.objectId == null}">
 				        <c:set var="newObjectId" value="<%= (new org.kuali.core.util.Guid()).toString()%>" />
                        <c:set var="tabKey" value="Item-${newObjectId}" />
                        <html:hidden property="document.item[${ctr}].objectId" value="${newObjectId}" />
				    </c:when>
				    <c:when test="${itemLine.objectId != null}">
				        <c:set var="tabKey" value="Item-${itemLine.objectId}" />
				        <html:hidden property="document.item[${ctr}].objectId" /> 
				    </c:when>
				</c:choose>
				
                <!--  hit form method to increment tab index -->
                <c:set var="dummyIncrementer" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />

                <c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>

				<%-- default to closed --%>
				<c:choose>
					<c:when test="${empty currentTab}">
						<c:set var="isOpen" value="true" />
					</c:when>
					<c:when test="${!empty currentTab}">
						<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
					</c:when>
				</c:choose>

				<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

				<tr>
					<td colspan="11" class="tab-subhead" style="border-right: none;">
					    Item ${ctr+1}
					</td>
				</tr>

				<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
					<tbody style="display: none;" id="tab-${tabKey}-div">
				</c:if>
				<!-- table class="datatable" style="width: 100%;" -->

				<tr>
					<td class="infoline" nowrap="nowrap" rowspan="2">
					    <html:hidden property="document.item[${ctr}].itemIdentifier" /> 
					    <html:hidden property="document.item[${ctr}].purapDocumentIdentifier" />
					    <html:hidden property="document.item[${ctr}].versionNumber" /> 
						<%-- make sure itemTypeCode changes are populated in case refresh behind the scenes doesn't occur --%>					    
					    <html:hidden property="document.item[${ctr}].itemType.itemTypeCode" /> 
					    <html:hidden property="document.item[${ctr}].itemType.itemTypeDescription" />

					    <c:if test="${isATypeOfPODoc}">
						    <html:hidden property="document.item[${ctr}].itemActiveIndicator" />
                            <html:hidden property="document.item[${ctr}].itemInvoicedTotalQuantity" />
                            <html:hidden property="document.item[${ctr}].itemInvoicedTotalAmount" />
                            <html:hidden property="document.item[${ctr}].itemReceivedTotalQuantity" />
                            <html:hidden property="document.item[${ctr}].itemDamagedTotalQuantity" />
                            <html:hidden property="document.item[${ctr}].itemOutstandingEncumberedQuantity" />
                            <html:hidden property="document.item[${ctr}].itemOutstandingEncumberedAmount" />
  					    </c:if> 
					    <html:hidden property="document.item[${ctr}].itemType.active" />
					    <html:hidden property="document.item[${ctr}].itemType.quantityBasedGeneralLedgerIndicator" />
					    <html:hidden property="document.item[${ctr}].itemType.itemTypeAboveTheLineIndicator" />
						<c:forTokens var="hiddenField" items="${extraHiddenItemFields}" delims=",">
 							<html:hidden property="document.item[${ctr}].${hiddenField}" />
 						</c:forTokens>		    
					    &nbsp;<b><html:hidden write="true" property="document.item[${ctr}].itemLineNumber" /></b>&nbsp; 
					    <c:if test="${fullEntryMode}">
						    <html:image property="methodToCall.upItem.line${ctr}"
							    src="${ConfigProperties.externalizable.images.url}purap-up.gif"
							    alt="Move Item Up" title="Move Item Up" styleClass="tinybutton" />
						    <html:image property="methodToCall.downItem.line${ctr}"
							    src="${ConfigProperties.externalizable.images.url}purap-down.gif"
							    alt="Move Item Down" title="Move Item Down"
							    styleClass="tinybutton" />
					    </c:if>
					</td>
					<td class="infoline">
					    <kul:htmlControlAttribute
						    attributeEntry="${itemAttributes.itemTypeCode}"
						    property="document.item[${ctr}].itemTypeCode"
						    extraReadOnlyProperty="document.item[${ctr}].itemType.itemTypeDescription"
						    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.versionNumber == null))}" />
					</td>

					<td class="infoline">
					    <kul:htmlControlAttribute
						    attributeEntry="${itemAttributes.itemQuantity}"
						    property="document.item[${ctr}].itemQuantity"
						    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null))))}" />
					</td>
					<td class="infoline">
					    <kul:htmlControlAttribute
						    attributeEntry="${itemAttributes.itemUnitOfMeasureCode}"
						    property="document.item[${ctr}].itemUnitOfMeasureCode"
						    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null))))}" />
				    </td>
					<td class="infoline">
					    <kul:htmlControlAttribute
						    attributeEntry="${itemAttributes.itemCatalogNumber}"
						    property="document.item[${ctr}].itemCatalogNumber"
						    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null))))}" />
				    </td>
                    <td class="infoline">
                        <kul:htmlControlAttribute 
                            attributeEntry="${itemAttributes.commodityCode}" 
                            property="document.item[${ctr}].purchasingCommodityCode"
                            onblur="loadCommodityCodeInfo( 'document.item[${ctr}].purchasingCommodityCode', 'document.item[${ctr}].commodityCode.commodityDescription' );${onblur}"
                            readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null))))}"/>
                        <c:if test="${fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator)}">   
                            <kul:lookup boClassName="org.kuali.module.vendor.bo.CommodityCode" 
                                fieldConversions="purchasingCommodityCode:document.item[${ctr}].purchasingCommodityCode"
                                lookupParameters="'Y':active"/>    
                        </c:if>
                        <div id="document.item[${ctr}].commodityCode.commodityDescription.div" class="fineprint">
                            <html:hidden write="true" property="document.item[${ctr}].commodityCode.commodityDescription"/>&nbsp;  
                        </div>
                    </td>				    
					<td class="infoline">
						 <kul:htmlControlAttribute
						    attributeEntry="${itemAttributes.itemDescription}"
						    property="document.item[${ctr}].itemDescription"
						    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null))))}" />
					</td>
					<td class="infoline">
					    <div align="right">
					        <kul:htmlControlAttribute
						        attributeEntry="${itemAttributes.itemUnitPrice}"
						        property="document.item[${ctr}].itemUnitPrice"
						        readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null))))}" />
						</div>
					</td>
					<td class="infoline">
					    <div align="right">
					        <kul:htmlControlAttribute
						        attributeEntry="${itemAttributes.extendedPrice}"
						        property="document.item[${ctr}].extendedPrice" readOnly="${true}" />
					    </div>
					</td>
					<c:if test="${displayRequisitionFields}">
						<td class="infoline">
						<div align="center">
						    <kul:htmlControlAttribute
							    attributeEntry="${itemAttributes.itemRestrictedIndicator}"
							    property="document.item[${ctr}].itemRestrictedIndicator"
							    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null))))}" />
					    </div>
						</td>
					</c:if>
					<!-- TODO: PHASE 2B -->
					<!-- td class="infoline">
                            <div align="center">
                                <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" property="newPurchasingItemLine.itemAssignedToTradeInIndicator" readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator))}" />
                            </div>
                        </td -->
					<c:if test="${(fullEntryMode or (amendmentEntry and itemLine.itemInvoicedTotalAmount == null))}">
						<td class="infoline" rowspan="2">
						    <div align="center">
						        <html:image
							        property="methodToCall.deleteItem.line${ctr}"
							        src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
							        alt="Delete Item ${ctr+1}" title="Delete Item ${ctr+1}"
							        styleClass="tinybutton" />
							</div>
						</td>
					</c:if>
					<c:if test="${amendmentEntry}">
					    <c:choose>
					        <c:when test="${(itemLine.canInactivateItem and itemLine.itemInvoicedTotalAmount != null)}">
						<td class="infoline" rowspan="2">
						    <div align="center">
						        <html:image
							        property="methodToCall.inactivateItem.line${ctr}"
						    	    src="${ConfigProperties.externalizable.images.url}tinybutton-inactivate.gif"
							        alt="Inactivate Item ${ctr+1}" title="Inactivate Item ${ctr+1}"
							        styleClass="tinybutton" />
					        </div>
					    </td>
					        </c:when>
					        <c:otherwise>
					            <c:if test="${(itemLine.itemInvoicedTotalAmount != null and itemLine.itemActiveIndicator)}">
					                <td class="infoline" rowspan="2">&nbsp;</td>
								</c:if>
					        </c:otherwise>
					    </c:choose>
					</c:if>
					<c:choose>
					    <c:when test="${(isATypeOfPODoc and ! itemLine.itemActiveIndicator)}">
						    <td class="infoline" rowspan="2">
						        <div align="center">Inactive</div>
						    </td>
					    </c:when>
					    <c:otherwise>
                            <c:if test="${(not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator)))}">
						        <td class="infoline" rowspan="2">
						            <div align="center">&nbsp;</div>
						        </td>
					        </c:if>
					    </c:otherwise>
					</c:choose>
					<c:if test="${isATypeOfPODoc}">
					    <td class="infoline" rowspan="2">
					        <div align="right">
					            <kul:htmlControlAttribute
						            attributeEntry="${itemAttributes.itemInvoicedTotalAmount}"
						            property="document.item[${ctr}].itemInvoicedTotalAmount" readOnly="${true}" />
					        </div>
					    </td>
					</c:if>
				</tr>

                <c:if test="${amendmentEntry}">
                    <c:choose>
	    			    <c:when test="${(itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null)) )}">
				    <c:set target="${KualiForm.accountingLineEditingMode}" property="fullEntry" value="true" />
					<purap:purapGeneralAccounting
						editingMode="${KualiForm.accountingLineEditingMode}"
						editableAccounts="${KualiForm.editableAccounts}"
						sourceAccountingLinesOnly="true"
						optionalFields="accountLinePercent"
						extraHiddenFields=",accountIdentifier,itemIdentifier,amount,itemAccountOutstandingEncumbranceAmount"
						accountingLineAttributes="${accountingLineAttributes}"
						accountPrefix="document.item[${ctr}]." hideTotalLine="true"
						hideFields="amount" accountingAddLineIndex="${ctr}"
						itemsAttributes="${itemAttributes}" ctr="${ctr}" />
    				    </c:when>
	        			<c:otherwise>
				    <c:set target="${KualiForm.editingMode}" property="viewOnly" value="true" />
					<purap:purapGeneralAccounting editingMode="${KualiForm.editingMode}"
						editableAccounts="${KualiForm.editableAccounts}"
						sourceAccountingLinesOnly="true"
						optionalFields="accountLinePercent"
						extraHiddenFields="${acctExtraHiddenFields}"
						accountingLineAttributes="${accountingLineAttributes}"
						accountPrefix="document.item[${ctr}]." hideTotalLine="true"
						hideFields="amount" accountingAddLineIndex="${ctr}"
						itemsAttributes="${itemAttributes}" ctr="${ctr}" />
				        </c:otherwise>
				    </c:choose>
				</c:if>
				<c:if test="${(!amendmentEntry)}">
					<c:if test="${!empty KualiForm.editingMode['allowItemEntry'] && (KualiForm.editingMode['allowItemEntry'] == itemLine.itemIdentifier)}" >
					    <c:set target="${KualiForm.editingMode}" property="expenseEntry" value="true" />
					</c:if>
					<purap:purapGeneralAccounting editingMode="${KualiForm.editingMode}"
						editableAccounts="${KualiForm.editableAccounts}"
						sourceAccountingLinesOnly="true"
						optionalFields="accountLinePercent"
			    		extraHiddenFields="${acctExtraHiddenFields}"
						accountingLineAttributes="${accountingLineAttributes}"
						accountPrefix="document.item[${ctr}]." hideTotalLine="true"
						hideFields="amount" accountingAddLineIndex="${ctr}"
						itemsAttributes="${itemAttributes}" ctr="${ctr}" />
				</c:if>

				<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
					</tbody>
				</c:if>
			</c:if>
		</logic:iterate>

		<tr>
			<th height=30 colspan="11">&nbsp;</th>
		</tr>

		<purap:miscitems itemAttributes="${itemAttributes}" accountingLineAttributes="${accountingLineAttributes}" extraHiddenItemFields="${extraHiddenItemFields}" descriptionFirst="${isATypeofPurDoc}"/>

		<!-- BEGIN TOTAL SECTION -->
		<tr>
			<th height=30 colspan="11">&nbsp;</th>
		</tr>

		<tr>
			<td colspan="11" class="subhead">
                <span class="subhead-left">Totals</span>
                <span class="subhead-right">&nbsp;</span>
            </td>
		</tr>

		<tr>
			<th align=right width='75%' colspan=7 scope="row">
			    <div align="right">
			        <kul:htmlAttributeLabel attributeEntry="${DataDictionary.RequisitionDocument.attributes.totalDollarAmount}" />
			    </div>
			</th>
			<td valign=middle class="datacell">
			    <div align="right"><b>
                    <kul:htmlControlAttribute
                        attributeEntry="${DataDictionary.RequisitionDocument.totalDollarAmount}"
                        property="document.totalDollarAmount"
                        readOnly="true" />&nbsp; </b>
                </div>
                <html:hidden property="document.totalDollarAmount" />
			</td>
			<td colspan=3 class="datacell">&nbsp;</td>
		</tr>

		<tr>
			<th align=right width='75%' colspan=7 scope="row">
			    <c:if test="${displayRequisitionFields}">
				    <div align="right">
				        <kul:htmlAttributeLabel attributeEntry="${DataDictionary.RequisitionDocument.attributes.organizationAutomaticPurchaseOrderLimit}" />
					</div>
			    </c:if> 
			    <c:if test="${!displayRequisitionFields}"> 
                    <div align="right">
                        <kul:htmlAttributeLabel attributeEntry="${DataDictionary.PurchaseOrderDocument.attributes.internalPurchasingLimit}" />
                    </div>
                </c:if>
            </th>
			<td align=right valign=middle class="datacell">
			    <c:if test="${displayRequisitionFields}">
				    <div align="right">
				        <kul:htmlControlAttribute
					        attributeEntry="${DataDictionary.RequisitionDocument.attributes.organizationAutomaticPurchaseOrderLimit}"
					        property="document.organizationAutomaticPurchaseOrderLimit"
					        readOnly="true" />&nbsp;
					</div>
				    <html:hidden property="document.organizationAutomaticPurchaseOrderLimit" />
			    </c:if> 
			    <c:if test="${!displayRequisitionFields}">
                    <div align="right">
                        <kul:htmlControlAttribute
                            attributeEntry="${DataDictionary.PurchaseOrderDocument.attributes.internalPurchasingLimit}"
                            property="document.internalPurchasingLimit"
                            readOnly="true" />&nbsp;
                    </div>
                    <html:hidden property="document.internalPurchasingLimit" />
			    </c:if>
			</td>
			<td colspan="3" class="datacell">&nbsp;</td>
		</tr>
		<!-- END TOTAL SECTION -->

	</table>

	</div>
</kul:tab>
