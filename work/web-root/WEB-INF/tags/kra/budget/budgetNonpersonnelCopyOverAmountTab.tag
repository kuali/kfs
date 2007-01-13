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

<%@ attribute name="tabTitle" required="true" %>
<%@ attribute name="copyIndicatorLabel" required="true" %>
<%@ attribute name="amountType" required="true" %>
<%@ attribute name="transparentBackground" required="false" %>

<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>

    <kul:tab tabTitle="${tabTitle}" tabDescription=" " transparentBackground="${transparentBackground}" defaultOpen="false">
      <div class="tab-container" id="G02" style="" align="center">
          <table class="datatable" align="center" border="0" cellpadding="4" cellspacing="1">
            <tbody>
              <tr>
                <td colspan="99" class="subhead">Agency Amount Requested </td>
              </tr>
              <tr>
                <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetNonpersonnel.attributes.budgetNonpersonnelSubCategoryCode}" skipHelpUrl="true" noColon="true" readOnly="true" /></th>
                <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetNonpersonnel.attributes.budgetNonpersonnelDescription}" skipHelpUrl="true" noColon="true" readOnly="true" /></th>
                <c:forEach items="${KualiForm.document.budget.periods}" var="period" varStatus="status">
	                <th colspan="2" class="bord-l-b" align="center"> ${status.index + 1} </th>
                </c:forEach>
              </tr>

              <c:forEach items="${KualiForm.budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelpers[KualiForm.currentNonpersonnelCategoryCode].nprsItems}" var="nprsItem" varStatus="iStatus">
	              <tr>
	                <td class="datacell" align="left"> ${nprsItem.nonpersonnelSubCategoryName} </td>
	                <td class="datacell" align="left"> ${nprsItem.budgetNonpersonnelDescription}</td>
                  <c:forEach items="${nprsItem.periodAmounts}" var="periodAmount" varStatus="jStatus"> <!-- this better equal the number of periods -->
  	                <td class="infoline" align="center" width="5"><div align="right">
                      <c:choose>
                        <c:when test="${periodAmount.budgetNonpersonnelSequenceNumber == -1}">
                          <!-- display disabled: 1. items before origin -->
                          <input type="checkbox" disabled="true" />
		                    </c:when>
                        <c:when test="${periodAmount.originItem || viewOnly}">
                          <!-- display disabled: 2. origin item -->
                          <html:checkbox disabled="true" property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${KualiForm.currentNonpersonnelCategoryCode}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].${copyIndicatorLabel}" />
                          <html:hidden property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${KualiForm.currentNonpersonnelCategoryCode}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].${copyIndicatorLabel}" />
		                    </c:when>
                        <c:otherwise>
				                  <html:checkbox property="budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelper(${KualiForm.currentNonpersonnelCategoryCode}).nprsItem[${iStatus.index}].periodAmount[${jStatus.index}].${copyIndicatorLabel}" />
		                    </c:otherwise>
		                  </c:choose>
		                  </div></td>
				              <td class="datacell" align="center" width="45"><div id="p1/s1-1" align="right">
				              <c:choose>
				                <c:when test="${amountType == 'agency'}">
      		                <fmt:formatNumber value="${periodAmount.displayAgencyRequestAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
      		              </c:when>
      		              <c:when test="${amountType == 'institution'}">
      		                <fmt:formatNumber value="${periodAmount.displayBudgetInstitutionCostShareAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
      		              </c:when>
      		              <c:when test="${amountType == 'third'}">
      		                <fmt:formatNumber value="${periodAmount.displayBudgetThirdPartyCostShareAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
      		              </c:when>
      		              <c:otherwise>
      		                Unknown amountType!
       		              </c:otherwise>
    		              </c:choose>
				            </div></td>
                  </c:forEach>
	              </tr>
              </c:forEach>

              <tr>
                <td colspan="2" class="infoline"><br></td>
                <c:forEach items="${KualiForm.document.budget.periods}" var="period" varStatus="status">
                  <td colspan="2" class="infoline" align="center"><div id="periodTotal[1]" align="right"><strong>
			        <c:choose>
			          <c:when test="${empty KualiForm.budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelpers[KualiForm.currentNonpersonnelCategoryCode]}">
                        0
  		              </c:when>
			          <c:when test="${amountType == 'agency'}">
                        <fmt:formatNumber value="${KualiForm.budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelpers[KualiForm.currentNonpersonnelCategoryCode].agencyRequestAmountTotal[status.index]}" type="currency" currencySymbol="" maxFractionDigits="0" />
  		              </c:when>
  		              <c:when test="${amountType == 'institution'}">
                        <fmt:formatNumber value="${KualiForm.budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelpers[KualiForm.currentNonpersonnelCategoryCode].budgetInstitutionCostShareAmountTotal[status.index]}" type="currency" currencySymbol="" maxFractionDigits="0" />
  		              </c:when>
  		              <c:when test="${amountType == 'third'}">
                        <fmt:formatNumber value="${KualiForm.budgetNonpersonnelCopyOverFormHelper.nonpersonnelCopyOverCategoryHelpers[KualiForm.currentNonpersonnelCategoryCode].budgetThirdPartyCostShareAmountTotal[status.index]}" type="currency" currencySymbol="" maxFractionDigits="0" />
  		              </c:when>
  		              <c:otherwise>
  		                Unknown amountType!
   		              </c:otherwise>
		            </c:choose>
                  </strong></div></td>
                </c:forEach>
              </tr>
            </tbody>
          </table>

          <span class="gen-container"> <br>
          	<c:if test="${!viewOnly}">
          		<html:image property="methodToCall.update" src="images/tinybutton-recalculate.gif" styleClass="tinybutton" />
          	</c:if>
          </span></div>
    </kul:tab>
