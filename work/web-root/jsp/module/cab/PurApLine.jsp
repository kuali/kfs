<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<script>
<c:if test="${!empty KualiForm.documentNumber}">
	var popUpurl = '${KualiForm.docHandlerForwardLink}';
	window.open(popUpurl, "${KualiForm.documentNumber}");
</c:if>
</script>

<kul:page showDocumentInfo="false" htmlFormAction="cabPurApLine" renderMultipart="true"
	showTabButtons="true" docTitle="Purchasing / Accounts Payable Transactions" 
	transactionalDocument="false" headerDispatch="true" headerTabActive="true"
	sessionDocument="false" headerMenuBar="" feedbackKey="true" defaultMethodToCall="refresh" >
	<kul:tabTop tabTitle="Purchase Order Processing" defaultOpen="true">
		<div class="tab-container" align=center>
			<c:set var="cabPurApDocumentAttributes"	value="${DataDictionary.PurchasingAccountsPayableDocument.attributes}" />
			<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
				<tr>
        			<td colspan="2" class="subhead">Purchase Order Processing</td>
   				</tr>
   				<tr>
   					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${cabPurApDocumentAttributes.purchaseOrderIdentifier}" readOnly="true" /></th>
        			<td class="grid" width="75%">
        				<c:choose>
        				<c:when test="${!empty KualiForm.purchaseOrderInquiryUrl }">
							<a href="${ConfigProperties.application.url}/${KualiForm.purchaseOrderInquiryUrl }" target="_blank"> 
							${KualiForm.purchaseOrderIdentifier}							
							</a>
						</c:when>
						<c:otherwise>
							${KualiForm.purchaseOrderIdentifier}&nbsp;
						</c:otherwise>
						</c:choose>
        			</td>								
    			</tr>
    			<tr>
   					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${cabPurApDocumentAttributes.purApContactEmailAddress}" readOnly="true" /></th>
        			<td class="grid" width="75%">${KualiForm.purApContactEmailAddress}</td>								
    			</tr>
    			<tr>
   					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${cabPurApDocumentAttributes.purApContactPhoneNumber}" readOnly="true" /></th>
        			<td class="grid" width="75%">${KualiForm.purApContactPhoneNumber}</td>
        		</tr>
    		</table>
		</div>
	</kul:tabTop>
	
	<c:set var="readOnly" value="true" />
	<c:forEach items="${KualiForm.purApDocs}" var="purApDoc" >
		<c:forEach items="${purApDoc.purchasingAccountsPayableItemAssets}" var="assetLine" >
		<c:if test="${assetLine.active}" >
			<c:set var="readOnly" value="false" />
		</c:if>
		</c:forEach>
	</c:forEach>
	
	<cab:purApItemLines activeIndicator="true" title="Active Line Items" defaultOpen="true" tabErrorKey="purApDocs*,merge*" readOnly="${readOnly}"/>
	<cab:purApItemLines activeIndicator="false" title="Submitted Line Items" defaultOpen="false"/>
	<kul:panelFooter />
	
	
	<div id="globalbuttons" class="globalbuttons">
        <c:if test="${not readOnly}">
	        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_save.gif" styleClass="globalbuttons" 
	        	property="methodToCall.save" title="save" alt="save"/>
	        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" 
	        	property="methodToCall.close" title="close" alt="close"/>
        </c:if>
	    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_reload.gif" styleClass="globalbuttons" property="methodToCall.reload" title="reload" alt="reload"/>
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="globalbuttons" property="methodToCall.cancel" title="Cancel" alt="Cancel"/>		
    </div>
</kul:page>
