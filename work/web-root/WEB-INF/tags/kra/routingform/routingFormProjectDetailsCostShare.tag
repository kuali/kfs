<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/WEB-INF/tags/kra/routingform/routingFormProjectDetailsCostShare.tag,v $
 
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
<c:set var="institutionCostShareAttributes" value="${DataDictionary.RoutingFormInstitutionCostShare.attributes}" />

<dd:evalNameToMap mapName="DataDictionary.${KualiForm.docTypeName}.attributes" returnVar="documentAttributes"/>

<kul:tabTop tabTitle="Cost Share" defaultOpen="true" tabErrorKey="${Constants.DOCUMENT_ERRORS}" >

          <div class="tab-container" align="center">
            <div class="tab-container-error"> </div>
            <div class="h2-container">
              <h2>Cost Share</h2>
            </div>

            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <td colspan=7 class="tab-subhead"><span class="left">Institution Cost Share</span> </td>
              </tr>
              <tr>
                <th width="50">&nbsp;</th>
                <th>Chart</th>
                <th>Org</th>
                <th>Description</th>
                <th>Account</th>
                <th>Amount</th>
                <th>Action</th>
              </tr>

              <tr>
                <th align=right valign=middle>Keywords:</th>
                <td colspan="3" align=left valign=middle nowrap >

		            <table cellpadding="0" cellspacing="0" class="neutral">
		              <tr>
		                <td> <div align="center">
							add:
		                </div></td>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.chartOfAccountsCode" attributeEntry="${institutionCostShareAttributes.chartOfAccountsCode}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.organizationCode" attributeEntry="${institutionCostShareAttributes.organizationCode}" />
					    		<kul:lookup boClassName="org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare" lookupParameters="newRoutingFormInstitutionCostShare.organizationCode:organizationCode" fieldConversions="organizationCode:newRoutingFormInstitutionCostShare.organizationCode" tabindexOverride="5100" anchor="${currentTabIndex}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.proposalCostShareDescription" attributeEntry="${institutionCostShareAttributes.proposalCostShareDescription}" />
					    		<kul:lookup boClassName="org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare" lookupParameters="newRoutingFormInstitutionCostShare.proposalCostShareDescription:proposalCostShareDescription" fieldConversions="proposalCostShareDescription:newRoutingFormInstitutionCostShare.proposalCostShareDescription" tabindexOverride="5100" anchor="${currentTabIndex}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.accountNumber" attributeEntry="${institutionCostShareAttributes.accountNumber}" />
					    		<kul:lookup boClassName="org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare" lookupParameters="newRoutingFormInstitutionCostShare.accountNumber:accountNumber" fieldConversions="accountNumber:newRoutingFormInstitutionCostShare.accountNumber" tabindexOverride="5100" anchor="${currentTabIndex}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.proposalCostShareAmount" attributeEntry="${institutionCostShareAttributes.proposalCostShareAmount}" />
					    		<kul:lookup boClassName="org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare" lookupParameters="newRoutingFormInstitutionCostShare.proposalCostShareAmount:proposalCostShareAmount" fieldConversions="proposalCostShareAmount:newRoutingFormInstitutionCostShare.proposalCostShareAmount" tabindexOverride="5100" anchor="${currentTabIndex}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"><div align="center"><html:image property="methodToCall.insertRoutingFormCostShare.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add institution cost share"/></div></td>
		              </tr>   
		              
		              <c:forEach items = "${KualiForm.document.routingFormInstitutionCostShare}" var="routingFormInstitutionCostShare" varStatus="status"  >
					  <tr>
		                <td class="neutral"> <div align="left">
			                <kul:htmlControlAttribute property="routingFormInstitutionCostShare.sequenceNumber" attributeEntry="${institutionCostShareAttributes.sequenceNumber}" />
		                </div></td>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="routingFormInstitutionCostShare.chartOfAccountsCode" attributeEntry="${institutionCostShareAttributes.chartOfAccountsCode}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="routingFormInstitutionCostShare.organizationCode" attributeEntry="${institutionCostShareAttributes.organizationCode}" />
					    		<kul:lookup boClassName="org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare" lookupParameters="document.routingFormInstitutionCostShare.organizationCode:organizationCode" fieldConversions="organizationCode:document.routingFormInstitutionCostShare.organizationCode" tabindexOverride="5100" anchor="${currentTabIndex}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="routingFormInstitutionCostShare.proposalCostShareDescription" attributeEntry="${institutionCostShareAttributes.proposalCostShareDescription}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="routingFormInstitutionCostShare.accountNumber" attributeEntry="${institutionCostShareAttributes.accountNumber}" />
					    		<kul:lookup boClassName="org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare" lookupParameters="document.routingFormInstitutionCostShare.accountNumber:accountNumber" fieldConversions="accountNumber:document.routingFormInstitutionCostShare.accountNumber" tabindexOverride="5100" anchor="${currentTabIndex}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"> <div align="left">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="routingFormInstitutionCostShare.proposalCostShareAmount" attributeEntry="${institutionCostShareAttributes.proposalCostShareAmount}" />
		                	</c:if>
		                </div></td>
		                <td class="neutral"><div align="center"><html:image property="methodToCall.deleteRoutingFormCostShare.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete institution cost share"/></div></td>
		              </tr>
		              </c:forEach>
		            </table>


