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

<c:if test="${KualiForm.coverSheetPrintingAllowed}">
   <div align="center">
        <a href="financialCashReceipt.do?methodToCall=printCoverSheet&${PropertyConstants.DOCUMENT_NUMBER}=${KualiForm.document.documentNumber}" target="pdf_window">
            <font color="red"><bean:message key="label.document.cashReceipt.printCoverSheet"/></font>
        </a>
        <html:img src="${ConfigProperties.externalizable.images.url}icon-pdf.png" alt="print cover sheet" title="print cover sheet" width="16" height="16"/>
   </div>
   <br>
</c:if>
