<%--
 Copyright 2006 The Kuali Foundation.
 
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
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<c:set var="KraConstants" value="${KraConstants}" />

<div align="right">
  <kul:help securityGroupName="${KraConstants.KRA_ADMIN_GROUP_NAME}" parameterName="${KraConstants.BUDGET_NONPERSONNEL_COPY_OVER_HELP_PARAMETER_NAME}" altText="page help"/>
</div>

  <!-- A bonanza of hidden variables from nonpersonnel & copy over to assure non-saved items are retained. -->
  <html:hidden property="document.budget.institutionCostShareIndicator" />
  <html:hidden property="document.budget.budgetThirdPartyCostShareIndicator" />
  <html:hidden property="document.nonpersonnelNextSequenceNumber" />

  <logic:iterate id="nonpersonnelItem" name="KualiForm" property="document.budget.nonpersonnelItems" indexId="ctr">
	  <html:hidden property="document.budget.nonpersonnelItem[${ctr}].documentNumber" />
	  <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetTaskSequenceNumber" />
	  <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetPeriodSequenceNumber" />
	  <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelCategoryCode" />
	  <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelSequenceNumber" />
	  <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelSubCategoryCode" />
	  <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelDescription" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].agencyRequestAmount" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetThirdPartyCostShareAmount" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetInstitutionCostShareAmount" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetOriginSequenceNumber" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].agencyCopyIndicator" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetInstitutionCostShareCopyIndicator" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetThirdPartyCostShareCopyIndicator" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetOriginAgencyAmount" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetOriginInstitutionCostShareAmount" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetOriginThirdPartyCostShareAmount" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].subcontractorNumber" />
    <html:hidden property="document.budget.nonpersonnelItem[${ctr}].versionNumber" />
  </logic:iterate>

  <logic:iterate id="nonpersonnelCategory" name="KualiForm" property="nonpersonnelCategories" indexId="i">
    <c:forEach items="${KualiForm.budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelpers[nonpersonnelCategory.code].nprsItems}" var="nprsItem" varStatus="iStatus">
	    <c:forEach items="${nprsItem.periodAmounts}" var="period" varStatus="jStatus">
	    	<c:if test="${!empty period}"> <!-- We can have empty spots, skip those. -->
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nonpersonnelCategoryCode" />
		
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].nonpersonnelSubCategoryName" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].budgetNonpersonnelDescription" />
		
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].documentNumber" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetTaskSequenceNumber" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetPeriodSequenceNumber" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetNonpersonnelCategoryCode" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetNonpersonnelSequenceNumber" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetNonpersonnelSubCategoryCode" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetNonpersonnelDescription" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].agencyRequestAmount" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetThirdPartyCostShareAmount" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetInstitutionCostShareAmount" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetOriginSequenceNumber" />
	
		      <!-- currentNonpersonnelCategoryCode is displayed on this page, these fields are user interactible. Don't put as hiddens. -->
		      <c:if test="${nonpersonnelCategory.code ne KualiForm.currentNonpersonnelCategoryCode}">
		        <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].agencyCopyIndicator" />
		        <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetInstitutionCostShareCopyIndicator" />
		        <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetThirdPartyCostShareCopyIndicator" />
		      </c:if>
	
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetOriginAgencyAmount" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetOriginInstitutionCostShareAmount" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetOriginThirdPartyCostShareAmount" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].subcontractorNumber" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetInflatedAgencyAmount" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetInflatedInstitutionCostShareAmount" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].budgetInflatedThirdPartyCostShareAmount" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].versionNumber" />
		      <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${nonpersonnelCategory.code}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].nonpersonnelObjectCode.nonpersonnelSubCategory.name" />
        </c:if>
	    </c:forEach>
    </c:forEach>
  </logic:iterate>

  <!-- Hidden Variables for categories. -->
	<logic:iterate id="nonpersonnelCategory" name="KualiForm" property="nonpersonnelCategories" indexId="i">
	  <html:hidden property="nonpersonnelCategory[${i}].name" />
		<html:hidden property="nonpersonnelCategory[${i}].code" />
		<logic:iterate id="nonpersonnelObjectCode" name="nonpersonnelCategory" property="nonpersonnelObjectCodes" indexId="j">
		  <html:hidden property="nonpersonnelCategory[${i}].nonpersonnelObjectCode[${j}].nonpersonnelSubCategory.code" />
			<html:hidden property="nonpersonnelCategory[${i}].nonpersonnelObjectCode[${j}].nonpersonnelSubCategory.name" />
		</logic:iterate>
  </logic:iterate>

  <!-- "Select View:" box -->
        <div class="annotate">
          <table class="annotate-top" cellpadding="0" cellspacing="0" width="100%">
            <tbody><tr>
              <td class="annotate-t"><img src="images/annotate-tl1.gif" alt="" class="annotate-t" align="middle" height="24" width="12">Select View:</td>
              <td class="annotate-t"><div align="right"><img src="images/annotate-tr1.gif" alt="" align="middle" height="24" width="12"></div></td>
            </tr>
          </tbody></table>
          <div class="annotate-container"> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetTask.attributes.budgetTaskName}" skipHelpUrl="true" readOnly="true" />
            <html:hidden property="currentTaskNumber" value="${KualiForm.currentTaskNumber}" /> <!-- Necessary because drop down is disabled. -->
            <html:hidden property="previousTaskNumber" value="${KualiForm.currentTaskNumber}" />
            <html:select property="currentTaskNumber" disabled="true">
              <c:set var="budgetTasks" value="${KualiForm.budgetDocument.budget.tasks}"/>
              <html:options collection="budgetTasks" property="budgetTaskSequenceNumber" labelProperty="budgetTaskName"/>
              <c:if test="${includeSummary && KualiForm.document.taskListSize > 1}"><html:option value="0">Summary</html:option></c:if>
            </html:select>

            <html:hidden property="currentPeriodNumber" value="${KualiForm.currentPeriodNumber}" /> <!-- Necessary because drop down is disabled. -->
            <html:hidden property="previousPeriodNumber" value="${KualiForm.currentPeriodNumber}" />

