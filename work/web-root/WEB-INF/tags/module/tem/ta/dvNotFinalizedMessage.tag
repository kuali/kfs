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

<c:set var="DVDocNumbersNotFinalized" value="${KualiForm.DVDocNumbersNotFinalized}" />
<c:if test="${fn:length(DVDocNumbersNotFinalized) > 0}">
	${kfunc:registerEditableProperty(KualiForm, "methodToCall")}
    <div align="left">
   		<c:forEach items="${DVDocNumbersNotFinalized}" var="DVdocNumber">
   			<font color="red">The disbursement voucher was not finalized, complete the document <a target="_blank" href='financialDisbursementVoucher.do?methodToCall=docHandler&docId=<c:out value="${DVdocNumber}" />&command=displayDocSearchView#topOfForm"'>#<c:out value="${DVdocNumber}" /></a> manually and submit. Or, you may retrieve the document from the action list.</font>
   		</c:forEach>
    </div>
    <br>
</c:if>
