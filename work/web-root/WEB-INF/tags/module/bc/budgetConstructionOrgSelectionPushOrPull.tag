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

<c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
  <c:set var="tabTitle" value="Push Down Candidates"/>
</c:if>

<c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
  <c:set var="tabTitle" value="Pull Up Candidates"/>
</c:if>

<kul:tab tabTitle="${tabTitle}" defaultOpen="true" tabErrorKey="reportSel">
	<div class="tab-container" align="center" id="G02" style="display: block;">
    		<h3>${tabTitle}</h3>

        <table width="100%" cellpadding="0" cellspacing="0">
      		<c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
      		 <tr>
                <td width="200">
                  <div align="center">
                    <html:image property="methodToCall.performShowPullUpBudgetDocs" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Show Documents" alt="Show Documents" styleClass="tinybutton" />
                  </div>
                </td>
                <td>List Pullup Candidate Documents </td>
             </tr>
           </c:if>
           <c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
              <tr>
                <td width="200">
                  <div align="center">
                    <html:image property="methodToCall.performShowPushDownBudgetDocs" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Show Documents" alt="Show Documents" styleClass="tinybutton" />
                  </div>
                </td>
                <td>List Pushdown Candidate Documents </td>
             </tr>
           </c:if>
      </table>
  </div>
</kul:tab>  
