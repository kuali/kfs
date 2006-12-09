<%--
 Copyright 2006 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<!-- BEGIN budgetPersonnel.tag -->

<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<div id="workarea">

<c:set var="budgetAttributes" value="${DataDictionary.Budget.attributes}" />
<c:set var="budgetPersonnelAttributes" value="${DataDictionary.BudgetUser.attributes}" />
<c:set var="firstItemNotDisplayed" value="true" />

  <html:hidden property="document.budget.institutionCostShareIndicator" />
  <html:hidden property="document.budget.budgetThirdPartyCostShareIndicator" />
  
  <logic:iterate id="person" name="KualiForm" property="document.budget.personnel" indexId="listIndex">

      <kra-b:budgetPersonnelIndividualDetail person="${person}" firstInList="${firstItemNotDisplayed}" listIndex="${listIndex}" />
      <c:set var="firstItemNotDisplayed" value="false" />

  </logic:iterate> 
 
 <c:if test="${!empty KualiForm.document.budget.personnel}">
          <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
            <tr>
              <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
              <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
            </tr>
          </table>    
 </c:if>

 
<!-- END budgetPersonnel.tag -->
