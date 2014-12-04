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

<kul:tabTop tabTitle="Report/Export" defaultOpen="true" tabErrorKey="${BCConstants.BUDGET_CONSTRUCTION_REPORTDUMP_TAB_ERRORS}">
<div class="tab-container" align=center>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
    <tr>
        <td colspan="2" class="subhead">
		    <span class="subhead-left">Report/Export</span>
		</td>
    </tr>

    <c:forEach items="${KualiForm.budgetConstructionDocumentReportModes}" var="item" varStatus="status" >
        <tr>
            <td class="datacell" nowrap>
                <div align="center">
                  <html:image property="methodToCall.performReportDump.line${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Run Report/Dump For Line ${status.index}" onclick="excludeSubmitRestriction=true" alt="Run Report/Dump Line ${status.index}" styleClass="tinybutton" />
                </div>
            </td>
            <td class="datacell" nowrap>
                <div align="left">
                    ${item.reportDesc}
                </div>
            </td>
        </tr>
    
    </c:forEach>
    </table>
</div>
</kul:tabTop>
