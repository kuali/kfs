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

<c:set var="pointOfViewOrgAttributes" value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}" />
<c:set var="organizationAttributes" value="${DataDictionary.Org.attributes}" />

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetOrganizationSelectionTree" renderMultipart="true"
	docTitle="Organization Selection"
    transactionalDocument="false">

<%--	<kul:hiddenDocumentFields /> --%>
	<html-el:hidden name="KualiForm" property="returnAnchor" />
	<html-el:hidden name="KualiForm" property="returnFormKey" />
	<html-el:hidden name="KualiForm" property="operatingMode" />
	
    <kul:errors errorTitle="Errors found in Organization Selection:" />
    <kul:messages/>

    <table align="center" cellpadding="0" cellspacing="0" class="datatable-100">
        <tr>
            <th class="grid" colspan="7" align="left">
                <br>
                ${KualiForm.operatingModeTitle}
                <br><br>
		    </th>
        </tr>
	    <tr>
            <%--point of view header --%>
            <th class="grid" colspan="2" rowspan="2">&nbsp;</th>
		    <th class="grid" align="left" colspan="4">
		        Current Point Of View Organization
		    </th>
		    <th class="grid" align="center" colspan="1">
			    Action
		    </th>
	    </tr>
	    <tr>
            <%--point of view data line --%>
            <%--first cell in row above spans two rows
                lookupUnkeyedFieldConversions="appointmentDurationCode:newBCAFLine.appointmentFundingDurationCode,"
                accountingLineValuesMap="${pointOfViewOrg.valuesMap}" --%>
            <bc:pbglLineDataCell dataCellCssClass="grid"
                accountingLine="pointOfViewOrg"
                field="chartOfAccountsCode"
                attributes="${pointOfViewOrgAttributes}" inquiry="false"
                readOnly="true"
                displayHidden="false"
                colSpan="1" />
            <bc:pbglLineDataCell dataCellCssClass="grid"
                accountingLine="pointOfViewOrg"
                field="organizationCode"
                detailField="organization.organizationName"
                attributes="${pointOfViewOrgAttributes}" inquiry="true"
                boClassSimpleName="Org"
                readOnly="false"
                displayHidden="false" />
            <td class="grid" valign="center" rowspan="1" colspan="1">
			    &nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
			    &nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
			    &nbsp;
            </td>
	    </tr>
    </table>

    <div id="globalbuttons" class="globalbuttons">
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>

</kul:page>
