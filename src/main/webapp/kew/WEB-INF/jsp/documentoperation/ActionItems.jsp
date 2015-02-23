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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<tr>
  <td><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>
  <td> 
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="bord-r-t" align="center">
	  <tr>
		<td class="thnormal" colspan="2" align="center" height="30"><strong>Action Items</strong></td>
	  </tr>
	  <c:choose> 	
	  <c:when test="${empty DocumentOperationForm.actionItems}">
	    <tr><td class="datacell" colspan="2" align="center" height="15">None</td></tr>
	  </c:when>	  		 
	  <c:otherwise>
      <logic-el:iterate id="actionItem" name="DocumentOperationForm" property="actionItems" indexId="ctr">
      <html-el:hidden property="actionItems[${ctr}].id" />
 	  <tr>
	    <td width="33%" class="headercell3-b-l" align="right"><b> Action Item ID: </b><c:out value="${actionItem.id}" /> </td>
	    <td width="66%" class="headercell3-b-l">
	      <html-el:radio property="actionItemOp[${ctr}].value" value="update"/>Update &nbsp;&nbsp;<html-el:radio property="actionItemOp[${ctr}].value" value="delete"/>Delete&nbsp;&nbsp;<html-el:radio property="actionItemOp[${ctr}].value" value="noop"/>No Operation&nbsp;&nbsp;
	      <html-el:hidden property="actionItemOp[${ctr}].index" />
	    </td>
	  </tr>	 
	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Document ID:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].documentId" /></td>
  	  </tr> 
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Doc Type Name:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].docName" />
			<kul:lookup boClassName="org.kuali.rice.kew.doctype.bo.DocumentType" fieldConversions="name:actionItems[${ctr}].docName" lookupParameters="actionItems[${ctr}].docName:name" />
        </td>
  	  </tr> 
  	   <tr>
  	    <td width="33%" align="right" class="thnormal">* Doc Type Label:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].docLabel" /></td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Doc Handler URL:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].docHandlerURL" /></td>
  	  </tr>
   	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Date Assigned:</td>
  	    <td width="66%" class="datacell">
  	      <input type='text' name='actionItemDateAssigned<c:out value="${ctr}"/>' value='<c:out value="${actionItem.dateAssignedStringValue}"/>'>
	  	  <a href="javascript:addCalendar('actionItemDateAssigned<c:out value="${ctr}"/>', 'Select Date', 'actionItemDateAssigned<c:out value="${ctr}"/>', 'DocumentOperationForm'); showCal('actionItemDateAssigned<c:out value="${ctr}"/>');"><img src="images/cal.gif" width="16" height="16" align="absmiddle" alt="Click Here to select a date"></a>
  	      <html-el:hidden property="actionItems[${ctr}].dateAssignedStringValue" />
  	    </td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Action Request ID:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].actionRequestId" /></td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Action Requested:</td>
  	    <td width="66%" class="datacell"><%-- <html-el:text property="actionItems[${ctr}].actionRequestCd" />--%>
  	      <html-el:select property="actionItems[${ctr}].actionRequestCd" value="${actionItem.actionRequestCd}">
		    <c:set var="actionRequestCds" value="${DocumentOperationForm.actionRequestCds}"/>
    		<html-el:options collection="actionRequestCds" property="key" labelProperty="value"/>
  		  </html-el:select>   
  	    </td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Responsibility ID:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].responsibilityId" /></td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Person ID:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].principalId" />
  	      <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" fieldConversions="principalId:actionItems[${ctr}].principalId" lookupParameters="actionItems[${ctr}].principalId:principalId" />
  	    </td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">Workgroup ID:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].groupId" />
  	      <kul:lookup boClassName="org.kuali.rice.kim.impl.group.GroupBo" fieldConversions="id:actionItems[${ctr}].groupId" lookupParameters="actionItems[${ctr}].groupId:id" />
  	    </td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">Role Name:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].roleName" />
  	      <kul:lookup boClassName="org.kuali.rice.kim.impl.role.RoleBo" fieldConversions="name:actionItems[${ctr}].roleName" lookupParameters="actionItems[${ctr}].roleName:name" />
  	    </td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">Delegator Person ID: </td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].delegatorPrincipalId" />
  	      <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" fieldConversions="principalId:actionItems[${ctr}].delegatorPrincipalId" lookupParameters="actionItems[${ctr}].delegatorPrincipalId:principalId" />
  	    </td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">Delegator Workgroup ID:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].delegatorGroupId" />
  	      <kul:lookup boClassName="org.kuali.rice.kim.impl.group.GroupBo" fieldConversions="id:actionItems[${ctr}].delegatorGroupId" lookupParameters="actionItems[${ctr}].delegatorGroupId:id" />
  	    </td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">Document Title:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionItems[${ctr}].docTitle" /></td>
  	  </tr>
  	  </logic-el:iterate>
	  </c:otherwise>
      </c:choose>
    </table>
  </td>
  <td width="20" height="30">&nbsp;</td>
</tr>
