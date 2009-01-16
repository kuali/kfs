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

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:set var="amendmentEntry"	value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />
<c:set var="lockB2BEntry" value="${(not empty KualiForm.editingMode['lockB2BEntry'])}" />
<c:set var="unorderedItemAccountEntry"	value="${(not empty KualiForm.editingMode['unorderedItemAccountEntry'])}" />
<c:set var="amendmentEntryWithUnpaidPreqOrCM" value="${(amendmentEntry && (KualiForm.document.containsUnpaidPaymentRequestsOrCreditMemos))}" />
<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentType}" />
<c:set var="lockTaxAmountEntry" value="${(not empty KualiForm.editingMode['lockTaxAmountEntry']) || !fullEntryMode}" />
<c:set var="purapTaxEnabled" value="${(not empty KualiForm.editingMode['purapTaxEnabled'])}" />

<c:set var="mainColumnCount" value="12"/>
<c:if test="${purapTaxEnabled}">
	<c:set var="mainColumnCount" value="14"/>
</c:if>

<c:choose>
<c:when test="${displayRequisitionFields}">
	<c:set var="colSpanItemType" value="4"/>
	<c:set var="colSpanDescription" value="5"/>
	<c:set var="colSpanExtendedPrice" value="5"/>
</c:when>
<c:otherwise>
	<c:set var="colSpanItemType" value="4"/>
	<c:set var="colSpanDescription" value="5"/>
	<c:set var="colSpanExtendedPrice" value="5"/>
