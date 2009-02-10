<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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

<c:set var="CGConstants" value="${CGConstants}" />

<div align="right">
  <kul:help securityGroupName="${CGConstants.KRA_ADMIN_GROUP_NAME}" parameterName="${CGConstants.BUDGET_NONPERSONNEL_COPY_OVER_HELP_PARAMETER_NAME}" altText="page help"/>
</div>

  <!-- A bonanza of hidden variables from nonpersonnel & copy over to assure non-saved items are retained. -->

  <!-- "Select View:" box -->
        <div class="annotate">
          <table class="annotate-top" cellpadding="0" cellspacing="0" width="100%">
            <tbody><tr>
              <td class="annotate-t"><img src="${ConfigProperties.kr.externalizable.images.url}annotate-tl1.gif" alt="" class="annotate-t" align="middle" height="24" width="12">Select View:</td>
              <td class="annotate-t"><div align="right"><img src="${ConfigProperties.kr.externalizable.images.url}annotate-tr1.gif" alt="" align="middle" height="24" width="12"></div></td>
            </tr>
          </tbody></table>
          <div class="annotate-container"> <kul:htmlAttributeLabel labelFor="currentTaskNumber" attributeEntry="${DataDictionary.BudgetTask.attributes.budgetTaskName}" skipHelpUrl="true" readOnly="true" />
            <html:hidden property="currentTaskNumber" value="${KualiForm.currentTaskNumber}" /> <!-- Necessary because drop down is disabled. -->
            <html:hidden property="previousTaskNumber" value="${KualiForm.currentTaskNumber}" />
            <html:select styleId="currentTaskNumber" property="currentTaskNumber" disabled="true">
              <c:set var="budgetTasks" value="${KualiForm.budgetDocument.budget.tasks}"/>
              <html:options collection="budgetTasks" property="budgetTaskSequenceNumber" labelProperty="budgetTaskName"/>
              <c:if test="${includeSummary && KualiForm.document.taskListSize > 1}"><html:option value="0">Summary</html:option></c:if>
            </html:select>

            <html:hidden property="currentPeriodNumber" value="${KualiForm.currentPeriodNumber}" /> <!-- Necessary because drop down is disabled. -->
            <html:hidden property="previousPeriodNumber" value="${KualiForm.currentPeriodNumber}" />

&nbsp;&nbsp; <kul:htmlAttributeLabel labelFor="currentNonpersonnelCategoryCode" attributeEntry="${DataDictionary.BudgetNonpersonnel.attributes.budgetNonpersonnelCategoryCode}" skipHelpUrl="true" readOnly="true" />
  			    <c:set var="budgetNonpersonnelCategories" value="${KualiForm.nonpersonnelCategories}"/>
            <html:select styleId="currentNonpersonnelCategoryCode" property="currentNonpersonnelCategoryCode" >
              <html:options collection="budgetNonpersonnelCategories" property="code" labelProperty="name"/>
            </html:select>

&nbsp; &nbsp;<html:image property="methodToCall.update" src="${ConfigProperties.externalizable.images.url}tinybutton-updateview.gif" align="middle" styleClass="tinybutton" title="update" alt="update"/></div>
          <table width="100%" cellpadding="0"  cellspacing="0" class="annotate-top">
            <tr>
              <td class="annotate-b"><img src="${ConfigProperties.kr.externalizable.images.url}annotate-bl1.gif" alt="" width=12 height=24></td>
              <td class="annotate-b"><div align="right"><img src="${ConfigProperties.kr.externalizable.images.url}annotate-br1.gif" alt="" width=12 height=24></div></td>
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
              <h3>Institution Cost Share</h3>
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
              <h3>3rd Party Cost Share</h3>
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
            <td align="left" class="footer"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
            <td align="right" class="footer-right"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
          </tr>
        </table>
    </div>