<!-- 
              <tr>
                <th scope="row">add:</th>
                <td class="infoline"><div align="center">
                    <select name="newTargetLine.chartOfAccountsCode" tabindex="0" onchange="" onblur="loadChartInfo( this.name, 'newTargetLine.chart.finChartOfAccountDescription');" style="" class="">
                      <option value=""></option>
                      <option value="BA">BA</option>
                      <option value="BL">BL</option>

                      <option value="EA">EA</option>
                      <option value="FW">FW</option>
                      <option value="HO">HO</option>
                      <option value="IA">IA</option>
                      <option value="IN">IN</option>
                      <option value="IU">IU</option>

                      <option value="KO">KO</option>
                      <option value="NW">NW</option>
                      <option value="SB">SB</option>
                      <option value="SE">SE</option>
                      <option value="UA">UA</option>
                    </select>

                  </div></td>
                <td class="infoline"><div align="center">
                    <input name="textfield" type="text" size="12">
                    <a href="lookups/lookup-param1.html"> <img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>
                <td class="infoline"><div align="center">
                    <input name="textfield" type="text" size="20">
                  </div></td>
                <td class="infoline"><div align="center">

                    <input name="textfield" type="text" size="12">
                    <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>
                <td class="infoline"><div align="right">
                    <input name="textfield" type="text" size="12" class="right">
                  </div></td>
                <td class="infoline"><div align=center><a href="ib-multi09.html"><img src="images/tinybutton-add1.gif" alt="add" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th scope="row"><div align="center">1</div></th>

                <td><div align="center"><span class="infoline">
                    <select name="newTargetLine.chartOfAccountsCode" tabindex="0" onchange="" onblur="loadChartInfo( this.name, 'newTargetLine.chart.finChartOfAccountDescription');" style="" class="">
                      <option value=""></option>
                      <option value="BA">BA</option>
                      <option value="BL" selected>BL</option>
                      <option value="EA">EA</option>
                      <option value="FW">FW</option>

                      <option value="HO">HO</option>
                      <option value="IA">IA</option>
                      <option value="IN">IN</option>
                      <option value="IU">IU</option>
                      <option value="KO">KO</option>
                      <option value="NW">NW</option>

                      <option value="SB">SB</option>
                      <option value="SE">SE</option>
                      <option value="UA">UA</option>
                    </select>
                    </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="CARD" size="12">

                    </span> <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="description goes here" size="20">
                    </span></div></td>
                <td><div align="center">
                    <input name="textfield" type="text" value="263463" size="12">
                    <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>
                <td><div align="right"><span class="infoline">

                    <input name="textfield" type="text" value="600.00" size="12" class="right">
                    </span></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th  scope="row"><div align="center">2</div></th>
                <td><div align="center"><span class="infoline">
                    <select name="newTargetLine.chartOfAccountsCode" tabindex="0" onchange="" onblur="loadChartInfo( this.name, 'newTargetLine.chart.finChartOfAccountDescription');" style="" class="">

                      <option value=""></option>
                      <option value="BA">BA</option>
                      <option value="BL">BL</option>
                      <option value="EA">EA</option>
                      <option value="FW">FW</option>
                      <option value="HO">HO</option>

                      <option value="IA" selected>IA</option>
                      <option value="IN">IN</option>
                      <option value="IU">IU</option>
                      <option value="KO">KO</option>
                      <option value="NW">NW</option>
                      <option value="SB">SB</option>

                      <option value="SE">SE</option>
                      <option value="UA">UA</option>
                    </select>
                    </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="UITS" size="12">
                    </span> <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>

                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="description goes here" size="20">
                    </span></div></td>
                <td><div align="center">
                    <input name="textfield" type="text" value="727234" size="12">
                    <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>
                <td><div align="right"><span class="infoline">
                    <input name="textfield" type="text" value="400.00" size="12" class="right">
                    </span></div></td>

                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              
-->              
              
              <tr>
                <td colspan="5" class="total-line"  scope="row">&nbsp;</td>
                <td class="total-line">$1000.00</td>
                <td class="total-line">&nbsp;</td>
              </tr>
              <tr>

                <td colspan=7 class="tab-subhead"><span class="left">3rd Party Cost Share</span> </td>
              </tr>
              <tr>
                <th>&nbsp;</th>
                <th colspan="3"> <div align="center">Source</div></th>
                <th colspan="2">Amount</th>

                <th >Action</th>
              </tr>
              <tr>
                <th scope="row">add:</th>
                <td colspan="3" class="infoline"><div align="center">
                    <input name="textfield" type="text" size="20">
                    </div></td>
                <td colspan="2" class="infoline"><input name="textfield" type="text" size="12" class="right"></td>

                <td class="infoline"><div align=center><a href="ib-multi09.html"><img src="images/tinybutton-add1.gif" alt="add" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th scope="row"><div align="center">1</div></th>
                <td colspan="3"><div align="center">
                    <input name="textfield" type="text" value="source 1" size="20">
                    </div></td>
                <td colspan="2"><span class="infoline">

                  <input name="textfield" type="text" value="600.00" size="12" class="right">
                  </span></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th  scope="row"><div align="center">2</div></th>
                <td colspan="3"><div align="center">
                    <input name="textfield" type="text" value="source 2" size="20">

                    </div></td>
                <td colspan="2"><span class="infoline">
                  <input name="textfield" type="text" value="400.00" size="12" class="right">
                  </span></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <td colspan="4" class="total-line"  scope="row">&nbsp;</td>

                <th colspan="2" class="total-line"  scope="row"><div align="right">$1000.00</div></th>
                <td class="total-line"></td>
              </tr>
            </table>
          </div>

</kul:tabTop>
