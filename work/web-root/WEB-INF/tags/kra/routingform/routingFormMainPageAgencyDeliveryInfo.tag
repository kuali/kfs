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

<c:set var="routingFormAttributes" value="${DataDictionary.KualiRoutingFormDocument.attributes}" />
<c:set var="routingFormAgencyAttributes" value="${DataDictionary.RoutingFormAgency.attributes}" />
<c:set var="cfdaAttributes" value="${DataDictionary.Cfda.attributes}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>
<c:set var="budgetLinked" value="${KualiForm.editingMode['budgetLinked']}" />

<kul:tab tabTitle="Agency/Delivery Info" defaultOpen="true" tabErrorKey="document.routingFormAgency*,document.federalPassThroughAgency*,document.agencyFederalPassThrough*" auditCluster="mainPageAuditErrors" tabAuditKey="document.routingFormAgency*">
	<div class="tab-container-error"><div class="left-errmsg-tab"><kra:auditErrors cluster="mainPageAuditErrors" keyMatch="document.routingFormAgency*" isLink="false" includesTitle="true"/></div></div>

		<div class="tab-container" align="center">
            <div class="tab-container-error"> </div>
            <div class="h2-container">
              <h2>Agency/Delivery Info</h2>
            </div>
            
            <table cellpadding="0" cellspacing="0" summary="view/edit document overview information">
              <tr>
                <th width="20%" align=right valign=middle>${routingFormAttributes.routingFormAgency.label}:</th>
                <td width="30%">
                  	<html:hidden property="document.routingFormAgencyToBeNamedIndicator" />
				   <c:if test="${viewOnly || budgetLinked}"> 
				    	<html:hidden property="document.routingFormAgency.agencyNumber" /> 
	              </c:if> 
			    	<html:hidden property="document.routingFormAgency.agency.fullName" styleId="document.routingFormAgency.agency.fullName" /> 
    				<html:hidden property="document.routingFormAgency.agency.agencyTypeCode" styleId="document.routingFormAgency.agency.agencyTypeCode"/>
			    	<c:if test="${empty KualiForm.document.routingFormAgency.agencyNumber && !KualiForm.document.routingFormAgencyToBeNamedIndicator}"></c:if>
  			    <c:if test="${KualiForm.document.routingFormAgencyToBeNamedIndicator}">TO BE NAMED</c:if>
			   <c:if test="${!viewOnly and !budgetLinked}"> 
	               <kul:htmlControlAttribute property="document.routingFormAgency.agencyNumber" attributeEntry="${routingFormAttributes.routingFormAgency}" readOnly="${viewOnly}" onblur="onblur_agencyNumber('document.routingFormAgency.agencyNumber','agency')"/>
	               <kul:lookup boClassName="org.kuali.module.cg.bo.Agency" lookupParameters="document.routingFormAgency.agencyNumber:agencyNumber,document.routingFormAgency.agency.fullName:fullName" fieldConversions="agencyNumber:document.routingFormAgency.agencyNumber,fullName:document.routingFormAgency.agency.fullName" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_namelater.gif" extraButtonParams="&document.routingFormAgencyToBeNamedIndicator=true" anchor="${currentTabIndex}" />
              </c:if> 
	          <div id="document.routingFormAgency.agency.fullName.div" >
	          <c:if test="${!empty KualiForm.document.routingFormAgency.agencyNumber}">
	            <c:choose>
							  <c:when test="${empty KualiForm.document.routingFormAgency.agency.fullName}">
								  <span style='color: red;'><c:out value="agency not found" /> </span>
								</c:when>
								<c:otherwise>
								  <c:out value="${KualiForm.document.routingFormAgency.agency.fullName}" />
								</c:otherwise>
							</c:choose>                        

	                    </div>
                	</c:if>
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.routingFormDueDate}" skipHelpUrl="true" /></th>
                <td colspan="2" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.routingFormDueDate" attributeEntry="${routingFormAgencyAttributes.routingFormDueDate}" datePicker="true" readOnly="${viewOnly}"/>
                </td>
              </tr>
              <tr>
                <th width="20%" align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.agencyFederalPassThroughNumber}" skipHelpUrl="true" /></th>
                <td width="30%" align=left valign=middle >
				   <c:if test="${viewOnly || budgetLinked}"> 
				    	<html:hidden property="document.agencyFederalPassThroughNumber" /> 
	              </c:if> 
                    <html:hidden property="document.agencyFederalPassThroughNotAvailableIndicator" />
                    <html:hidden property="document.federalPassThroughAgency.fullName" /> 
			    	<c:choose>
			    		<c:when test="${!viewOnly && KualiForm.document.routingFormAgency.agency.agencyTypeCode != KFSConstants.AGENCY_TYPE_CODE_FEDERAL}">
			    			<c:if test="${empty KualiForm.document.agencyFederalPassThroughNumber && !KualiForm.document.agencyFederalPassThroughNotAvailableIndicator and !viewOnly and !budgetLinked}"></c:if>
			    			<c:if test="${KualiForm.document.agencyFederalPassThroughNotAvailableIndicator}">Unknown</c:if>
			    			<c:if test="${!viewOnly and !budgetLinked}">

						     <div id="pDiv">
						       <div id="cDiv">
								    				<kul:htmlControlAttribute property="document.agencyFederalPassThroughNumber" attributeEntry="${routingFormAttributes.agencyFederalPassThroughNumber}" readOnly="${viewOnly}" onblur="onblur_agencyNumber('document.agencyFederalPassThroughNumber','federalPassThroughAgency');addCfp('document.agencyFederalPassThroughNumber');"/>
								    				<kul:lookup boClassName="org.kuali.module.cg.bo.Agency" fieldConversions="agencyNumber:document.agencyFederalPassThroughNumber,fullName:document.federalPassThroughAgency.fullName" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_namelater.gif" extraButtonParams="&document.agencyFederalPassThroughNotAvailableIndicator=true" anchor="${currentTabIndex}" />
								    			</c:if>
								    			    <div id="document.federalPassThroughAgency.fullName.div" >
										             <c:if test="${!empty KualiForm.document.agencyFederalPassThroughNumber}">
										                 <c:choose>
															<c:when test="${empty KualiForm.document.federalPassThroughAgency.fullName}">
																<span style='color: red;'><c:out value="federal pass through agency not found" /> </span>
															</c:when>
															<c:otherwise>
																<c:out value="${KualiForm.document.federalPassThroughAgency.fullName}" />
															</c:otherwise>
														 </c:choose>                        
										              </c:if>
										           </div>
						           <div id="myDiv">
						           		<div id="newDiv">
						            				<c:if test="${not empty KualiForm.document.agencyFederalPassThroughNumber}">
								    				
								    					<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-clearfptagency.jpg" styleClass="tinybutton" property="methodToCall.clearFedPassthrough.anchor${currentTabIndex}" alt="Clear Federal Pass Through Agency"/>
								    				</c:if>
						           		</div>
						           </div>
						       </div>
						     </div>    
			    		</c:when>
			    		<c:otherwise>
					     <div id="pDiv">
					       <div id="cDiv">
							    		     N/A
							</div>
						</div>
			    	  </c:otherwise>
			    	</c:choose>
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.routingFormDueDateTypeCode}" skipHelpUrl="true" /></th>
                <td colspan="2" align=left valign=middle >
                  <c:forEach items="${KualiForm.document.routingFormAgency.routingFormDueDateTypes}" var="routingFormDueDateType" varStatus="status">
                    <html:hidden property="document.routingFormAgency.routingFormDueDateTypes[${status.index}].documentNumber" />
                    <html:hidden property="document.routingFormAgency.routingFormDueDateTypes[${status.index}].dueDateTypeCode" />
                    <html:hidden property="document.routingFormAgency.routingFormDueDateTypes[${status.index}].versionNumber" />
                    <html:hidden property="document.routingFormAgency.routingFormDueDateTypes[${status.index}].dueDateType.dueDateDescription" />
	              </c:forEach>
                  <kul:checkErrors keyMatch="document.routingFormAgency.routingFormDueDateTypeCode" auditMatch="document.routingFormAgency.routingFormDueDateTypeCode"/>
				  <c:if test="${hasErrors==true}">
				    <c:set var="routingFormDueDateTypeCodeTextStyle" value="background-color: red"/>
				  </c:if>
                  <c:choose>
                    <c:when test="${!viewOnly}">
                      <html:select property="document.routingFormAgency.routingFormDueDateTypeCode" style="${routingFormDueDateTypeCodeTextStyle}" disabled="${viewOnly}">
                        <html:option value="">&nbsp;</html:option>
                        <c:set var="routingFormDueDateTypes" value="${KualiForm.document.routingFormAgency.routingFormDueDateTypes}"/>
                        <html:options collection="routingFormDueDateTypes" property="dueDateTypeCode" labelProperty="dueDateType.dueDateDescription"/>
                      </html:select>
		            </c:when>
		            <c:otherwise>
                      <html:hidden property="document.routingFormAgency.routingFormDueDateTypeCode" />
                      <html:hidden property="document.routingFormAgency.dueDateType.dueDateDescription" />
                      ${KualiForm.document.routingFormAgency.dueDateType.dueDateDescription}&nbsp
		            </c:otherwise>
		          </c:choose>
                </td>

              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.agencyAddressDescription}" skipHelpUrl="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.agencyAddressDescription" attributeEntry="${routingFormAgencyAttributes.agencyAddressDescription}" readOnly="${viewOnly}"/>
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.agencyShippingInstructionsDescription}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.agencyShippingInstructionsDescription" attributeEntry="${routingFormAgencyAttributes.agencyShippingInstructionsDescription}" readOnly="${viewOnly}"/>
                </td>
                <td nowrap >
                	<kul:htmlControlAttribute property="document.routingFormAgency.agencyDiskAccompanyIndicator" attributeEntry="${routingFormAgencyAttributes.agencyDiskAccompanyIndicator}" readOnly="${viewOnly}"/>
                	<kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.agencyDiskAccompanyIndicator}" skipHelpUrl="true" labelFor="document.routingFormAgency.agencyDiskAccompanyIndicator" noColon="true" />
                  	<br>
                	<kul:htmlControlAttribute property="document.routingFormAgency.agencyElectronicSubmissionIndicator" attributeEntry="${routingFormAgencyAttributes.agencyElectronicSubmissionIndicator}" readOnly="${viewOnly}"/>
                	<kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.agencyElectronicSubmissionIndicator}" skipHelpUrl="true" labelFor="document.routingFormAgency.agencyElectronicSubmissionIndicator" noColon="true" />
                  </td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${cfdaAttributes.cfdaNumber}" skipHelpUrl="true" /></th>

                <td align=left valign=middle >
			    	<html:hidden property="document.routingFormCatalogOfFederalDomesticAssistanceNumber" />
			    	<html:hidden property="document.cfda.cfdaProgramTitleName" />
			    	<c:if test="${viewOnly}">
			    	    <html:hidden property="document.cfda.cfdaNumber"/>
			    	</c:if>
			    	<c:if test="${!viewOnly}">
			    	    <!--  <c:if test="${empty KualiForm.document.routingFormCatalogOfFederalDomesticAssistanceNumber}">&nbsp;</c:if> -->
			    	     <html:text property="document.cfda.cfdaNumber" onblur="cfdaLookup('document.cfda.cfdaNumber')"/>
			    		<kul:lookup boClassName="org.kuali.module.cg.bo.Cfda" lookupParameters="document.routingFormCatalogOfFederalDomesticAssistanceNumber:cfdaNumber" fieldConversions="cfdaNumber:document.routingFormCatalogOfFederalDomesticAssistanceNumber,cfdaNumber:document.cfda.cfdaNumber,cfdaProgramTitleName:document.cfda.cfdaProgramTitleName" anchor="${currentTabIndex}" />
                	</c:if>
		          <div id="document.cfda.cfdaProgramTitleName.div" >
		             <c:if test="${!empty KualiForm.document.cfda.cfdaNumber}">
		                 <c:choose>
							<c:when test="${empty KualiForm.document.cfda.cfdaProgramTitleName}">
								<span style='color: red;'><c:out value="cfda not found" /> </span>
							</c:when>
							<c:otherwise>
								<c:out value="${KualiForm.document.cfda.cfdaProgramTitleName}" />
							</c:otherwise>
						 </c:choose>                        
		              </c:if>
		           </div>
                	
                	&nbsp
				</td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.routingFormRequiredCopyText}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td colspan="2" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.routingFormRequiredCopyText" attributeEntry="${routingFormAgencyAttributes.routingFormRequiredCopyText}" readOnly="${viewOnly}"/>
                	Submit 2 additional copies plus the number of copies required by your department and school.
                </td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.routingFormAnnouncementNumber}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td colspan="4" align=left valign=middle >
                	<c:if test="${budgetLinked and empty KualiForm.document.routingFormAnnouncementNumber}">
                		(The linked budget does not contain a routing form announcement number.)
                	</c:if>
                	<kul:htmlControlAttribute property="document.routingFormAnnouncementNumber" attributeEntry="${routingFormAttributes.routingFormAnnouncementNumber}" readOnly="${viewOnly or budgetLinked}"/>
                </td>
              </tr>
            </table>
          </div>

</kul:tab>
