<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<kul:tab tabTitle="Report/Dump" defaultOpen="false" tabErrorKey="${BCConstants.BUDGET_CONSTRUCTION_REPORTDUMP_TAB_ERRORS}">
<div class="tab-container" align=center>
    <table width="50%" border="0" cellpadding="0" cellspacing="0" class="datatable">
    <tr>
        <td colspan="2" class="subhead">
		    <span class="subhead-left">Report/Dump</span>
		</td>
    </tr>
    <tr>
            <td class="datacell" nowrap>
                <div align="center">
                Description
                </div>
            </td>
            <td class="datacell" nowrap>
                <div align="center">
                Action
                </div>
            </td>
    </tr>
    <c:forEach items="${KualiForm.budgetConstructionDocumentReportModes}" var="item" varStatus="status" >
        <tr>
            <td class="datacell" nowrap>
                <div align="left">
                    ${item.reportDesc}
                </div>
            </td>
            <td class="datacell" nowrap>
                <div align="center">
                  <html:image property="methodToCall.performReportDump.line${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Run Report/Dump For Line ${status.index}" alt="Run Report/Dump Line ${status.index}" styleClass="tinybutton" />
                </div>
            </td>
        </tr>
    
    </c:forEach>
    </table>
</div>
</kul:tab>
