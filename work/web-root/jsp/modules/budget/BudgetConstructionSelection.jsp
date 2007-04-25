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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:set var="bcHeaderAttributes"
	value="${DataDictionary['BudgetConstructionHeader'].attributes}" />

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetBudgetConstructionSelection" renderMultipart="true"
	docTitle="Budget Construction Selection"
    transactionalDocument="false"
	>

<%--	<kul:hiddenDocumentFields /> --%>

	<kul:errors errorTitle="Errors found in Search Criteria:" />
	<kul:messages/>

    <table width="100%" cellpadding="0" cellspacing="0" class="datatable-100">
<%--
    <table align="center" border="0" cellpadding="0" cellspacing="0" class="datatable-100">
--%>
    	<tr>
		    <th colspan="2" class="headerarea-small" align="left">
			    Budget Construction Document Open
		    </th>
		</tr>
	    <tr>
		    <td colspan="1" class="infoline" align="right">
                <html:hidden property="universityFiscalYear"/>
			    BC Fiscal Year:
		    </td>

            <td class="grid" valign="center" rowspan="1" colspan="1">
            <div align="left">
            <span class="nowrap">
                ${KualiForm.universityFiscalYear}&nbsp;
<%--
            <kul:htmlControlAttribute
                property="universityFiscalYear"
                attributeEntry="${bcHeaderAttributes.universityFiscalYear}"
                readOnly="true"
            />
--%>
            </span>
            </div>
            </td>
	    </tr>
	    <tr>
            <td class="grid" nowrap>
                &nbsp;
            </td>
            <td class="grid" nowrap>
            <div align="center">
                <html:image property="methodToCall.performBCDocumentOpen.anchorbcheaderLineAnchor" src="images/tinybutton-loaddoc.gif" title="Load Budget Construction Document" alt="Load Budget Construction Document" styleClass="tinybutton" />
            </div>
            </td>
	    </tr>
	</table>

<%--TODO need to create save and close(and prompt to save) actions that calls returnToCaller --%>
    <div id="globalbuttons" class="globalbuttons">
        <html:image src="images/buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>

</kul:page>