&nbsp;&nbsp; <kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetNonpersonnel.attributes.budgetNonpersonnelCategoryCode}" skipHelpUrl="true" readOnly="true" />
  			    <c:set var="budgetNonpersonnelCategories" value="${KualiForm.nonpersonnelCategories}"/>
            <html:select property="currentNonpersonnelCategoryCode" >
              <html:options collection="budgetNonpersonnelCategories" property="code" labelProperty="name"/>
            </html:select>

&nbsp; &nbsp;<html:image property="methodToCall.update" src="images/tinybutton-updateview.gif" align="middle" styleClass="tinybutton" alt="update"/></div>
          <table width="100%" cellpadding="0"  cellspacing="0" class="annotate-top">
            <tr>
              <td class="annotate-b"><img src="images/annotate-bl1.gif" alt="" width=12 height=24></td>
              <td class="annotate-b"><div align="right"><img src="images/annotate-br1.gif" alt="" width=12 height=24></div></td>
            </tr>
          </table>
        </div>

<div id="workarea">

  <!-- "Agency Amount Requested" tab -->
  <kra-b:budgetNonpersonnelCopyOverAmountTab tabTitle="Agency Amount Requested" copyIndicatorLabel="agencyCopyIndicator" amountType="agency" transparentBackground="true"/>
    
  <!-- "Institution Cost Share" tab -->
  <c:if test="${!KualiForm.document.budget.institutionCostShareIndicator}">
	<kul:tab tabTitle="Institution Cost Share" tabDescription="None" defaultOpen="false" transparentBackground="false">
      <div class="tab-container" align="center">
          <div class="h2-container"> <span class="subhead-left">
              <h2>Institution Cost Share</h2>
              </span> </div>
            <table cellpadding=0  summary="view/edit document overview information">
          <tr>
            <td height="70" align=left valign=middle class="datacell"><div align="center">
                Not Included In This Budget
                </div></td>
          </tr>
        </table>
      </div>
	</kul:tab>
  </c:if>
  <c:if test="${KualiForm.document.budget.institutionCostShareIndicator}">
    <kra-b:budgetNonpersonnelCopyOverAmountTab tabTitle="Institution Cost Share" copyIndicatorLabel="budgetInstitutionCostShareCopyIndicator" amountType="institution" transparentBackground="false"/>
  </c:if>

  <!-- "3rd Party Cost Share" tab -->
  <c:if test="${!KualiForm.document.budget.budgetThirdPartyCostShareIndicator}">
	<kul:tab tabTitle="3rd Party Cost Share" tabDescription="None" defaultOpen="false" transparentBackground="false">
      <div class="tab-container" align="center">
          <div class="h2-container"> <span class="subhead-left">
              <h2>3rd Party Cost Share</h2>
              </span> </div>
            <table cellpadding=0  summary="view/edit document overview information">
          <tr>
            <td height="70" align=left valign=middle class="datacell"><div align="center">
                Not Included In This Budget
                </div></td>
          </tr>
        </table>
      </div>
	</kul:tab>
  </c:if>
  <c:if test="${KualiForm.document.budget.budgetThirdPartyCostShareIndicator}">
    <kra-b:budgetNonpersonnelCopyOverAmountTab tabTitle="3rd Party Cost Share" copyIndicatorLabel="budgetThirdPartyCostShareCopyIndicator" amountType="third" transparentBackground="false"/>
  </c:if>
  
        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
          <tr>
            <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
            <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
          </tr>
        </table>
    </div>
