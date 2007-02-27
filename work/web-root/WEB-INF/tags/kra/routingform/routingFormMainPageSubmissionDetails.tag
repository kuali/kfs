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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:set var="routingFormAttributes" value="${DataDictionary.KualiRoutingFormDocument.attributes}" />
<c:set var="routingFormBudgetAttributes" value="${DataDictionary.RoutingFormBudget.attributes}" />
<c:set var="routingFormProjectTypeAttributes" value="${DataDictionary.RoutingFormProjectType.attributes}" />
<c:set var="routingFormKeywordAttributes" value="${DataDictionary.RoutingFormKeyword.attributes}" />
<c:set var="contractGrantProposalAttributes" value="${DataDictionary.ContractGrantProposal.attributes}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>
<c:set var="budgetLinked" value="${KualiForm.editingMode['budgetLinked']}"/>

<kul:tab tabTitle="Submission Details" defaultOpen="true" tabErrorKey="newRoutingFormKeyword*,document.contractGrantProposal*,document.projectAbstract,document.routingFormProjectTitle,document.routingFormBudget*" auditCluster="mainPageAuditErrors" tabAuditKey="document.routingFormBudget*,document.submissionTypeCode,document.previousFederalIdentifier,document.routingFormPurposeCode,document.researchTypeCode,document.routingFormOtherPurposeDescription,document.routingFormProjectTitle,document.projectAbstract,document.routingFormProjectTypes*,document.projectTypeOtherDescription,document.routingFormPriorGrantNumber,document.grantNumber">

          <div class="tab-container" align="center">
            <div class="h2-container">
              <h2>Other Details </h2>
            </div>
            <table cellpadding="0" cellspacing="0" summary="view/edit document overview information">

              <tr>
                <td colspan=4 class="tab-subhead"><span class="left"><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.submissionTypeCode}" skipHelpUrl="true" noColon="true"/></span> </td>
              </tr>
              <tr>
                <th align=right valign=middle>Type:</th>
                <td colspan="3" align=left valign=middle nowrap >
                  <c:forEach items="${KualiForm.document.routingFormSubmissionTypes}" var="routingFormSubmissionType" varStatus="status"> 
		            <html:hidden property="document.routingFormSubmissionTypes[${status.index}].documentNumber" /> 
		            <html:hidden property="document.routingFormSubmissionTypes[${status.index}].submissionTypeCode" /> 
		            <html:hidden property="document.routingFormSubmissionTypes[${status.index}].versionNumber" /> 
		            <html:hidden property="document.routingFormSubmissionTypes[${status.index}].submissionType.submissionTypeDescription" /> 
                    <label>
                      <c:choose>
                        <c:when test="${!viewOnly}">
                          <html:radio property="document.submissionTypeCode" value="${routingFormSubmissionType.submissionTypeCode}" disabled="${viewOnly}"/>
                        </c:when>
                        <c:when test="${KualiForm.document.submissionTypeCode eq routingFormSubmissionType.submissionTypeCode}"> Yes </c:when>
    				    <c:otherwise> No </c:otherwise>
                      </c:choose>
		              ${routingFormSubmissionType.submissionType.submissionTypeDescription}
		            </label>
		            <c:if test="${routingFormSubmissionType.submissionTypeCode eq KraConstants.SUBMISSION_TYPE_CHANGE}">
		              &nbsp;<kul:htmlControlAttribute property="document.previousFederalIdentifier" attributeEntry="${routingFormAttributes.previousFederalIdentifier}" readOnly="${viewOnly}"/>
		            </c:if>
                  <br>
	              </c:forEach>
                </td>
              </tr>
              <tr>
                <td colspan=4 class="tab-subhead"><kul:htmlAttributeLabel attributeEntry="${routingFormProjectTypeAttributes.projectTypeCode}" skipHelpUrl="true" noColon="true"/></td>
              </tr>

              <tr>
                <th align=right valign=middle>Type:</th>
                <td colspan="3" align=left valign=middle >
                  <c:forEach items="${KualiForm.projectTypes}" varStatus="status">
                    <html:hidden property="projectTypes[${status.index}].projectTypeCode" />
                    <html:hidden property="projectTypes[${status.index}].projectTypeDescription" />
                  </c:forEach>
                  <c:forEach items="${KualiForm.document.routingFormProjectTypes}" varStatus="status">
                    <html:hidden property="document.routingFormProjectTypes[${status.index}].projectTypeCode" />
                    <html:hidden property="document.routingFormProjectTypes[${status.index}].documentNumber" />
                    <html:hidden property="document.routingFormProjectTypes[${status.index}].versionNumber" />
                  </c:forEach>
                  <table width="100%" cellspacing="0" cellpadding="0" class="nobord">
                    <tr>
                      <c:if test="${fn:length(KualiForm.projectTypes) != 0}">
				        <td class="nobord" width="50%" valign="top">
					      <c:set var="roundedEndIndex"><fmt:formatNumber value="${fn:length(KualiForm.projectTypes) / 2 - 1}" maxFractionDigits="0"/></c:set>
					      <kra-rf:routingFormMainPageSubmissionDetailsProjectTypes begin="0" end="${roundedEndIndex}"/>
                        </td>
					    <td class="nobord" width="50%" valign="top">
					      <c:set var="roundedBeginIndex"><fmt:formatNumber value="${fn:length(KualiForm.projectTypes) / 2}" maxFractionDigits="0"/></c:set>
					      <kra-rf:routingFormMainPageSubmissionDetailsProjectTypes begin="${roundedBeginIndex}" end="${fn:length(KualiForm.projectTypes)}"/>
                        </td>
                      </c:if>
                    </tr>
				  </table>
                      </td>
                    </tr>

                    <tr>
                <td colspan=4 class="tab-subhead">Reference Numbers</td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.routingFormPriorGrantNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormPriorGrantNumber" attributeEntry="${routingFormAttributes.routingFormPriorGrantNumber}" readOnly="${viewOnly}"/>
               	</td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.institutionAccountNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.institutionAccountNumber" attributeEntry="${routingFormAttributes.institutionAccountNumber}" readOnly="${viewOnly}"/>
                </td>
              </tr>
              <tr>

                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.federalIdentifier}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.federalIdentifier" attributeEntry="${routingFormAttributes.federalIdentifier}" readOnly="${viewOnly}"/>
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.grantsGovernmentConfirmationNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.grantsGovernmentConfirmationNumber" attributeEntry="${routingFormAttributes.grantsGovernmentConfirmationNumber}" readOnly="${viewOnly}"/>
                </td>
              </tr>
              <tr>

                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.grantNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.grantNumber" attributeEntry="${routingFormAttributes.grantNumber}" readOnly="${viewOnly}"/>
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${contractGrantProposalAttributes.proposalNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.contractGrantProposal.proposalNumber" attributeEntry="${contractGrantProposalAttributes.proposalNumber}" readOnly="${viewOnly}"/>
                </td>
              </tr>
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left"><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.routingFormPurposeCode}" skipHelpUrl="true" noColon="true"/></span> </td>
              </tr>
              <tr>
                <th align=right valign=middle>Type:</th>
                <td colspan="3" align=left valign=middle nowrap >
                  <c:forEach items="${KualiForm.document.routingFormPurposes}" var="routingFormPurpose" varStatus="status"> 
		            <html:hidden property="document.routingFormPurposes[${status.index}].documentNumber" /> 
		            <html:hidden property="document.routingFormPurposes[${status.index}].purposeCode" /> 
		            <html:hidden property="document.routingFormPurposes[${status.index}].versionNumber" /> 
		            <html:hidden property="document.routingFormPurposes[${status.index}].purpose.purposeDescription" /> 
                    <label>
                      <c:choose>
                        <c:when test="${!viewOnly and !budgetLinked}">
                          <html:radio property="document.routingFormPurposeCode" value="${routingFormPurpose.purposeCode}" disabled="${viewOnly}"/>
                        </c:when>
                        <c:when test="${KualiForm.document.routingFormPurposeCode eq routingFormPurpose.purposeCode}"> Yes </c:when>
    				    <c:otherwise> No </c:otherwise>
                      </c:choose>
		              ${routingFormPurpose.purpose.purposeDescription}
		            </label>
		            <c:choose>
		              <c:when test="${routingFormPurpose.purposeCode eq KraConstants.PURPOSE_RESEARCH}">
		                <c:forEach items="${KualiForm.document.routingFormResearchTypeCodes}" var="routingFormResearchTypeCode" varStatus="status"> 
		                  <html:hidden property="document.routingFormResearchTypeCodes[${status.index}].documentNumber" /> 
		                  <html:hidden property="document.routingFormResearchTypeCodes[${status.index}].researchTypeCode" /> 
		                  <html:hidden property="document.routingFormResearchTypeCodes[${status.index}].versionNumber" /> 
		                  <html:hidden property="document.routingFormResearchTypeCodes[${status.index}].researchType.researchTypeDescription" /> 
		                </c:forEach>
		                <kul:checkErrors keyMatch="document.researchTypeCode" auditMatch="document.researchTypeCode"/>
						<c:if test="${hasErrors==true}">
						  <c:set var="researchTypeCodeTextStyle" value="background-color: red"/>
						</c:if>
		                <html:select property="document.researchTypeCode" style="${researchTypeCodeTextStyle}" disabled="${viewOnly}"> 
		                  <html:option value="">select:</html:option> 
		                  <c:set var="routingFormResearchTypeCodes" value="${KualiForm.document.routingFormResearchTypeCodes}"/> 
		                  <html:options collection="routingFormResearchTypeCodes" property="researchTypeCode" labelProperty="researchType.researchTypeDescription"/> 
		                </html:select>
		              </c:when>
		              <c:when test="${routingFormPurpose.purposeCode eq KraConstants.PURPOSE_OTHER}">
		                &nbsp;<kul:htmlControlAttribute property="document.routingFormOtherPurposeDescription" attributeEntry="${routingFormAttributes.routingFormOtherPurposeDescription}" readOnly="${viewOnly}"/>
		              </c:when>
		            </c:choose>
                  <br>
	              </c:forEach>
                </td>
              </tr>
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Project Summary </span> </td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.routingFormProjectTitle}" skipHelpUrl="true" useShortLabel="true" /></th>

                <td colspan="3" align=left valign=middle nowrap >
                	<kul:htmlControlAttribute property="document.routingFormProjectTitle" attributeEntry="${routingFormAttributes.routingFormProjectTitle}" readOnly="${viewOnly}"/>
                </td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormKeywordAttributes.routingFormKeywordDescription}" skipHelpUrl="true" /></th>
                <td colspan="3" align=left valign=middle nowrap >

		            <table cellpadding="0" cellspacing="0" class="neutral">
		              <tr>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
						    	<html:hidden write="true" property="newRoutingFormKeyword.routingFormKeywordDescription" /> 
					    		<kul:lookup boClassName="org.kuali.module.kra.routingform.bo.Keyword" lookupParameters="newRoutingFormKeyword.routingFormKeywordDescription:routingFormKeywordDescription" fieldConversions="routingFormKeywordDescription:newRoutingFormKeyword.routingFormKeywordDescription" anchor="${currentTabIndex}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"><div align="center">
		                  <c:if test="${!viewOnly}">
		                    <html:image property="methodToCall.insertRoutingFormKeyword.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add keyword"/>
		                  </c:if>
		                </div></td>
		              </tr>   
		              
		              <c:forEach items = "${KualiForm.document.routingFormKeywords}" var="routingFormKeyword" varStatus="status"  >
					  <tr>
		                <td class="neutral"> <div align="left">
				    		<html:hidden write="true" property="document.routingFormKeywords[${status.index}].routingFormKeywordDescription" /> 
		                </div></td>
		                <td class="neutral"><div align="center">
		                  <c:if test="${!viewOnly}">
		                    <html:image property="methodToCall.deleteRoutingFormKeyword.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete research risk"/>
		                  </c:if>
		                </div></td>
		              </tr>   
		              </c:forEach>
		            </table>
                </td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.projectAbstract}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td colspan="3" align=left valign=middle nowrap ><kul:htmlControlAttribute property="document.projectAbstract" attributeEntry="${routingFormAttributes.projectAbstract}" readOnly="${viewOnly}"/></td>
              </tr>
            </table>

            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <td colspan=6 class="tab-subhead"><span class="left">Amounts &amp; Dates </span> </td>
              </tr>
              <tr>
                <th>&nbsp;</th>
                <th><div align="center"><kul:htmlAttributeLabel attributeEntry="${routingFormBudgetAttributes.routingFormBudgetDirectAmount}" skipHelpUrl="true" useShortLabel="true" noColon="true"/></div></th>
                <th><div align="center"><kul:htmlAttributeLabel attributeEntry="${routingFormBudgetAttributes.routingFormBudgetIndirectCostAmount}" skipHelpUrl="true" useShortLabel="true" noColon="true"/></div></th>
                <th>Total Costs</th>
                <th><kul:htmlAttributeLabel attributeEntry="${routingFormBudgetAttributes.routingFormBudgetStartDate}" skipHelpUrl="true" useShortLabel="true" noColon="true"/></th>
                <th><kul:htmlAttributeLabel attributeEntry="${routingFormBudgetAttributes.routingFormBudgetEndDate}" skipHelpUrl="true" useShortLabel="true" noColon="true"/></th>
              </tr>
              <tr>
                <th scope="row"><div align="right">Current Period:</div></th>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetDirectAmount" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetDirectAmount}" readOnly="${viewOnly or budgetLinked}"/></div></td>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetIndirectCostAmount" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetIndirectCostAmount}" readOnly="${viewOnly or budgetLinked}"/></div></td>
                <td><div align="right">$ ${KualiForm.document.routingFormBudget.routingFormBudgetDirectAmount + KualiForm.document.routingFormBudget.routingFormBudgetIndirectCostAmount} </div></td>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetStartDate" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetStartDate}" datePicker="true" readOnly="${viewOnly or budgetLinked}"/></div></td>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetEndDate" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetEndDate}" datePicker="true" readOnly="${viewOnly or budgetLinked}"/></div>
                  <kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetMinimumPeriodNumber" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetMinimumPeriodNumber}" />
                  <kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetMaximumPeriodNumber" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetMaximumPeriodNumber}" />
                </td>
              </tr>
              <tr>
                <th scope="row"><div align="right">Total Periods:</div></th>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetTotalDirectAmount" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetTotalDirectAmount}" readOnly="${viewOnly or budgetLinked}"/></div></td>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetTotalIndirectCostAmount" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetTotalIndirectCostAmount}" readOnly="${viewOnly or budgetLinked}"/></div></td>
                <td><div align="right">$ ${KualiForm.document.routingFormBudget.routingFormBudgetTotalDirectAmount + KualiForm.document.routingFormBudget.routingFormBudgetTotalIndirectCostAmount} </div></td>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetTotalStartDate" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetTotalStartDate}" datePicker="true" readOnly="${viewOnly or budgetLinked}"/></div></td>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetTotalEndDate" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetTotalEndDate}" datePicker="true" readOnly="${viewOnly or budgetLinked}"/></div></td>
              </tr>
            </table>

          </div>

</kul:tab>
