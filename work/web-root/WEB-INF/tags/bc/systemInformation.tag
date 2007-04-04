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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>

<c:set var="documentAttributes" value="${DataDictionary.KualiBudgetConstructionDocument.attributes}" />
<c:set var="accountAttributes" value="${DataDictionary.Account.attributes}" />
<c:set var="subFundGroupAttributes" value="${DataDictionary.SubFundGroup.attributes}" />
<c:set var="orgAttributes" value="${DataDictionary.Org.attributes}" />
<c:set var="orgVals" value="${KualiForm.document.account.organization}" />
<c:set var="orgPropString" value="document.account.organization" />
<c:set var="accountRptsAttributes" value="${DataDictionary.BudgetConstructionAccountReports.attributes}" />
<c:set var="orgRptsAttributes" value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}" />
<c:set var="orgRptsVals" value="${KualiForm.document.budgetConstructionAccountReports.budgetConstructionOrganizationReports}" />
<c:set var="orgRptsPropString" value="document.budgetConstructionAccountReports.budgetConstructionOrganizationReports" />

<kul:tab tabTitle="System Information" defaultOpen="true" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS}">
<div class="tab-container" align=center>
	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable" title="view system information" summary="view system information">
    	<tr>
		    <td colspan="4" class="subhead">
			<span class="subhead-left">System Information</span>
		    </td>
		</tr>
		<tr>
	      <kul:htmlAttributeHeaderCell
	          labelFor="document.previousUniversityFiscalYear"
	          literalLabel="Fiscal Year:"
	          horizontal="true"
	          />
	      <td></td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="document.previousUniversityFiscalYear" attributeEntry="${documentAttributes.universityFiscalYear}" readOnly="${true}"/>
	      </td>
	      <td>&nbsp;</td>
		</tr>
	    <tr>
	      <kul:htmlAttributeHeaderCell
	          labelFor="document.accountNumber"
	          literalLabel="Chart/Account:"
	          horizontal="true"
	          />
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="document.chartOfAccountsCode"
	      		attributeEntry="${documentAttributes.chartOfAccountsCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.Chart"
				    keyValues="chartOfAccountsCode=${KualiForm.document.chartOfAccountsCode}"
				    render="true">
			    	<html:hidden write="true" property="document.chartOfAccountsCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="document.accountNumber"
	      		attributeEntry="${documentAttributes.accountNumber}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.Account"
				    keyValues="chartOfAccountsCode=${KualiForm.document.chartOfAccountsCode}&amp;accountNumber=${KualiForm.document.accountNumber}"
				    render="true">
			    	<html:hidden write="true" property="document.accountNumber" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="document.account.accountName" attributeEntry="${accountAttributes.accountName}" readOnly="${true}"/>
	      </td>
		</tr>
	    <tr>
	      <kul:htmlAttributeHeaderCell
	          labelFor="document.subAccountNumber"
	          literalLabel="Sub-Account:"
	          horizontal="true"
	          />
	      <td>&nbsp;</td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="document.subAccountNumber" attributeEntry="${documentAttributes.subAccountNumber}" readOnly="${true}"/>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="document.subAccount.subAccountName" attributeEntry="${DataDictionary.SubAccount.attributes.subAccountName}" readOnly="${true}"/>
	      </td>
		</tr>
	    <tr>
	      <kul:htmlAttributeHeaderCell
	          labelFor="document.account.subFundGroupCode"
	          literalLabel="Sub-Fund Group:"
	          horizontal="true"
	          />
	      <td>&nbsp;</td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="document.account.subFundGroupCode"
	      		attributeEntry="${accountAttributes.subFundGroupCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.SubFundGroup"
				    keyValues="subFundGroupCode=${KualiForm.document.account.subFundGroupCode}"
				    render="true">
			    	<html:hidden write="true" property="document.account.subFundGroupCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="document.account.subFundGroup.subFundGroupDescription" attributeEntry="${DataDictionary.SubFundGroup.attributes.subfundGroupDescription}" readOnly="${true}"/>
	      </td>
		</tr>
	    <tr>
	      <kul:htmlAttributeHeaderCell
	          labelFor="document.account.organizationCode"
	          literalLabel="Org:"
	          horizontal="true"
	          />
	      <td>&nbsp;</td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="document.account.organizationCode"
	      		attributeEntry="${accountAttributes.organizationCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.Org"
				    keyValues="chartOfAccountsCode=${KualiForm.document.account.chartOfAccountsCode}&amp;organizationCode=${KualiForm.document.account.organizationCode}"
				    render="true">
			    	<html:hidden write="true" property="document.account.organizationCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="document.account.organization.organizationName" attributeEntry="${DataDictionary.Org.attributes.organizationName}" readOnly="${true}"/>
	      </td>
        </tr>
	    <tr>
	      <kul:htmlAttributeHeaderCell
	          labelFor="document.account.organization.reportsToOrganizationCode"
	          literalLabel="Reports-To Chart/Org:"
	          horizontal="true"
	          />
	      <td align="center" valign="middle">
		      	<kul:htmlControlAttribute
	      		property="document.account.organization.reportsToChartOfAccountsCode"
	      		attributeEntry="${orgAttributes.reportsToChartOfAccountsCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.Chart"
				    keyValues="chartOfAccountsCode=${KualiForm.document.account.organization.reportsToChartOfAccountsCode}"
				    render="true">
			    	<html:hidden write="true" property="document.account.organization.reportsToChartOfAccountsCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="${orgPropString}.reportsToOrganizationCode"
	      		attributeEntry="${orgAttributes.reportsToOrganizationCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.Org"
				    keyValues="chartOfAccountsCode=${orgVals.reportsToChartOfAccountsCode}&amp;organizationCode=${orgVals.reportsToOrganizationCode}"
				    render="true">
			    	<html:hidden write="true" property="${orgPropString}.reportsToOrganizationCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="${orgPropString}.reportsToOrganization.organizationName" attributeEntry="${organizationAttributes.organizationName}" readOnly="${true}"/>
	      </td>
		</tr>
    	<tr>
		    <td colspan="4" class="subhead">
			<span class="subhead-left">Next Year Data</span>
		    </td>
	    </tr>
	    <tr>
	      <kul:htmlAttributeHeaderCell
	          labelFor="document.universityFiscalYear"
	          literalLabel="Fiscal Year:"
	          horizontal="true"
	          />
	      <td>&nbsp;</td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="document.universityFiscalYear" attributeEntry="${documentAttributes.universityFiscalYear}" readOnly="${true}"/>
	      </td>
	      <td>&nbsp;</td>
        </tr>
		<tr>
	      <kul:htmlAttributeHeaderCell
	          labelFor="document.budgetConstructionAccountReports.reportsToChartOfAccountsCode"
	          literalLabel="Chart/Org:"
	          horizontal="true"
	          />
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="document.budgetConstructionAccountReports.reportsToChartOfAccountsCode"
	      		attributeEntry="${accountRptsAttributes.reportsToChartOfAccountsCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.Chart"
				    keyValues="chartOfAccountsCode=${KualiForm.document.budgetConstructionAccountReports.reportsToChartOfAccountsCode}"
				    render="true">
			    	<html:hidden write="true" property="document.budgetConstructionAccountReports.reportsToChartOfAccountsCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="document.budgetConstructionAccountReports.reportsToOrganizationCode"
	      		attributeEntry="${accountRptsAttributes.reportsToOrganizationCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.budget.bo.BudgetConstructionOrganizationReports"
				    keyValues="chartOfAccountsCode=${KualiForm.document.budgetConstructionAccountReports.reportsToChartOfAccountsCode}&amp;organizationCode=${KualiForm.document.budgetConstructionAccountReports.reportsToOrganizationCode}"
				    render="true">
			    	<html:hidden write="true" property="document.budgetConstructionAccountReports.reportsToOrganizationCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="document.budgetConstructionAccountReports.reportsToOrganization.organizationName" attributeEntry="${orgAttributes.organizationName}" readOnly="${true}"/>
	      </td>
		</tr>
	    <tr>
	      <kul:htmlAttributeHeaderCell
		          labelFor="document.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToOrganizationCode"
		          literalLabel="Reports-To Chart/Org:"
		          horizontal="true"
		          />
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="document.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode"
	      		attributeEntry="${orgRptsAttributes.reportsToChartOfAccountsCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.Chart"
				    keyValues="chartOfAccountsCode=${KualiForm.document.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode}"
				    render="true">
			    	<html:hidden write="true" property="document.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="${orgRptsPropString}.reportsToOrganizationCode"
	      		attributeEntry="${orgRptsAttributes.reportsToOrganizationCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.budget.bo.BudgetConstructionOrganizationReports"
				    keyValues="chartOfAccountsCode=${orgRptsVals.reportsToChartOfAccountsCode}&amp;organizationCode=${orgRptsVals.reportsToOrganizationCode}"
				    render="true">
			    	<html:hidden write="true" property="${orgRptsPropString}.reportsToOrganizationCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="${orgRptsPropString}.reportsToOrganization.organizationName" attributeEntry="${organizationAttributes.organizationName}" readOnly="${true}"/>
	      </td>
		</tr>


    	<tr>
		    <td colspan="4" class="subhead">
			<span class="subhead-left">Approval Level Data</span>
		    </td>
	    </tr>
	    <tr>
	   	  <kul:htmlAttributeHeaderCell
	          labelFor="document.organizationLevelCode"
	          literalLabel="Current Level::"
	          horizontal="true"
	          />
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="document.organizationLevelCode" attributeEntry="${documentAttributes.organizationLevelCode}" readOnly="${true}"/>
	      </td>
	      <td>&nbsp;</td>
	      <td align="center" valign="middle">
		  	<c:if test="${KualiForm.document.organizationLevelCode == 0}">
				Account Level Update Access
		  	</c:if>
	      </td>
		</tr>
	    <tr>
	      <kul:htmlAttributeHeaderCell
	          labelFor="document.organizationLevelOrganizationCode"
	          literalLabel="Level Chart/Org:"
	          horizontal="true"
	          />
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="document.organizationLevelChartOfAccountsCode"
	      		attributeEntry="${documentAttributes.organizationLevelChartOfAccountsCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.Chart"
				    keyValues="chartOfAccountsCode=${KualiForm.document.organizationLevelChartOfAccountsCode}"
				    render="true">
			    	<html:hidden write="true" property="document.organizationLevelChartOfAccountsCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute
	      		property="document.organizationLevelOrganizationCode"
	      		attributeEntry="${documentAttributes.organizationLevelOrganizationCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.budget.bo.BudgetConstructionOrganizationReports"
				    keyValues="chartOfAccountsCode=${KualiForm.document.organizationLevelChartOfAccountsCode}&amp;organizationCode=${KualiForm.document.organizationLevelOrganizationCode}"
				    render="true">
			    	<html:hidden write="true" property="document.organizationLevelOrganizationCode" />
				</kul:inquiry>
	      	</kul:htmlControlAttribute>
	      </td>
	      <td align="center" valign="middle">
	      	<kul:htmlControlAttribute property="document.organizationLevelOrganization.organizationName" attributeEntry="${orgAttributes.organizationName}" readOnly="${true}"/>
	      </td>
		</tr>



    	<tr>
		    <td colspan="4" class="subhead">
			<span class="subhead-left">Controls</span>
		    </td>
	    </tr>

    	<tr>
          <td colspan="4" class="datacell" nowrap>
            <div align="center">
              <html:image property="methodToCall.performAccountPullup.anchorsystemControlsAnchor" src="images/buttonsmall_pullup.gif" title="Account Pull Up" alt="Account Pull Up" styleClass="tinybutton"/>&nbsp;&nbsp;&nbsp;
              <html:image property="methodToCall.performAccountPushdown.anchorsystemControlsAnchor" src="images/buttonsmall_pushdown.gif" title="Account Push Down" alt="Account Push Down" styleClass="tinybutton" />&nbsp;&nbsp;&nbsp;
              <html:image property="methodToCall.performReportDump.anchorsystemControlsAnchor" src="images/buttonsmall_reportdump.gif" title="Account Report/Dump" alt="Account Report/Dump" styleClass="tinybutton"/>
            </div>
          </td>
	    </tr>
	    
	</table>
</div>
</kul:tab>
