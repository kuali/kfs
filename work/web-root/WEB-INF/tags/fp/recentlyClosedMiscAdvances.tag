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

<kul:tab tabTitle="Recently Closed Miscellaneous Advances" defaultOpen="false" tabErrorKey="recentlyClosedItemsInProcess" >
  <div class="tab-container" align="center">
    <table border="0" cellspacing="3" cellpadding="3" class="datatable">
      <tr>
        <th>Identifier</th>
        <th>Open Date</th>
        <th>Closed Date</th>
        <th>Total Amount</th>
      </tr>
      <c:forEach var="itemInProcess" items="${KualiForm.recentlyClosedItemsInProcess}">
        <tr>
          <td><fp:miscAdvanceInquiry itemInProcess="${itemInProcess}" /></td>
          <td><fmt:formatDate value="${itemInProcess.itemOpenDate}" /></td>
          <td><fmt:formatDate value="${itemInProcess.itemClosedDate}" /></td>
          <td>$${itemInProcess.itemAmount}</td>
        </tr>
      </c:forEach>
    </table>
  </div>
</kul:tab>
