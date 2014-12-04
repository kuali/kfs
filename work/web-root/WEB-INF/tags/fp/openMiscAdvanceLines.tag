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

<%--
  the miscAdvanceLines.tag is only used to show misc advances that are being paid back - that is, they live in the "money out"
  section of the page
--%>
<c:if test="${!empty KualiForm.document.currentTransaction.openItemsInProcess}">
  <table border="0" cellspacing="0" cellpadding="0" class="datatable">
    <tr>
      <td colspan="7" class="infoline">
        Open Misc. Advances
      </td>
    </tr>
    <fp:miscAdvanceHeader itemInProcessProperty="document.currentTransaction.openItemInProcess[${loopStatus.index}]" creatingItemInProcess="false" />
    <c:forEach var="itemInProcess" items="${KualiForm.document.currentTransaction.openItemsInProcess}" varStatus="loopStatus">
      <fp:miscAdvanceLine itemInProcessProperty="document.currentTransaction.openItemInProcess[${loopStatus.index}]" creatingItemInProcess="false" />
    </c:forEach>
  </table>
</c:if>