</c:otherwise>
</c:choose>

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
	<c:if test="${!KualiForm.document.inquiryRendered}">
	    <div align="left">
	        Object Code and Sub-Object Code inquiries and descriptions have been removed because this is a prior year document.
        </div>
        <br>
    </c:if>
    <!--  if (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator)), then display the addLine -->
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
		<c:if test="${(fullEntryMode or amendmentEntry) and !lockB2BEntry}">
			<tr>
				<td colspan="9" class="subhead">
					<span class="subhead-left">Add Item <kul:help resourceKey="${PurapConstants.LINE_ITEM_IMPORT_HELP_PARAMETER}" altText="Add Items Help"/></span>
				</td>
				<td colspan="6" class="subhead" align="right" nowrap="nowrap" style="border-left: none;">
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
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}"/>
				<kul:htmlAttributeHeaderCell><kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" useShortLabel="true"/></kul:htmlAttributeHeaderCell>
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.purchasingCommodityCode}" nowrap="true" />
				<kul:htmlAttributeHeaderCell> * <kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemDescription}" useShortLabel="true"/></kul:htmlAttributeHeaderCell>
				<kul:htmlAttributeHeaderCell nowrap="true"> * <kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemUnitPrice}" useShortLabel="true"/></kul:htmlAttributeHeaderCell>				
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" nowrap="true" />

				<c:if test="${purapTaxEnabled}">
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTaxAmount}" nowrap="true" />				
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.totalAmount}" nowrap="true" />
				</c:if>

				<c:if test="${displayRequisitionFields}">
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemRestrictedIndicator}" nowrap="true" />
				</c:if>
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" nowrap="true"/>
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
                    <kul:htmlControlAttribute attributeEntry="${itemAttributes.purchasingCommodityCode}" 
                        property="${commodityCodeField}" 
                        onblur="loadCommodityCodeInfo( '${commodityCodeField}', '${commodityDescriptionField}' );${onblur}" readOnly="${readOnly}" />
                    <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.CommodityCode" 
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
				
				<c:if test="${purapTaxEnabled}">
				<td class="infoline">
 				    <div align="right">
 				        <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemTaxAmount}" property="newPurchasingItemLine.itemTaxAmount" readOnly="true" />
					</div>
				</td>				
				<td class="infoline">
 				    <div align="right">
 				        <kul:htmlControlAttribute attributeEntry="${itemAttributes.totalAmount}" property="newPurchasingItemLine.totalAmount" readOnly="true" />
					</div>
				</td>
				</c:if>

				<c:if test="${displayRequisitionFields}">
					<td class="infoline">
  					    <div align="center">
  					        <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemRestrictedIndicator}" property="newPurchasingItemLine.itemRestrictedIndicator" />
						</div>
					</td>
				</c:if>
			    <td class="infoline">
  					<div align="center">
  		                <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" property="newPurchasingItemLine.itemAssignedToTradeInIndicator" />
					</div>
				</td>				
				<td class="infoline" colspan="2">
				    <div align="center">
				        <html:image property="methodToCall.addItem" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert an Item" title="Add an Item" styleClass="tinybutton" />
				    </div>
				</td>
				
			</tr>
		</c:if>
		<!-- End of if (fullEntryMode or amendmentEntry), then display the addLine -->


		<tr>
			<th height=30 colspan="${mainColumnCount}">
			    <purap:accountdistribution accountingLineAttributes="${accountingLineAttributes}" 
			        itemAttributes="${itemAttributes}"/>
		    </th>
		</tr>


		<tr>
			<td colspan="${mainColumnCount}" class="subhead">
			    <span class="subhead-left">Current Items</span>
			</td>
		</tr>

        <c:if test="${!((!lockB2BEntry and !(fn:length(KualiForm.document.items) > fn:length(KualiForm.document.belowTheLineTypes))) or (lockB2BEntry and !(fn:length(KualiForm.document.items) > 0)))}">
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTypeCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.purchasingCommodityCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" />
				
				<c:if test="${purapTaxEnabled}">
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTaxAmount}" />				
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.totalAmount}" />
				</c:if>

				<c:if test="${displayRequisitionFields and !lockB2BEntry}">
					<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemRestrictedIndicator}" />
				</c:if>
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" />
				<!-- TODO: PHASE 2B -->
				<c:if test="${isATypeOfPODoc}">
				    <c:choose>
                        <c:when test="${((documentType != 'PurchaseOrderDocument') && !(fullEntryMode or amendmentEntry))}">
                            <kul:htmlAttributeHeaderCell literalLabel="Inactive"/>
                        </c:when>
                        <c:otherwise>
                            <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
                        </c:otherwise>
                    </c:choose>
                    <kul:htmlAttributeHeaderCell literalLabel="Amount Paid" />
                </c:if>
                <c:if test="${!isATypeOfPODoc}">
                    <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
                </c:if>
			</tr>
		</c:if>

		<c:if test="${(!lockB2BEntry and !(fn:length(KualiForm.document.items) > fn:length(KualiForm.document.belowTheLineTypes))) or (lockB2BEntry and !(fn:length(KualiForm.document.items) > 0))}">
			<tr>
				<th height=30 colspan="${mainColumnCount}">No items added to document</th>
			</tr>
		</c:if>

		<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
			<c:if test="${itemLine.itemType.lineItemIndicator == true}">
				<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
				<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
				
				<c:choose>
 				    <c:when test="${itemLine.objectId == null}">
 				        <c:set var="newObjectId" value="<%= (new org.kuali.rice.kns.util.Guid()).toString()%>" />
                        <c:set var="tabKey" value="Item-${newObjectId}" />
				    </c:when>
				    <c:when test="${itemLine.objectId != null}">
				        <c:set var="tabKey" value="Item-${itemLine.objectId}" />
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

				<tr>
					<td colspan="${mainColumnCount}" class="tab-subhead" style="border-right: none;">
					    Item ${ctr+1}
					</td>
				</tr>

				<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
					<tbody style="display: none;" id="tab-${tabKey}-div">
				</c:if>
				<!-- table class="datatable" style="width: 100%;" -->

				<tr>
					<td class="infoline" nowrap="nowrap" rowspan="2">	    
					    &nbsp;<b><bean:write name="KualiForm" property="document.item[${ctr}].itemLineNumber"/></b>&nbsp; 
					    <c:if test="${fullEntryMode and !lockB2BEntry}">
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
						    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.versionNumber == null)) or lockB2BEntry}" />
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
						    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null)))) or lockB2BEntry}" />
				    </td>
					<td class="infoline">
					    <kul:htmlControlAttribute
						    attributeEntry="${itemAttributes.itemCatalogNumber}"
						    property="document.item[${ctr}].itemCatalogNumber"
						    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null)))) or lockB2BEntry}" />
				    </td>
                    <td class="infoline">
                        <kul:htmlControlAttribute 
                            attributeEntry="${itemAttributes.purchasingCommodityCode}" 
                            property="document.item[${ctr}].purchasingCommodityCode"
                            onblur="loadCommodityCodeInfo( 'document.item[${ctr}].purchasingCommodityCode', 'document.item[${ctr}].commodityCode.commodityDescription' );${onblur}"
                            readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null))))}"/>
                        <c:if test="${fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator)}">   
                            <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.CommodityCode" 
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
						    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null)))) or lockB2BEntry}" />
					</td>
					<td class="infoline">
					    <div align="right">
					        <kul:htmlControlAttribute
						        attributeEntry="${itemAttributes.itemUnitPrice}"
						        property="document.item[${ctr}].itemUnitPrice"
						        readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null)))) or lockB2BEntry}" />
						</div>
					</td>
					<td class="infoline">
					    <div align="right">
					        <kul:htmlControlAttribute
						        attributeEntry="${itemAttributes.extendedPrice}"
						        property="document.item[${ctr}].extendedPrice" readOnly="${true}" />
					    </div>
					</td>

					<c:if test="${purapTaxEnabled}">
					<td class="infoline">
					    <div align="right">
					        <kul:htmlControlAttribute
						        attributeEntry="${itemAttributes.itemTaxAmount}"
						        property="document.item[${ctr}].itemTaxAmount" readOnly="${lockTaxAmountEntry}" />
					    </div>
					</td>					
					<td class="infoline">
					    <div align="right">
					        <kul:htmlControlAttribute
						        attributeEntry="${itemAttributes.totalAmount}"
						        property="document.item[${ctr}].totalAmount" readOnly="${true}" />
					    </div>
					</td>
					</c:if>

					<c:if test="${displayRequisitionFields and !lockB2BEntry}">
						<td class="infoline">
						<div align="center">
						    <kul:htmlControlAttribute
							    attributeEntry="${itemAttributes.itemRestrictedIndicator}"
							    property="document.item[${ctr}].itemRestrictedIndicator"
							    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null))))}" />
					    </div>
						</td>
					</c:if>
				    <td class="infoline">
                            <div align="center">
						    <kul:htmlControlAttribute
							    attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}"
							    property="document.item[${ctr}].itemAssignedToTradeInIndicator"
							    readOnly="${not (fullEntryMode or (amendmentEntry and itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null))))}" />
                            </div>
					</td>
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
				
				<c:set var="subtractor" value="3"/>
				<c:if test="${displayRequisitionFields and !lockB2BEntry}">
					<c:set var="subtractor" value="2"/>
				</c:if>

				<c:set var="columnCount" value="${mainColumnCount - subtractor}"/>
				<c:choose>
                <c:when test="${amendmentEntry}">                
                    <c:choose>
	    			    <c:when test="${ (itemLine.itemActiveIndicator and (not (amendmentEntryWithUnpaidPreqOrCM and itemLine.itemInvoicedTotalAmount != null)) )}">
				    <c:set target="${KualiForm.accountingLineEditingMode}" property="fullEntry" value="true" />
					<purap:purapGeneralAccounting
						accountPrefix="document.item[${ctr}]." 
						itemColSpan="${columnCount}" />
    				    </c:when>
	        			<c:otherwise>
				    <c:set target="${KualiForm.editingMode}" property="viewOnly" value="true" />
					<purap:purapGeneralAccounting 
						accountPrefix="document.item[${ctr}]." 
						itemColSpan="${columnCount}" />
				        </c:otherwise>
				    </c:choose>
				</c:when>
				<c:when test="${unorderedItemAccountEntry and itemLine.newUnorderedItem}">
				    <c:set target="${KualiForm.accountingLineEditingMode}" property="fullEntry" value="true" />
					<purap:purapGeneralAccounting
						accountPrefix="document.item[${ctr}]." 
						itemColSpan="${columnCount}" />				
				</c:when>
				<c:when test="${(!amendmentEntry)}">
					<c:if test="${!empty KualiForm.editingMode['allowItemEntry'] && (KualiForm.editingMode['allowItemEntry'] == itemLine.itemIdentifier)}" >
					    <c:set target="${KualiForm.editingMode}" property="expenseEntry" value="true" />
					</c:if>					
					<purap:purapGeneralAccounting 
						accountPrefix="document.item[${ctr}]." 
						itemColSpan="${columnCount}" />
				</c:when>
				</c:choose>
				
				<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
					</tbody>
				</c:if>
			</c:if>
		</logic:iterate>

		<c:if test="${!lockB2BEntry}">
		<tr>
			<th height=30 colspan="${mainColumnCount}">&nbsp;</th>
		</tr>

		<purap:miscitems itemAttributes="${itemAttributes}" accountingLineAttributes="${accountingLineAttributes}" descriptionFirst="${isATypeofPurDoc}" mainColumnCount="${mainColumnCount}" colSpanItemType="${colSpanItemType}" colSpanDescription="${colSpanDescription}" colSpanExtendedPrice="${colSpanExtendedPrice}" />
		</c:if>
		
		<!-- BEGIN TOTAL SECTION -->
		<tr>
			<th height=30 colspan="${mainColumnCount}">&nbsp;</th>
		</tr>

		<tr>
			<td colspan="${mainColumnCount}" class="subhead">
                <span class="subhead-left">Totals</span>
                <span class="subhead-right">&nbsp;</span>
            </td>
		</tr>

		<c:if test="${purapTaxEnabled}">
		<tr>
			<th align=right width='75%' colspan=9 scope="row">
			    <div align="right">
			        <kul:htmlAttributeLabel attributeEntry="${DataDictionary.RequisitionDocument.attributes.totalPreTaxDollarAmount}" />
			    </div>
			</th>
			<td valign=middle class="datacell">
			    <div align="right"><b>
                    <kul:htmlControlAttribute
                        attributeEntry="${DataDictionary.RequisitionDocument.totalPreTaxDollarAmount}"
                        property="document.totalPreTaxDollarAmount"
                        readOnly="true" />&nbsp; </b>
                </div>
			</td>
			<td colspan=6 class="datacell">&nbsp;</td>
		</tr>

		<tr>
			<th align=right width='75%' colspan=9 scope="row">
			    <div align="right">
			        <kul:htmlAttributeLabel attributeEntry="${DataDictionary.RequisitionDocument.attributes.totalTaxAmount}" />
			    </div>
			</th>
			<td valign=middle class="datacell">
			    <div align="right"><b>
                    <kul:htmlControlAttribute
                        attributeEntry="${DataDictionary.RequisitionDocument.totalTaxAmount}"
                        property="document.totalTaxAmount"
                        readOnly="true" />&nbsp; </b>
                </div>
			</td>
			<td colspan=6 class="datacell">&nbsp;</td>
		</tr>
		</c:if>

		<tr>
			<th align=right width='75%' colspan=9 scope="row">
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
			</td>
			<td colspan=6 class="datacell">&nbsp;</td>
		</tr>

		<tr>
			<th align=right width='75%' colspan=9 scope="row">
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
			    </c:if> 
			    <c:if test="${!displayRequisitionFields}">
                    <div align="right">
                        <kul:htmlControlAttribute
                            attributeEntry="${DataDictionary.PurchaseOrderDocument.attributes.internalPurchasingLimit}"
                            property="document.internalPurchasingLimit"
                            readOnly="true" />&nbsp;
                    </div>
			    </c:if>
			</td>
			<td colspan="6" class="datacell">&nbsp;</td>
		</tr>
		<!-- END TOTAL SECTION -->

	</table>

	</div>
</kul:tab>
