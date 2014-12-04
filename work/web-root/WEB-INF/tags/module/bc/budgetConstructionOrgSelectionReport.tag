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
