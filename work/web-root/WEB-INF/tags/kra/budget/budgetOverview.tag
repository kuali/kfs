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

<div align="right">
	<kul:help documentTypeName="${DataDictionary.KualiBudgetDocument.documentTypeName}" pageName="${KraConstants.OVERVIEW_HEADER_TAB}" altText="page help"/>
</div>

  <kra-b:budgetDetailSelection includeSummary="true" />

        <div align="center">
          <table class="t3" border="0" cellpadding="0" cellspacing="0" width="100%">

            <tbody>
              <tr>
                <td><img src="images/pixel_clear.gif" alt="" class="tl3" height="12" width="12"></td>
                <td align="right"><img src="images/pixel_clear.gif" alt="" class="tr3" height="12" width="12"></td>
              </tr>
            </tbody>
          </table>
          <div id="workarea" >
          <div class="tab-container" align="center">
            <table class="datatable" cellpadding="0" cellspacing="0">

              <tbody><tr>
                <td colspan="4" class="subhead"><span class="subhead-left">Personnel Expenses&nbsp;<html:image src="images/edit.gif" styleClass="tinybutton" property="methodToCall.headerTab.headerDispatch.overview.navigateTo.personnel" alt="Personnel"/></span> </td>
                <td colspan="2" class="subhead style1"><div align="center"><span class="subhead-right"><span class="nowrap"><strong> Amount Requested</strong></span></span> </div></td>
                <td colspan="2" class="subhead"><div align="center"><span class="subhead-right"><span class="nowrap"><strong> Institution CS</strong><br><c:if test="${!KualiForm.document.budget.institutionCostShareIndicator}">Cost Share is set to No</c:if></span></span> </div></td>
                <td class="subhead"><div align="center"><span class="subhead-right"><span class="nowrap"><strong> 3rd Party CS</strong><br><c:if test="${!KualiForm.document.budget.budgetThirdPartyCostShareIndicator}">Cost Share is set to No</c:if></span> </span></div></td>
              </tr></tbody>
              
              <!-- Defined here because can't pass a fmt:formatNumber to kra:budgetExpensesRow -->
              <c:set var="personnelSalaryAgencyRequest">
                <fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.personnelSalaryAgencyRequest}" type="currency" currencySymbol="" maxFractionDigits="0" />
              </c:set>
              <c:set var="personnelSalaryInstitutionCostShare">
                <fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.personnelSalaryInstitutionCostShare}" type="currency" currencySymbol="" maxFractionDigits="0" />
              </c:set>
              <c:set var="personnelFringeBenefitsAgencyRequest">
                <fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.personnelFringeBenefitsAgencyRequest}" type="currency" currencySymbol="" maxFractionDigits="0" />
              </c:set>
              <c:set var="personnelFringeBenefitsInstitutionCostShare">
                <fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.personnelFringeBenefitsInstitutionCostShare}" type="currency" currencySymbol="" maxFractionDigits="0" />
              </c:set>
              
              <kra-b:budgetExpensesRow tabTitle="Personnel - Salary" defaultOpen="false" amountRequested="${personnelSalaryAgencyRequest}" institutionCostShare="${personnelSalaryInstitutionCostShare}" thirdPartyCostShare="--">
                <tr>
                  <td class="infoline" width="5%">&nbsp;</td>
                  <td class="infoline"><strong>Name </strong></td>
                  <td class="infoline"><strong>Role </strong></td>
                  <td class="infoline"><strong> Appt Type </strong></td>
                  <td class="infoline"><div align="center"><strong> Effort </strong></div></td>
                  <td class="infoline"><div align="center"><strong> Amount</strong></div></td>
                  <td class="infoline"><div align="center"><strong> Effort </strong></div></td>
                  <td class="infoline"><div align="center"><strong> Amount</strong></div></td>
                  <td class="infoline"><div align="center"><strong> Amount</strong></div></td>
                </tr>
                <logic:iterate id="person" name="KualiForm" property="budgetOverviewFormHelper.budgetOverviewPersonnelHelpers">
	                <tr>
	                  <td class="datacell" width="5%">&nbsp;</td>
	                  <td class="datacell">${person.personName}</td>
	                  <td class="datacell"><c:choose><c:when test="${empty person.role}">&nbsp;</c:when><c:otherwise>${person.role}</c:otherwise></c:choose></td>
	                  <td class="datacell">${person.appointmentTypeDescription}</td>
	                  <td class="datacell" align="right"><div align="center"><c:choose><c:when test="${person.hourlyAppointmentType}">${person.userAgencyHours} hrs.</c:when><c:when test="${KualiForm.currentPeriodNumber ne 0 and KualiForm.currentTaskNumber ne 0}"><fmt:formatNumber value="${person.agencyPercentEffortAmount}" type="number" maxFractionDigits="0" />%</c:when><c:otherwise>&nbsp;</c:otherwise></c:choose></div></td>
	                  <td class="datacell" align="right"><div align="right"><fmt:formatNumber value="${person.agencyRequestTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></div></td>
	                  <td class="datacell" align="right"><div align="center"><c:choose><c:when test="${person.hourlyAppointmentType}">${person.userInstitutionHours} hrs.</c:when><c:when test="${KualiForm.currentPeriodNumber ne 0 and KualiForm.currentTaskNumber ne 0}"><fmt:formatNumber value="${person.institutionCostSharePercentEffortAmount}" type="number" maxFractionDigits="0" />%</c:when><c:otherwise>&nbsp;</c:otherwise></c:choose></div></td>
	                  <td class="datacell" align="right"><div align="right"><fmt:formatNumber value="${person.institutionCostShareRequestTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></div></td>
	                  <td class="datacell" align="right"><div align="right">--</div></td>
	                </tr>
                </logic:iterate>
                <tr>
                  <td class="datacell-top" width="5%">&nbsp;</td>
                  <td colspan="3" class="datacell-top">Total Salary </td>
                  <td colspan="2" class="datacell-top" align="right"><div align="right"><b>${personnelSalaryAgencyRequest}</b> </div></td>
                  <td colspan="2" class="datacell-top" align="right"><div align="right"><b>${personnelSalaryInstitutionCostShare}</b> </div></td>
                  <td class="datacell-top" align="right"><div align="right"><b>--</b> </div></td>
                </tr>
              </kra-b:budgetExpensesRow>

              <kra-b:budgetExpensesRow tabTitle="Personnel - Fringe Benefits" defaultOpen="false" amountRequested="${personnelFringeBenefitsAgencyRequest}" institutionCostShare="${personnelFringeBenefitsInstitutionCostShare}" thirdPartyCostShare="--">
                <tr>
                  <td class="infoline">&nbsp;</td>
                  <td colspan="2" class="infoline"><strong>Name </strong></td>
                  <td class="infoline"><strong> Appt Type </strong></td>
                  <td class="infoline"><div align="center"><strong> Rate </strong></div></td>
                  <td class="infoline"><div align="center"><strong> Amount</strong></div></td>
                  <td class="infoline"><div align="center"><strong> Rate </strong></div></td>
                  <td class="infoline"><div align="center"><strong> Amount</strong></div></td>
                  <td class="infoline"><div align="center"><strong> Amount</strong></div></td>
                </tr>
                <logic:iterate id="person" name="KualiForm" property="budgetOverviewFormHelper.budgetOverviewPersonnelHelpers">
	                <tr>
	                  <td class="datacell" align="left">&nbsp;</td>
	                  <td colspan="2" class="datacell" align="left">${person.personName}</td>
	                  <td class="datacell">${person.appointmentTypeDescription}</td>
	                  <td class="datacell" align="right"><div align="center"><fmt:formatNumber value="${person.contractsAndGrantsFringeRateAmount}" type="number" maxFractionDigits="2" />%</div></td>
	                  <td class="datacell" align="right"><div align="right"><fmt:formatNumber value="${person.agencyFringeBenefitTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></div></td>
	                  <td class="datacell" align="right"><div align="center"><fmt:formatNumber value="${person.institutionCostShareFringeRateAmount}" type="number" maxFractionDigits="2" />%</div></td>
	                  <td class="datacell" align="right"><div align="right"><fmt:formatNumber value="${person.institutionCostShareFringeBenefitTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></div></td>
	                  <td class="datacell" align="right"><div align="right">--</div></td>
	                </tr>
                </logic:iterate>
                <tr>
                  <td class="datacell-top">&nbsp;</td>
                  <td colspan="3" class="datacell-top">Total Fringe Benefits </td>
                  <td colspan="2" class="datacell-top" align="right"><div align="right"><b>${personnelFringeBenefitsAgencyRequest}</b> </div></td>
                  <td colspan="2" class="datacell-top" align="right"><div align="right"><b>${personnelFringeBenefitsInstitutionCostShare}</b> </div></td>
                  <td class="datacell-top" align="right"><div align="right"><b>--</b> </div></td>
                </tr>
              </kra-b:budgetExpensesRow>

              <tbody><tr>
                <td class="infoline">&nbsp;</td>
                <td colspan="3" class="infoline"><strong>Total Personnel </strong> </td>
                <td colspan="2" class="infoline" align="right"><div align="right"><b><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.totalPersonnelAgencyRequest}" type="currency" currencySymbol="" maxFractionDigits="0" /></b></div></td>
                <td colspan="2" class="infoline" align="right"><div align="right"><b><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.totalPersonnelInstitutionCostShare}" type="currency" currencySymbol="" maxFractionDigits="0" /></b> </div></td>
                <td class="infoline" align="right"><div align="right"><b>--</b> </div></td>
              </tr>
              <tr>
                <td colspan="4" class="subhead"><span class="subhead-left">Non-Personnel Expenses&nbsp;<html:image src="images/edit.gif" styleClass="tinybutton" property="methodToCall.headerTab.headerDispatch.overview.navigateTo.nonpersonnel" alt="Nonpersonnel"/></span> </td>
                <td colspan="2" class="subhead style1"><div align="center"><span class="subhead-right"><span class="nowrap"><strong> Amount Requested</strong></span></span> </div></td>
                <td colspan="2" class="subhead"><div align="center"><span class="subhead-right"><span class="nowrap"><strong> Institution CS</strong><br><c:if test="${!KualiForm.document.budget.institutionCostShareIndicator}">Cost Share is set to No</c:if></span></span> </div></td>
                <td class="subhead"><div align="center"><span class="subhead-right"><span class="nowrap"><strong> 3rd Party CS</strong><br><c:if test="${!KualiForm.document.budget.budgetThirdPartyCostShareIndicator}">Cost Share is set to No</c:if></span> </span></div></td>
              </tr>
              </tbody>
              
              <logic:iterate id="nonpersonnelCategory" name="KualiForm" property="nonpersonnelCategories" indexId="i">
                  <!-- Defined here because can't pass a fmt:formatNumber to kra:budgetExpensesRow -->
                  <c:set var="nonpersonnelAmountRequested">
                    <fmt:formatNumber value="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelCategoryHelperMap[nonpersonnelCategory.code].agencyTotal}" type="currency" currencySymbol="" maxFractionDigits="0" />
                  </c:set>
                  <c:set var="nonpersonnelInstitutionCostShare">
                    <c:choose><c:when test="${KualiForm.document.budget.institutionCostShareIndicator}"><fmt:formatNumber value="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelCategoryHelperMap[nonpersonnelCategory.code].univCostShareTotal}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose>
                  </c:set>
                  <c:set var="nonpersonnelThirdPartyCostShare">
                    <c:choose><c:when test="${KualiForm.document.budget.budgetThirdPartyCostShareIndicator}"><fmt:formatNumber value="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelCategoryHelperMap[nonpersonnelCategory.code].thirdPartyCostShareTotal}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose>
                  </c:set>

                  <kra-b:budgetExpensesRow tabTitle="${nonpersonnelCategory.name}" defaultOpen="false" amountRequested="${nonpersonnelAmountRequested}" institutionCostShare="${nonpersonnelInstitutionCostShare}" thirdPartyCostShare="${nonpersonnelThirdPartyCostShare}">
		                <logic:iterate id="nonpersonnelItem"  name="KualiForm" property="document.budget.nonpersonnelItems" indexId="ctr">
			                <c:if test="${nonpersonnelItem.budgetNonpersonnelCategoryCode eq nonpersonnelCategory.code
			                              and ((KualiForm.currentPeriodNumber eq 0 and KualiForm.currentTaskNumber eq 0)
			                                   or (KualiForm.currentPeriodNumber eq 0 and KualiForm.currentTaskNumber eq nonpersonnelItem.budgetTaskSequenceNumber)
			                                   or (KualiForm.currentPeriodNumber eq nonpersonnelItem.budgetPeriodSequenceNumber and KualiForm.currentTaskNumber eq 0)
			                                   or (KualiForm.currentPeriodNumber eq nonpersonnelItem.budgetPeriodSequenceNumber and KualiForm.currentTaskNumber eq nonpersonnelItem.budgetTaskSequenceNumber))}">
				                <tr>
					                <td class="datacell" align="left">&nbsp;</td>
					                <td colspan="3" class="datacell" align="left">${nonpersonnelItem.nonpersonnelObjectCode.nonpersonnelSubCategory.name} - ${nonpersonnelItem.budgetNonpersonnelDescription}</td>
					                <td colspan="2" class="datacell" align="right"><div align="right"><fmt:formatNumber value="${nonpersonnelItem.agencyRequestAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></div></td>
					                <td colspan="2" class="datacell" align="right"><div align="right"><c:choose><c:when test="${KualiForm.document.budget.institutionCostShareIndicator}"><fmt:formatNumber value="${nonpersonnelItem.budgetInstitutionCostShareAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose></div></td>
					                <td class="datacell" align="right"><div align="right"><c:choose><c:when test="${KualiForm.document.budget.budgetThirdPartyCostShareIndicator}"><fmt:formatNumber value="${nonpersonnelItem.budgetThirdPartyCostShareAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose></div></td>
				                </tr>
			                </c:if>
		                </logic:iterate>
		                <tr>
                      <td class="datacell-top">&nbsp;</td>
                      <td colspan="3" class="datacell-top">Total ${nonpersonnelCategory.name}</td>
                      <td colspan="2" class="datacell-top" align="right"><div align="right"><b>${nonpersonnelAmountRequested}</b> </div></td>
                      <td colspan="2" class="datacell-top" align="right"><div align="right"><b><c:choose><c:when test="${KualiForm.document.budget.institutionCostShareIndicator}">${nonpersonnelInstitutionCostShare}</c:when><c:otherwise>--</c:otherwise></c:choose></b> </div></td>
                      <td class="datacell-top" align="right"><div align="right"><b><c:choose><c:when test="${KualiForm.document.budget.budgetThirdPartyCostShareIndicator}">${nonpersonnelThirdPartyCostShare}</c:when><c:otherwise>--</c:otherwise></c:choose></b> </div></td>
                    </tr>
                  </kra-b:budgetExpensesRow>
              </logic:iterate>

              <tbody><tr>
                <td class="infoline">&nbsp;</td>
                <td colspan="3" class="infoline"><strong>Total Non-Personnel Expenses </strong></td>
                <td colspan="2" class="infoline" align="right"><div align="right"><b><fmt:formatNumber value="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelAgencyTotal}" type="currency" currencySymbol="" maxFractionDigits="0" /></b> </div></td>
                <td colspan="2" class="infoline" align="right"><div align="right"><b><c:choose><c:when test="${KualiForm.document.budget.institutionCostShareIndicator}"><fmt:formatNumber value="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelUnivCostShareTotal}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose></b> </div></td>
                <td class="infoline" align="right"><div align="right"><b><c:choose><c:when test="${KualiForm.document.budget.budgetThirdPartyCostShareIndicator}"><fmt:formatNumber value="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelThirdPartyCostShareTotal}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose></b> </div></td>
              </tr>
              <tr>
                <td colspan="4" class="subhead"><span class="subhead-left">Summary</span> </td>
                <td colspan="2" class="subhead style1"><div align="center"><span class="subhead-right"><span class="nowrap"><strong> Amount Requested</strong></span></span> </div></td>
                <td colspan="2" class="subhead"><div align="center"><span class="subhead-right"><span class="nowrap"><strong> Institution CS</strong><br><c:if test="${!KualiForm.document.budget.institutionCostShareIndicator}">Cost Share is set to No</c:if></span></span> </div></td>
                <td class="subhead"><div align="center"><span class="subhead-right"><span class="nowrap"><strong> 3rd Party CS</strong><br><c:if test="${!KualiForm.document.budget.budgetThirdPartyCostShareIndicator}">Cost Share is set to No</c:if></span> </span></div></td>
              </tr>
              <tr>
                <td class="infoline">&nbsp;</td>
                <td colspan="3" class="infoline"><strong>TOTAL DIRECT COSTS</strong></td>
                <td colspan="2" class="infoline"><div align="right"><strong><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.totalDirectCostsAgencyRequest}" type="currency" currencySymbol="" maxFractionDigits="0" /></strong> </div></td>
                <td colspan="2" class="infoline"><div align="right"><strong><c:choose><c:when test="${KualiForm.document.budget.institutionCostShareIndicator}"><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.totalDirectCostsInstitutionCostShare}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose></strong> </div></td>
                <td class="infoline"><div align="right"><strong><c:choose><c:when test="${KualiForm.document.budget.budgetThirdPartyCostShareIndicator}"><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.totalDirectThirdPartyCostShare}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose></strong> </div></td>
              </tr>
              <c:if test="${KualiForm.budgetOverviewFormHelper.overviewShowModular}">
                <tr>
                  <td class="infoline">&nbsp;</td>
                  <td colspan="3" class="infoline"><strong>MODULAR ADJUSTMENT</strong></td>
                  <td colspan="2" class="infoline"><div align="right"><strong><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.modularAdjustmentAgencyRequest}" type="currency" currencySymbol="" maxFractionDigits="0" /></strong> </div></td>
                  <td colspan="2" class="infoline"><div align="right"><strong>--</strong> </div></td>
                  <td class="infoline"><div align="right"><strong>--</strong> </div></td>
                </tr>
                <tr>
                  <td class="infoline">&nbsp;</td>
                  <td colspan="3" class="infoline"><strong>ADJUSTED DIRECT COSTS</strong></td>
                  <td colspan="2" class="infoline"><div align="right"><strong><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.adjustedDirectCostsAgencyRequest}" type="currency" currencySymbol="" maxFractionDigits="0" /></strong> </div></td>
                  <td colspan="2" class="infoline"><div align="right"><strong>--</strong> </div></td>
                  <td class="infoline"><div align="right"><strong>--</strong> </div></td>
                </tr>
              </c:if>
              <tr>
                <td class="infoline">&nbsp;</td>
                <td colspan="3" class="infoline"><strong>TOTAL INDIRECT COSTS</strong></td>
                <td colspan="2" class="infoline"><div align="right"><strong><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.totalIndirectCostsAgencyRequest}" type="currency" currencySymbol="" maxFractionDigits="0" /></strong> </div></td>
                <td colspan="2" class="infoline"><div align="right"><strong><c:choose><c:when test="${KualiForm.document.budget.institutionCostShareIndicator}"><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.totalIndirectCostsInstitutionCostShare}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose></strong> </div></td>
                <td class="infoline"><div align="right"><strong>--</strong> </div></td>
              </tr>
              <tr>
                <td class="infoline">&nbsp;</td>
                <td colspan="3" class="infoline"><strong>TOTAL COSTS </strong></td>
                <td colspan="2" class="infoline"><div align="right"><strong><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.totalCostsAgencyRequest}" type="currency" currencySymbol="" maxFractionDigits="0" /></strong> </div></td>
                <td colspan="2" class="infoline"><div align="right"><strong><strong><c:choose><c:when test="${KualiForm.document.budget.institutionCostShareIndicator}"><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.totalCostsInstitutionCostShare}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose></strong> </div></td>
                <td class="infoline"><div align="right"><strong><c:choose><c:when test="${KualiForm.document.budget.budgetThirdPartyCostShareIndicator}"><fmt:formatNumber value="${KualiForm.budgetOverviewFormHelper.totalCostsThirdPartyCostShare}" type="currency" currencySymbol="" maxFractionDigits="0" /></c:when><c:otherwise>--</c:otherwise></c:choose></strong> </div></td>
              </tr>

            </tbody>

            </table>
          </div>
          <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
          <tr>
            <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
            <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
          </tr>
        </table>
        </div>
        </div>
 