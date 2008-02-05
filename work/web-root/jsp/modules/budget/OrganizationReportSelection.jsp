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

<script language="JavaScript" type="text/javascript" src="scripts/budget/organizationSelectionTree.js"></script>

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetOrganizationReportSelection" renderMultipart="true"
	docTitle="${KualiForm.operatingModeTitle}"
    transactionalDocument="false">

	<html-el:hidden name="KualiForm" property="universityFiscalYear" />
	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="returnAnchor" />
	<html-el:hidden name="KualiForm" property="docFormKey" />
	<html-el:hidden name="KualiForm" property="accSumConsolidation" />

    <kul:errors keyMatch="pointOfViewOrg" errorTitle="Errors found in Organization Selection:" />
    <kul:messages/>
    <html:hidden property="operatingModeTitle" value="${KualiForm.operatingModeTitle}" />
    <table align="center" cellpadding="0" cellspacing="0" class="datatable-100">
        <tr>
            <th class="grid" colspan="6" align="left">
				<br>${KualiForm.operatingModeTitle}
				<br>
				<br>
  		    </th>
        </tr>
	    <tr>
            <%--point of view header --%>
            <th class="grid" > Select <br> </th>
		    <th class="grid" > <c:out value="${DataDictionary.SubFundGroup.attributes.subFundGroupCode.name}"/> <br> </th>
		    <th class="grid" > <c:out value="${DataDictionary.SubFundGroup.attributes.subFundGroupDescription.name}"/> <br> </th>
	    </tr>
	  
		<c:forEach var="subfund" items="${KualiForm.bcSubfundList}">
			<tr align="center">
				<td class="grid" valign="center">
					<%-- <center><html:multibox property="selectedSubfundGroupCode" value="${subfund.subFundGroupCode}"/></center> --%>
					<center>
						<input type="checkbox" name="${subfund.subFundGroupCode}" value="${subfund.subFundGroupCode}" <c:if test="${subfund.reportFlag eq 1}"> checked="checked" </c:if> />
					</center>
				</td>
				<td class="grid" valign="center">
					<center><c:out value="${subfund.subFundGroupCode}" /></center>
				</td>				
				<td class="grid" valign="center">
					<center><c:out value="${subfund.subFundGroup.subFundGroupDescription}" /></center>
				</td>				
					
			</tr>
		</c:forEach>
		</tr>
        </table>

    <div id="globalbuttons" class="globalbuttons">
    	<html:image property="methodToCall.selectAllSubFundGroup" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_selectall.gif"  title="Select" alt="Select All Sub-Fund Group" styleClass="smallbutton" />
		<html:image property="methodToCall.unSelectAllSubFundGroup" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_unselall.gif"  title="Unselect" alt="Unselect All Sub-Fund Group" styleClass="smallbutton" />
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_submit.gif" styleClass="globalbuttons" property="methodToCall.submit" title="submit" alt="submit" onclick="excludeSubmitRestriction=true" />
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>

</kul:page>
