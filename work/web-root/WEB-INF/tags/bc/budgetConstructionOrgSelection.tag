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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>



<kul:tabTop tabTitle="Org Selection" defaultOpen="true" tabErrorKey="orgSel">
<c:set var="pointOfViewOrgAttributes" value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}" />
	<div class="tab-container" align="center" id="G02" style="display: block;">
    	<div class="h2-container">
    		<span class="subhead-left">
        		<h2><html:hidden property="operatingModeTitle" value="${KualiForm.operatingModeTitle}" /> ${KualiForm.operatingModeTitle}</h2>
        	</span>
      	</div>
        <table cellpadding=0 cellspacing="0"  summary="">
          	<tr>
               	<td>
               	<div align="center"> <br>
                   	<table style="width:inherit" border="0" cellpadding="0" cellspacing="0" class="nobord">
                       	<tr>
                           	<td width="200" style="border-style:solid; border-color:#999999; border-width:1px; padding:6px; background-color:#EAE9E9">
                           	<div align="center">
                            	<strong>Select Point of View: <br><br>
                            	<html:hidden property="previousPointOfViewKeyCode" value="${KualiForm.currentPointOfViewKeyCode}" />
                        		<kul:htmlControlAttribute property="currentPointOfViewKeyCode" attributeEntry="${pointOfViewOrgAttributes.selectionKeyCode}" onchange="refreshPointOfView(this.form)" readOnly="false" styleClass="grid" />
                                <br>
                            	</strong>
                            </div>
                            </td>
                        	<c:if test="${!empty KualiForm.pointOfViewOrg.chartOfAccountsCode}"> 	                    
                           		<td width="30" class="nobord" >&nbsp;</td>
	                            <td class="nobord" >
    		                        <div align="left">
            		                	<p align="center"><strong>Currently Selected:</strong><strong><br><br></strong>
	                                <kul:htmlControlAttribute property="pointOfViewOrg.chartOfAccountsCode" attributeEntry="${pointOfViewOrgAttributes.chartOfAccountsCode}" readOnly="true" readOnlyBody="true">
    	                        	<kul:inquiry boClassName="org.kuali.module.chart.bo.Chart" keyValues="chartOfAccountsCode=${pointOfViewOrg.chartOfAccountsCode}" render="${!empty KualiForm.pointOfViewOrg.chartOfAccountsCode}">
	                                <html:hidden write="true" property="pointOfViewOrg.chartOfAccountsCode" />
    	                        	</kul:inquiry>
        	                  		</kul:htmlControlAttribute>
            	               		-
                	          		<kul:htmlControlAttribute property="pointOfViewOrg.organizationCode" attributeEntry="${pointOfViewOrgAttributes.organizationCode}" readOnly="true" readOnlyBody="true">
                    	        	<kul:inquiry boClassName="org.kuali.module.chart.bo.Org" keyValues="chartOfAccountsCode=${KualiForm.pointOfViewOrg.chartOfAccountsCode}&amp;organizationCode=${KualiForm.pointOfViewOrg.organizationCode}" render="${!empty KualiForm.pointOfViewOrg.organizationCode}">
	      		        	      	<html:hidden write="true" property="pointOfViewOrg.organizationCode" />
      			            		</kul:inquiry>
	                      			</kul:htmlControlAttribute>
	                                <span class="fineprint">(<kul:htmlControlAttribute property="pointOfViewOrg.organization.organizationName" attributeEntry="${organizationAttributes.organizationName}" readOnly="true"/>)
    	                            </span>
        		                    </div>
                	            </td>
                            </c:if>
                       	</tr>
                    </table>
                </div>
               	</td>
			</tr>
		</table>
    </div>
</kul:tabTop>
