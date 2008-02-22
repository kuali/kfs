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
<c:set var="subFundAttribute" value="${DataDictionary.BudgetConstructionSubFundPick.attributes}" />	

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetOrganizationReportSelection" renderMultipart="true"
	docTitle="${KualiForm.operatingModeTitle}"
    transactionalDocument="false">

	<html-el:hidden name="KualiForm" property="universityFiscalYear" />
	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="returnAnchor" />
	<html-el:hidden name="KualiForm" property="docFormKey" />
	<html-el:hidden name="KualiForm" property="reportMode" />
	<html-el:hidden name="KualiForm" property="buildControlList" />
	<html-el:hidden name="KualiForm" property="reportConsolidation" />
	<html-el:hidden name="KualiForm" property="refreshListFlag" />
	<html-el:hidden name="KualiForm" property="operatingModeTitle" />
	
    <kul:errors keyMatch="pointOfViewOrg" errorTitle="Errors found in Organization Selection:" />
    <kul:messages/>
    <html:hidden property="operatingModeTitle" value="${KualiForm.operatingModeTitle}" />
    <table align="center" cellpadding="0" cellspacing="0" class="datatable-100">
        <tr>
            <th class="grid" colspan="6" align="left">
				<br> ${KualiForm.operatingModeTitle} <br> <br>
			</th>
        </tr>
	    <tr>
            <th class="grid"> <br> Select <br> <br> </th>
		    <th class="grid"> <br>	    
		    <kul:htmlAttributeLabel  attributeEntry="${subFundAttribute.subFundGroupCode}" useShortLabel="false" /> <br> <br> </th>
		    <th class="grid" > <br>
		    <kul:htmlAttributeLabel  attributeEntry="${subFundAttribute['subFundGroup.subFundGroupDescription']}" useShortLabel="false" /> <br> <br> </th>
	    </tr>

		<logic:iterate name="KualiForm" id="bcSubFund" property="bcSubFunds" indexId="ctr">
		    <tr align="center">
				<td class="grid" valign="center">
					<center>
		            <html:checkbox property="bcSubFunds[${ctr}].reportFlag" value="1" />
					</center>
				</td>
				<td class="grid" valign="center">
					<center>
						<kul:htmlControlAttribute 
						property="bcSubFunds[${ctr}].subFundGroupCode"
			            attributeEntry="${subFundAttribute.subFundGroupCode}"
        		        readOnly="true"/>
            		<center>
				</td>				
				<td class="grid" valign="center">
					<center>
						<kul:htmlControlAttribute 
						property="bcSubFunds[${ctr}].subFundGroup.subFundGroupDescription"
			            attributeEntry="${subFundAttribute.subFundGroup.subFundGroupDescription}"
	        	        readOnly="true"/>
 					</center>
				</td>				
			</tr>
		</logic:iterate>
		</tr>
        </table>

    <div id="globalbuttons" class="globalbuttons">
    	<html:image property="methodToCall.selectAllSubFundGroup" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_selectall.gif"  title="Select" alt="Select All Sub-Fund Group" styleClass="smallbutton" />
		<html:image property="methodToCall.unSelectAllSubFundGroup" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_unselall.gif"  title="Unselect" alt="Unselect All Sub-Fund Group" styleClass="smallbutton" />
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_submit.gif" styleClass="globalbuttons" property="methodToCall.perform${KualiForm.reportMode}" title="perform${KualiForm.reportMode}" alt="submit" onclick="excludeSubmitRestriction=true" />
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>

</kul:page>
