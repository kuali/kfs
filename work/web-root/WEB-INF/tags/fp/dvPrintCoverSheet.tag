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

<c:if test="${KualiForm.canPrintCoverSheet}">
	${kfunc:registerEditableProperty(KualiForm, "methodToCall")}
   <div align="center">
        <a href='financialDisbursementVoucher.do?methodToCall=printDisbursementVoucherCoverSheet&<c:out value="${PropertyConstants.DOCUMENT_NUMBER}"/>=<c:out value="${KualiForm.document.documentNumber}"/>'>
            <font color="red"><bean:message key="label.document.disbursementVoucher.printCoverSheet"/></font>
        </a>
        <html:img src="${ConfigProperties.externalizable.images.url}icon-pdf.png" title="print cover sheet" alt="print cover sheet" width="16" height="16"/>
   </div>
   <br>
</c:if>
