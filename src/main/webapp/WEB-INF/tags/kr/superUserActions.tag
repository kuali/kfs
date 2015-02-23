<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ attribute name="showTab" required="false" description="used to decide if the tab should be open by default"%>
<c:if test="${KualiForm.superUserAuthorized && KualiForm.superUserActionAvaliable}">
<c:set var="tabTitle"><bean:message key="superuser.tab.label" /></c:set>
<c:set var="actionLabel"><bean:message key="superuser.action.column.label" /></c:set>
<c:set var="requestedLabel"><bean:message key="superuser.requested.column.label" /></c:set>
<c:set var="timeLabel"><bean:message key="superuser.time.column.label" /></c:set>
<c:set var="annotationLabel"><bean:message key="superuser.annotation.column.label" /></c:set>
<c:set var="tabOpenBydefault" value="true" />
<c:if test="${not empty showTab}"><c:set var="tabOpenBydefault" value="${showTab}" /></c:if>
  <kul:tab tabTitle="${tabTitle}"
	     defaultOpen="${tabOpenBydefault}"
	     tabErrorKey="superuser.errors"
	     transparentBackground="${transparentBackground}">
	<div class="tab-container" align=center id="G4">
    <c:if test="${KualiForm.superUserApproveSingleActionRequestAuthorized && KualiForm.stateAllowsApproveSingleActionRequest && not empty KualiForm.actionRequestsRequiringApproval}">
     	<h3>${tabTitle}</h3>
	    <table cellpadding="0" cellspacing="0" class="datatable" summary="view/add notes">
			<tbody>
			<tr>
			  <th style="width: 5%; text-align: center;"><input type="checkbox" onclick="jQuery('input.superUserAction').prop('checked', jQuery(this).prop('checked'))" /></th>
					    <th style="width: 15%;">${actionLabel}</th>
					    <th style="width: 15%;">${requestedLabel}</th>
					    <th style="width: 15%;">${timeLabel}</th>
				    	<th style="width: 50%;">${annotationLabel}</th>
				</tr>
        <c:forEach var="actionRequest" items="${KualiForm.actionRequests}" varStatus="status">
          <tr>
				    <td class="datacell" style="text-align: center;"><html:multibox property="selectedActionRequests" value="${actionRequest.id}" styleClass="superUserAction" /></td>
				    <td class="datacell">${actionRequest.actionRequested}</td>
				    <td class="datacell">
				        <c:choose>
                    <c:when test="${actionRequest.userRequest}">
                        <c:out value="${kfunc:getPrincipalDisplayName(actionRequest.principalId)}" />
                    </c:when>
                    <c:when test="${actionRequest.groupRequest}">
                        <c:out value="${kfunc:getKimGroupDisplayName(actionRequest.groupId)}" />
                    </c:when>
                    <c:otherwise>
                        <c:out value="${kfunc:getRoleDisplayName(actionRequest)}" />
                    </c:otherwise>
                 </c:choose>
            </td>
				    <td class="datacell"><joda:format value="${actionRequest.dateCreated}" pattern="MM/dd/yyyy hh:mm a"/>&nbsp;</td>
				    <td class="datacell">${actionRequest.annotation}</td>
				  </tr>
        </c:forEach>
		    </tbody>
        </table>
    </c:if>
    <br>
    <c:if test="${KualiForm.superUserActionAvaliable}">
    <div style="vertical-align: top;">
      <label for="superUserAnnotation" style="vertical-align: top;">Annotation<span style="color: red; vertical-align: top;">*</span></label>
      <html:textarea property="superUserAnnotation" rows="5" cols="100" styleId="superUserAnnotation" />
    </div>
    <div>
      <c:if test="${KualiForm.superUserApproveSingleActionRequestAuthorized && KualiForm.stateAllowsApproveSingleActionRequest && not empty KualiForm.actionRequestsRequiringApproval}">
        <html-el:image property="methodToCall.takeSuperUserActions" src="${ConfigProperties.kew.url}/images/buttonsmall_takeselected.gif" style="border-style:none;" align="absmiddle" />
      </c:if>
      <c:if test="${KualiForm.superUserApproveDocumentAuthorized && KualiForm.stateAllowsApproveOrDisapprove}">
        <html-el:image property="methodToCall.superUserApprove" src="${ConfigProperties.kew.url}/images/buttonsmall_approvedoc.gif" style="border-style:none;" align="absmiddle" />
      </c:if>
      <c:if test="${KualiForm.superUserDisapproveDocumentAuthorized && KualiForm.stateAllowsApproveOrDisapprove}">
        <html-el:image property="methodToCall.superUserDisapprove" src="${ConfigProperties.kew.url}/images/buttonsmall_disapprovedoc.gif" style="border-style:none;" align="absmiddle" />
      </c:if>
    </div>
    </c:if>
  </div>
  </kul:tab>
</c:if>