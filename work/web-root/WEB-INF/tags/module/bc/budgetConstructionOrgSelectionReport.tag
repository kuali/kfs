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

<kul:tab tabTitle="Reports And Exports" defaultOpen="true" tabErrorKey="reportSel">
	<div class="tab-container" align="center">

        <table class="datatable" border="0" width="100%" cellpadding="0" cellspacing="0">
        	<tr>
        		<td colspan="2" class="subhead">Reports</td>
        	</tr>
              <tr>
                <td width="200">
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((AccountFundingDetailReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Account Funding Detail" alt="Account Funding Detail" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Account Funding Detail</td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((AccountObjectDetailReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Account Object Detail" alt="Account Object Detail" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Account Object Detail &nbsp;&nbsp;&nbsp;
                  <html:checkbox property="accountObjectDetailConsolidation" title="accSumConsolidation"> (consolidated)</html:checkbox>
                </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((AccountSummaryReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Account Sum" alt="Account Sum" styleClass="tinybutton" value="AccountSummaryReport"/>
                  </div>
                </td>
                <td>Account Summary &nbsp;&nbsp;&nbsp; 
                  <html:checkbox property="accountSummaryConsolidation" title="accSumConsolidation"> (consolidated)</html:checkbox> 
                </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((LevelSummaryReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Level Sum" alt="Level Sum" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Level Summary </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((TwoPLGListReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="List 2PLG" alt="List 2PLG" styleClass="tinybutton" />
                  </div>
                </td>
                <td>List 2PLG </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((MonthSummaryReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Monthy Object Sum" alt="Monthly Object Sum" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Monthly Object Summary
                  &nbsp;&nbsp;&nbsp;
                  <html:checkbox property="monthObjectSummaryConsolidation" title="accSumConsolidation"> (consolidated)</html:checkbox>
                </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((ObjectSummaryReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Object Sum" alt="Object Sum" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Object Summary </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((SynchronizationProblemsReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Payroll Synchronization Problems" alt="Payroll Synchronization Problems" styleClass="tinybutton" onblur="formHasAlreadyBeenSubmitted = false"/>
                  </div>
                </td>
                <td>Payroll Synchronization Problems </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((PositionFundingDetailReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Position Funding" alt="Position Funding" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Position Funding </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((ReasonStatisticsReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Reason Statistics" alt="Reason Statistics" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Reason Statistics</td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((ReasonSummaryReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Reason Summary" alt="Reason Summary" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Reason Summary </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((SalaryStatisticsReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Salary Statistics" alt="Salary Statistics" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Salary Statistics </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((SalarySummaryReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Salary Summary" alt="Salary Summary" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Salary Summary </td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((SubFundSummaryReport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="SubFund Sum" alt="SubFund Sum" styleClass="tinybutton"  value="SubFundSummaryReport"/>
                  </div>
                </td>
                <td>Sub-Fund Summary</td>
              </tr>
              <tr>
                <td colspan="2" class="subhead">Export</td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((AccountExport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Account Export" alt="Account Export" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Budgeted Revenue/Expenditure Export</td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((FundingExport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Funding Export" alt="Funding Export" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Budgeted Salary Lines Export</td>
              </tr>
              <tr>
                <td>
                  <div align="center">
                    <html:image property="methodToCall.performReport.(((MonthlyExport)))" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Monthly Export" alt="Monthly Export" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Monthly Budget Export</td>
              </tr>
            </div>
       </table>
    </div>
</kul:tab>
