<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
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
