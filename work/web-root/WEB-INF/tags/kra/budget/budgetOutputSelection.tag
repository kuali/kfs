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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<c:set var="KraConstants" value="${KraConstants}" />

          <table cellpadding=0 cellspacing="0"  summary="">
            <tr>
              <td colspan=3 class="subhead">
                <span class="subhead-left"> Select Report Type </span>
              </td>
            </tr>
            <tr align="center" valign="top">
              <td width="40%" ><div class="floaters" > <strong>
                  <html:radio property="currentOutputReportType" styleId="currentOutputReportType.genericByTask" value="genericByTask"/><label for="currentOutputReportType.genericByTask">Generic by Task&nbsp&nbsp&nbsp</label>
                  <html:radio property="currentOutputReportType" styleId="currentOutputReportType.genericByPeriod" value="genericByPeriod"/><label for="currentOutputReportType.genericByPeriod">Generic by Period</label>
                  <br><br>
                  <html:select property="currentOutputDetailLevel">
                    <html:option value="">detail level:</html:option>
                    <html:option value="high">high</html:option>
                    <html:option value="medium">medium</html:option>
                    <html:option value="low">low</html:option>
                  </html:select>
                  </strong></div></td>
              <td width="45%" ><label> </label>
                <div class="floaters"> <strong>
                  <html:radio property="currentOutputReportType" styleId="currentOutputReportType.agency" value="agency"/><label for="currentOutputReportType.agency">Agency</label>
                  <br><br>
                  <html:select property="currentOutputAgencyType">
                    <html:option value="">type:</html:option>
                    <html:option value="NIH-398">NIH - PHS 398 Form Page 4&amp;5 - Rev 04/06</html:option>
                    <html:option value="NIH-2590">NIH - PHS 2590 - Rev 04/06</html:option>
                    <c:if test="${KualiForm.document.budget.agencyModularIndicator}"><html:option value="NIH-mod">NIH Modular Budget</html:option></c:if>
                    <c:if test="${KualiForm.document.budget.budgetAgency.agencyExtension.agencyNsfOutputIndicator}"><html:option value="NSF-summary">NSF Summary Proposal Budget</html:option></c:if>
                  </html:select>
                  <br>
                  <br>
                  </strong>
                  <html:select property="currentOutputAgencyPeriod">
                    <html:option value="">period:</html:option>
                    <c:set var="budgetPeriods" value="${KualiForm.budgetDocument.budget.periods}"/>
                    <html:options collection="budgetPeriods" property="budgetPeriodSequenceNumber" labelProperty="budgetPeriodLabel"/>
                  </html:select>
                  (only for NIH Form 2590)<br>
                  <br>Note: When using budget periods of less than one year with the interim NIH PHS 398 and 2590 Forms, the requested salary amounts are correct but the person-months figure and base salary will need to be manually adjusted in Adobe Acrobat.<br>
                </div></td>
              <td width="15%" ><div class="floaters" > <strong>
                  <html:radio property="currentOutputReportType" styleId="currentOutputReportType.SF424" value="SF424"/><label for="currentOutputReportType.SF424">SF424</label>
                  </strong></div></td>
            </tr>
          </table>
          