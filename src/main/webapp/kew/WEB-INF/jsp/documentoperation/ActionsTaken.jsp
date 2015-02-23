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
		<td class="thnormal" colspan="2" align="center" height="30"><strong>Actions Taken</strong></td>
	  </tr>
	  	  
	  <c:choose> 	
	  <c:when test="${empty DocumentOperationForm.actionsTaken}">
	    <tr><td class="datacell" colspan="2" align="center" height="15">None</td></tr>
	  </c:when>	  		 
	  <c:otherwise>
	  <logic-el:iterate id="actionTaken" name="DocumentOperationForm" property="actionsTaken" indexId="ctr">
	  <html-el:hidden property="actionsTaken[${ctr}].actionTakenId" />
	  <html-el:hidden property="actionsTaken[${ctr}].lockVerNbr" />
 	  <tr>
	    <td width="33%" class="headercell3-b-l" align="right"><b> Action Taken ID: </b><c:out value="${actionTaken.actionTakenId}" /> </td>
	    <td width="66%" class="headercell3-b-l">
	      <html-el:radio property="actionTakenOp[${ctr}].value" value="update"/>Update &nbsp;&nbsp;<html-el:radio property="actionTakenOp[${ctr}].value" value="delete"/>Delete&nbsp;&nbsp;<html-el:radio property="actionTakenOp[${ctr}].value" value="noop"/>No Operation&nbsp;&nbsp;
	      <html-el:hidden property="actionTakenOp[${ctr}].index" />
	    </td>
	  </tr>	  
	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Document ID:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionsTaken[${ctr}].documentId" /></td>
	  </tr>
	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Document Version:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionsTaken[${ctr}].docVersion" /></td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Action Taken:</td>
  	    <td width="66%" class="datacell">
  	      <html-el:select property="actionsTaken[${ctr}].actionTaken" value="${actionTaken.actionTaken}">
    	    <c:set var="actionTakenCds" value="${DocumentOperationForm.actionTakenCds}"/>
    		<html-el:options collection="actionTakenCds" property="key" labelProperty="value"/>
  		  </html-el:select> 
  	    <%-- <html-el:text property="actionsTaken[${ctr}].actionTaken" />--%>
  	    </td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Action Date:</td>
  	    <td width="66%" class="datacell"><%-- <html-el:text property="actionsTaken[${ctr}].actionDateString" />&nbsp;--%>
  	      <input type='text' name='actionTakenActionDate<c:out value="${ctr}"/>' value='<c:out value="${actionTaken.actionDateString}"/>'>
	  	  <a href="javascript:addCalendar('actionTakenActionDate<c:out value="${ctr}"/>', 'Select Date', 'actionTakenActionDate<c:out value="${ctr}"/>', 'DocumentOperationForm'); showCal('actionTakenActionDate<c:out value="${ctr}"/>');"><img src="images/cal.gif" width="16" height="16" align="absmiddle" alt="Click Here to select a date"></a>
  	      <html-el:hidden property="actionsTaken[${ctr}].actionDateString" />
  	    </td>
  	  </tr>
   	  <tr>
  	    <td width="33%" align="right" class="thnormal">* Action Taken Person ID:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionsTaken[${ctr}].principalId" />
  	      <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" fieldConversions="principalId:actionsTaken[${ctr}].principalId" lookupParameters="actionsTaken[${ctr}].principalId:principalId" />
  	    </td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">Delegator Person ID:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionsTaken[${ctr}].delegatorPrincipalId" />
  	      <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" fieldConversions="principalId:actionsTaken[${ctr}].delegatorPrincipalId" lookupParameters="actionsTaken[${ctr}].delegatorPrincipalId:principalId" />
  	    </td>
  	  </tr>
	  <tr>
  	    <td width="33%" align="right" class="thnormal">Delegator Workgroup ID:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionsTaken[${ctr}].delegatorGroupId" />
  	      <kul:lookup boClassName="org.kuali.rice.kim.impl.group.GroupBo" fieldConversions="id:actionsTaken[${ctr}].delegatorGroupId" lookupParameters="actionsTaken[${ctr}].delegatorGroupId:id" />
  	    </td>
  	  </tr>
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">Current Indicator:</td>
  	    <td width="66%" class="datacell"><html-el:text property="actionsTaken[${ctr}].currentIndicator" /></td>
  	  </tr>  
  	  <tr>
  	    <td width="33%" align="right" class="thnormal">Annotation:</td>
  	    <td width="66%" class="datacell"><html-el:textarea cols="120" rows="1" property="actionsTaken[${ctr}].annotation" /></td>
  	  </tr>
	  </logic-el:iterate>
	  </c:otherwise>
      </c:choose>
	</table>
  </td>
  <td width="20" height="30">&nbsp;</td>
</tr>
