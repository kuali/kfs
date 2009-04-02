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
<!-- BEGIN budgetPersonnelAdd.tag -->
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="budgetPersonnelAttributes" value="${DataDictionary.BudgetUser.attributes}" />


            <div class="annotate">
              <table width="100%" cellpadding="0" cellspacing="0" class="annotate-top">
                <tr>
                  <td class="annotate-t"><img src="${ConfigProperties.kr.externalizable.images.url}annotate-tl1.gif" alt="" width="12" height="24" align="absmiddle" class="annotate-t">
                    Add Personnel:
                  </td>
                  <td class="annotate-t">
                    <div align="right">
                      <img src="${ConfigProperties.kr.externalizable.images.url}annotate-tr1.gif" alt="" width="12" height="24" align="absmiddle">
                    </div>
                  </td>
                </tr>
              </table>
              
							<div class="annotate-container">
							  <div align="center">
		              <table width="100%" cellpadding="0" cellspacing="0" class="annotate-top">
		                <tr>
		                  <td width="12"> </td>
		                  <td>
		                    <kul:errors keyMatch="newPersonnel*" />
		                  </td>
		                </tr>
		              </table> 
							  </div>
							</div>

			        <div class="annotate-container">

			          <div align="center">
			
			            <table class="grid" summary="" cellpadding="4" cellspacing="0">
			              <tbody>
				              <tr>
					              <th class="grid"><div align="right">Name:</div></th>
					              <td class="grid">
					                <html:radio property="newPersonnelType" value="person" styleId="newPersonnelType.person" />
                          <label for="newPersonnelType.person">
							              <c:if test="${empty KualiForm.newPersonnel.principalId}">
							                name now
							              </c:if>
							              <html:hidden property="newPersonnel.user.name" write="true" />
                          </label>
						              <kul:lookup boClassName="org.kuali.rice.kim.bo.Person" fieldConversions="principalId:newPersonnel.principalId,name:newPersonnel.user.name,personBaseSalaryAmount:newPersonnel.baseSalary" />
						              <br />
						              <html:radio property="newPersonnelType" value="ToBeNamed" styleId="newPersonnelType.ToBeNamed" />
                          <label for="newPersonnelType.ToBeNamed">name later</label>
						            </td>
						            <th class="grid">
						              <div align="right"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetUser.attributes.role}" skipHelpUrl="true" /></div>
						            </th>
						            <td class="grid">
						              <kul:htmlControlAttribute property="newPersonnel.role" attributeEntry="${budgetPersonnelAttributes.role}"/>
						            </td>
			  		          </tr>
			              </tbody>
				          </table>
			
			            <br>
			            <span class="infoline">
				            <html:image property="methodToCall.clearNewPersonnel" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-clear1.gif" title="clear" alt="clear" />&nbsp;
			              <html:image property="methodToCall.insertPersonnel" src="${ConfigProperties.externalizable.images.url}tinybutton-addpers.gif" title="add this person" alt="add this person"/>
			            </span>
			          </div>
			        </div>

              <table width="100%" cellpadding="0" cellspacing="0" class="annotate-top">
                <tr>
                  <td class="annotate-b"><img src="${ConfigProperties.kr.externalizable.images.url}annotate-bl1.gif" alt="" width="12"height="24">
                  </td>

                  <td class="annotate-b">
                    <div align="right">
                      <img src="${ConfigProperties.kr.externalizable.images.url}annotate-br1.gif" alt="" width="12" height="24">
                    </div>
                  </td>
                </tr>
              </table>
            </div>
<!-- END budgetPersonnelAdd.tag -->

