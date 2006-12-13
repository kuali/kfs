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
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<c:set var="docHeaderAttributes" value="${DataDictionary.DocumentHeader.attributes}" />
<c:set var="routingFormBudgetAttributes" value="${DataDictionary.RoutingFormBudget.attributes}" />
<c:set var="routingFormKeywordAttributes" value="${DataDictionary.RoutingFormKeyword.attributes}" />
<c:set var="contractGrantProposalAttributes" value="${DataDictionary.ContractGrantProposal.attributes}" />

<dd:evalNameToMap mapName="DataDictionary.${KualiForm.docTypeName}.attributes" returnVar="documentAttributes"/>

<kul:tab tabTitle="Submission Details" defaultOpen="true" tabErrorKey="${Constants.RoutingFormConstants.ROUTING_FORM_SUBMISSION_DETAIL_ERRORS}" >

          <div class="tab-container" align="center">
            <div class="h2-container">
              <h2>Other Details </h2>
            </div>
            <table cellpadding="0" cellspacing="0" summary="view/edit document overview information">

              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Submission Type </span> </td>
              </tr>
              <tr>
                <th align=right valign=middle>Type:</th>
                <td colspan="3" align=left valign=middle nowrap ><label>
                  <input name="RadioGroup1" type="radio" class="nobord" value="radio">

                  Pre-application</label>
                  <br>
                  <label>
                  <input name="RadioGroup1" type="radio" class="nobord" value="radio">
                  Application</label>
                  <br>
                  <label>
                  <input name="RadioGroup1" type="radio" class="nobord" value="radio">

                  Change/corrected application (include previous federal identifier): </label>&nbsp;

                  <input name="textfield" type="text" size="12">
                </td>
              </tr>
              <tr>
                <td colspan=4 class="tab-subhead">Project Type</td>
              </tr>

              <tr>
                <th align=right valign=middle>Type:</th>
                <td colspan="3" align=left valign=middle ><table width="100%" cellpadding="0"  cellspacing="0" class="nobord">
                    <tr>
                      <td class="nobord"><input name="checkbox" type="checkbox" class="radio" value="checkbox">
                      </td>
                      <td class="nobord">New</td>
                      <td class="nobord"><input name="checkbox" type="checkbox" class="radio" value="checkbox"></td>

                      <td class="nobord">Time Extension</td>
                    </tr>
                    <tr>
                      <td class="nobord"><input name="checkbox" type="checkbox" class="radio" value="checkbox">
                      </td>
                      <td class="nobord">Renewal-Not Previously Committed</td>
                      <td class="nobord"><input name="checkbox" type="checkbox" class="radio" value="checkbox"></td>
                      <td class="nobord">Budget Revision to Active Project</td>

                    </tr>
                    <tr>
                      <td class="nobord"><input name="checkbox" type="checkbox" class="radio" value="checkbox">
                      </td>
                      <td class="nobord">Renewal-Previously Committed</td>
                      <td class="nobord"><input name="checkbox" type="checkbox" class="radio" value="checkbox"></td>
                      <td class="nobord">Budget Revision to Pending Proposal</td>
                    </tr>

                    <tr>
                      <td class="nobord"><input name="checkbox" type="checkbox" class="radio" value="checkbox">
                      </td>
                      <td class="nobord">Supplemental Funds</td>
                      <td class="nobord"><input name="checkbox" type="checkbox" class="radio" value="checkbox"></td>
                      <td class="nobord">Other - Specify:
                        <input name="textfield" type="text" size="12"></td>
                    </tr>
                  </table></td>

              </tr>

              <tr>
                <td colspan=4 class="tab-subhead">Reference Numbers</td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.routingFormPriorGrantNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormPriorGrantNumber" attributeEntry="${documentAttributes.routingFormPriorGrantNumber}"  />
               	</td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionAccountNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.institutionAccountNumber" attributeEntry="${documentAttributes.institutionAccountNumber}"  />
                </td>
              </tr>
              <tr>

                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.federalIdentifier}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.federalIdentifier" attributeEntry="${documentAttributes.federalIdentifier}"  />
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.grantsGovernmentConfirmationNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.grantsGovernmentConfirmationNumber" attributeEntry="${documentAttributes.grantsGovernmentConfirmationNumber}"  />
                </td>
              </tr>
              <tr>

                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.grantNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.grantNumber" attributeEntry="${documentAttributes.grantNumber}"  />
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${contractGrantProposalAttributes.proposalNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.contractGrantProposal.proposalNumber" attributeEntry="${contractGrantProposalAttributes.proposalNumber}"  />
                </td>
              </tr>
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Project Purpose </span> </td>
              </tr>
              <tr>
                <th align=right valign=middle>Type:</th>
                <td colspan="3" align=left valign=middle nowrap ><label>
                  <input name="RadioGroup1" type="radio" class="nobord" value="radio">
                  Research</label>
                  <label>
                  <select name="select2">
                    <option selected>select type:</option>
                    <option>basic</option>
                    <option>applied</option>
                  </select>
                  </label>
                  <br>
                  <label>

                  <input name="RadioGroup1" type="radio" class="nobord" value="radio">
                  Instruction</label>
                  <br>
                  <label>
                  <input name="RadioGroup1" type="radio" class="nobord" value="radio">
                  Service/other: </label>
                  &nbsp;
                  <input name="textfield" type="text" size="12">

                </td>
              </tr>
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Project Summary </span> </td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.routingFormProjectTitle}" skipHelpUrl="true" useShortLabel="true" /></th>

                <td colspan="3" align=left valign=middle nowrap >
                	<kul:htmlControlAttribute property="document.routingFormProjectTitle" attributeEntry="${documentAttributes.routingFormProjectTitle}" />
                </td>
              </tr>
              <tr>
                <th align=right valign=middle>Keywords:</th>
                <td colspan="3" align=left valign=middle nowrap >

		            <table cellpadding="0" cellspacing="0" class="neutral">
		              <tr>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
						    	<html:hidden write="true" property="newRoutingFormKeyword.routingFormKeywordDescription" /> 
					    		<kul:lookup boClassName="org.kuali.module.kra.routingform.bo.Keyword" lookupParameters="newRoutingFormKeyword.routingFormKeywordDescription:routingFormKeywordDescription" fieldConversions="routingFormKeywordDescription:newRoutingFormKeyword.routingFormKeywordDescription" tabindexOverride="5100" anchor="${currentTabIndex}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"><div align="center"><html:image property="methodToCall.insertRoutingFormKeyword.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add keyword"/></div></td>
		              </tr>   
		              
		              <c:forEach items = "${KualiForm.document.routingFormKeywords}" var="routingFormKeyword" varStatus="status"  >
					  <tr>
		                <td class="neutral"> <div align="left">
				    		<html:hidden write="true" property="document.routingFormKeyword[${status.index}].routingFormKeywordDescription" /> 
		                </div></td>
		                <td class="neutral"><div align="center"><html:image property="methodToCall.deleteRoutingFormKeyword.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete research risk"/></div></td>
		              </tr>   
		              </c:forEach>
		            </table>
                </td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.projectAbstract}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td colspan="3" align=left valign=middle nowrap ><kul:htmlControlAttribute property="document.projectAbstract" attributeEntry="${documentAttributes.projectAbstract}" /></td>
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
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetDirectAmount" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetDirectAmount}"  /></div></td>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetIndirectCostAmount" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetIndirectCostAmount}"  /></div></td>
                <td><div align="right">$ ${KualiForm.document.routingFormBudget.routingFormBudgetDirectAmount + KualiForm.document.routingFormBudget.routingFormBudgetIndirectCostAmount} </div></td>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetStartDate" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetStartDate}" datePicker="true" /></div></td>
                <td><div align="center"><kul:htmlControlAttribute property="document.routingFormBudget.routingFormBudgetEndDate" attributeEntry="${routingFormBudgetAttributes.routingFormBudgetEndDate}" datePicker="true" /></div></td>
              </tr>
              <tr>
                <th scope="row"><div align="right">Total Periods:</div></th>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
            </table>

          </div>

</kul:tab>
