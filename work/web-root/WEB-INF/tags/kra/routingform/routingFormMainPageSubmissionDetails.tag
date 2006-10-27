<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/WEB-INF/tags/kra/routingform/routingFormMainPageSubmissionDetails.tag,v $
 
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

<kul:tab tabTitle="Submission Details" defaultOpen="true" tabErrorKey="${Constants.DOCUMENT_ERRORS}" >

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
                <td colspan=4 class="tab-subhead">Project Type/Reference Numbers</td>
              </tr>

              <tr>
                <th align=right valign=middle>Type of application:</th>
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
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.proposalPriorGrantNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.proposalPriorGrantNumber" attributeEntry="${documentAttributes.proposalPriorGrantNumber}"  />

<!--
                	<input name="textfield" type="text" size="12">
-->
               	</td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionAccountNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.institutionAccountNumber" attributeEntry="${documentAttributes.institutionAccountNumber}"  />

<!--
                	<input name="textfield" type="text" size="12">
-->
                </td>
              </tr>
              <tr>

                <th align=right valign=middle>Federal Identification No.:</th>
                <td align=left valign=middle >
                	<input name="textfield" type="text" size="12">
                </td>
                <th align=right valign=middle>Grants.gov Confirmation No.:</th>
                <td align=left valign=middle >
                	<input name="textfield" type="text" size="12">
                </td>
              </tr>
              <tr>

                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.grantNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.grantNumber" attributeEntry="${documentAttributes.grantNumber}"  />

<!--
                	<input name="textfield" type="text" size="12">
-->
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${contractGrantProposalAttributes.proposalNumber}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.contractGrantProposal.proposalNumber" attributeEntry="${contractGrantProposalAttributes.proposalNumber}"  />

<!--
                	<input name="textfield" type="text" size="12">
-->
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
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.proposalProjectTitle}" skipHelpUrl="true" useShortLabel="true" /></th>

                <td colspan="3" align=left valign=middle nowrap >
                	<kul:htmlControlAttribute property="document.proposalProjectTitle" attributeEntry="${documentAttributes.proposalProjectTitle}" />
<!--                 
                	<textarea name="textfield" cols="60" rows="3"></textarea>
-->
                </td>
              </tr>
              <tr>
                <th align=right valign=middle>Keywords:</th>
                <td colspan="3" align=left valign=middle nowrap >

		            <table cellpadding="0" cellspacing="0" class="neutral">
		              <tr>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
					    		<kul:lookup boClassName="org.kuali.module.kra.routingform.bo.RoutingFormKeyword" lookupParameters="document.routingFormKeyword.proposalKeywordDescritpion:proposalKeywordDescription" fieldConversions="proposalKeywordDescription:document.routingFormKeyword.proposalKeywordDescription" tabindexOverride="5100" anchor="${currentTabIndex}" />
		                	</c:if>

<!-- 
		                  <kul:htmlControlAttribute property="newRoutingFormKeyword.proposalKeywordDescription" attributeEntry="${routingFormKeywordAttributes.proposalKeywordDescription}" />
-->
		                </div></td>
		                <td class="neutral"><div align="center"><html:image property="methodToCall.insertRoutingFormKeyword.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add keyword"/></div></td>
		              </tr>   
		              
		              <c:forEach items = "${KualiForm.document.routingFormKeywords}" var="routingFormKeyword" varStatus="status"  >
					  <tr>
		                <td class="neutral"> <div align="left">
		                  <kul:htmlControlAttribute property="document.routingFormKeyword[${status.index}].proposalKeywordDescription" attributeEntry="${routingFormKeywordAttributes.proposalKeywordDescription}" />
		                </div></td>
		                <td class="neutral"><div align="center"><html:image property="methodToCall.deleteRoutingFormKeyword.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete research risk"/></div></td>
		              </tr>   
		              </c:forEach>
		            </table>


<!-- 
                  <table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td class="neutral"><div align=left> <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>
                      <td class="neutral">
                        <div align="left">
                          <input type="image" name="methodToCall.showAllTabs6" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <td class="neutral">
                        <div align=left> biology </div>
                      </td>
                      <td class="neutral">
                        <div align="left">
                          <input type="image" name="methodToCall.showAllTabs7" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <td class="neutral"> cancer causing agent </td>
                      <td class="neutral">
                        <div align="left">
                          <input type="image" name="methodToCall.showAllTabs8" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div>
                      </td>
                    </tr>
                  </table>
-->
                </td>
              </tr>
              <tr>
                <th align=right valign=middle>Abstract:</th>
                <td colspan="3" align=left valign=middle nowrap ><textarea name="textfield" cols="60" rows="3"></textarea></td>
              </tr>
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Amounts &amp; Dates </span> </td>

              </tr>
              <tr>
                <th width="20%" align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormBudgetAttributes.proposalBudgetDirectAmount}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td width="30%" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormBudget.proposalBudgetDirectAmount" attributeEntry="${routingFormBudgetAttributes.proposalBudgetDirectAmount}"  />
<!-- 
                	<input name="textfield" type="text" size="12">
-->
                </td>
                <th width="20%" align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormBudgetAttributes.proposalBudgetStartDate}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td width="30%" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormBudget.proposalBudgetStartDate" attributeEntry="${routingFormBudgetAttributes.proposalBudgetStartDate}" datePicker="true" />
<!--
                	<input name="date1" size=10 value="01/01/2005 " type=text>
                  	<a href="#" onclick="cal.select(document.forms['example'].date1,'anchor1','MM/dd/yyyy'); return false;" name="anchor1" id="anchor1"><img src="images/cal.gif" alt="date selector" width=16 height=16 border=0></a>
-->
                </td>
              </tr>

              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormBudgetAttributes.proposalBudgetIndirectCostAmount}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormBudget.proposalBudgetIndirectCostAmount" attributeEntry="${routingFormBudgetAttributes.proposalBudgetIndirectCostAmount}"  />
<!--
                	<input name="textfield" type="text" size="12">
-->
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormBudgetAttributes.proposalBudgetEndDate}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormBudget.proposalBudgetEndDate" attributeEntry="${routingFormBudgetAttributes.proposalBudgetEndDate}" datePicker="true" />
<!-- 
                	<input name="date1" size=10 value="01/01/2005 " type=text>
                  	<a href="#" onclick="cal.select(document.forms['example'].date1,'anchor1','MM/dd/yyyy'); return false;" name="anchor1" id="anchor1"><img src="images/cal.gif" alt="date selector" width=16 height=16 border=0></a>
-->
                </td>
              </tr>
              <tr>

                <th align=right valign=middle>Total Costs:</th>
                <td colspan="3" align=left valign=middle >$</td>
              </tr>
            </table>
          </div>

</kul:tab>
