<%--

    Copyright 2005-2014 The Kuali Foundation

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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- This would be a good spot to use a tag library since this code is essentially verbatim from WorkgroupEntry.jsp --%>
<c:set var="userDisplayName" value="${RemoveReplaceForm.user.displayName}"/>
<c:if test="${UserSession.principalId != RemoveReplaceForm.user.workflowId}">
  <c:set var="userDisplayName" value="${RemoveReplaceForm.user.displayNameSafe}"/>
</c:if>

<c:set var="replacementUserDisplayName" value="${RemoveReplaceForm.replacementUser.displayName}"/>
<c:if test="${UserSession.principalId != RemoveReplaceForm.replacementUser.workflowId}">
  <c:set var="replacementUserDisplayName" value="${RemoveReplaceForm.replacementUser.displayNameSafe}"/>
</c:if>

  <div class="annotate-container">
    <div align="center">

      <table border="0" cellspacing="0" cellpadding="2">
        <tr>
          <td><div align="right"><strong>Action:</strong></div></td>
          <td><c:out value="${RemoveReplaceForm.operationDisplayName}"/></td>
        </tr>
        <tr>
          <td><div align="right"><strong>User Id:</strong></div></td>
          <td><c:out value="${userDisplayName}" />
             (<kul:inquiry boClassName="org.kuali.rice.kim.impl.identity.PersonImpl" keyValues="principalId=${RemoveReplaceForm.user.workflowId}" render="true">
                <c:out value="${RemoveReplaceForm.userId}" />
              </kul:inquiry>)
          </td>
        </tr>
        <c:if test="${RemoveReplaceForm.replace}">
        <tr>
          <td><div align="right"><strong>User Id to Replace With:</strong></div></td>
          <td><c:out value="${replacementUserDisplayName}" />
              (<kul:inquiry boClassName="org.kuali.rice.kim.impl.identity.PersonImpl" keyValues="principalId=${RemoveReplaceForm.replacementUser.workflowId}" render="true">
                <c:out value="${RemoveReplaceForm.replacementUserId}" />
              </kul:inquiry>)
          </td>
        </tr>
        </c:if>
      </table>

    </div>
  </div>
