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

<kul:page headerTitle="Customer Invoice Writeoff Summary" transactionalDocument="false" showDocumentInfo="false" htmlFormAction="arCustomerInvoiceWriteoffLookupSummary" docTitle="Customer Invoice Writeoff Summary">
		
	<ar:customerInvoiceWriteoffSummaryResults customerInvoiceDocumentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}"/>
	
	<kul:panelFooter />

	<div id="globalbuttons" class="globalbuttons">
		<c:if test="${KualiForm.sentToBatch}"> 
		<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_return.gif" styleClass="globalbuttons" property="methodToCall.cancel" title="claim" alt="claim"/>
		</c:if>
		<c:if test="${!KualiForm.sentToBatch}">
		<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_create.gif" styleClass="globalbuttons" property="methodToCall.createCustomerInvoiceWriteoffs" title="claim" alt="claim"/>
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="globalbuttons" property="methodToCall.cancel" title="cancel" alt="cancel"/>
		</c:if>
	</div>
</kul:page>
