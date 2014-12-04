<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
	
	<c:set var="kemidAttributes" value="${DataDictionary.KEMID.attributes}" />
	<c:set var="typeCodeAttributes" value="${DataDictionary.TypeCode.attributes}" />
	<c:set var="purposeCodeAttributes" value="${DataDictionary.PurposeCode.attributes}" />
	<c:set var="combineGroupCodeAttributes" value="${DataDictionary.CombineGroupCode.attributes}" />
	<c:set var="orgAttributes" value="${DataDictionary.Organization.attributes}" />
	<c:set var="chartAttributes" value="${DataDictionary.Chart.attributes}" />
		
<kul:page  showDocumentInfo="false"
	headerTitle="Endowment Trial Balance Generation" docTitle="Endowment Trial Balance Generation" renderMultipart="true"
	transactionalDocument="false" htmlFormAction="reportEndowTrialBalance" errorKey="foo">

	 <table cellpadding="0" cellspacing="0" class="datatable-80" summary="Trial Balance">
			<tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${kemidAttributes.kemidForReport}" readOnly="true" /></div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">                
					<kul:htmlControlAttribute attributeEntry="${kemidAttributes.kemidForReport}" property="kemid" />
                    <kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.KEMID"  fieldConversions="kemid:kemid" />
                </td>				                       
            </tr>
            <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right">Benefitting Organization Campus:</div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
					<kul:htmlControlAttribute attributeEntry="${orgAttributes.organizationPhysicalCampusCodeForReport}" property="benefittingOrganziationCampus" />	
                    <kul:lookup boClassName="org.kuali.rice.location.framework.campus.CampusEbo"  fieldConversions="code:benefittingOrganziationCampus" />
                </td>				                      
            </tr>                          
            <tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right">Benefitting Organization Chart:</div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
					<kul:htmlControlAttribute attributeEntry="${chartAttributes.chartCodeForReport}" property="benefittingOrganziationChart" />	
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Chart"  fieldConversions="chartCodeForReport:benefittingOrganziationChart" />
                </td>				                      
            </tr>          
            <tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right">Benefitting Organization</div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
					<kul:htmlControlAttribute attributeEntry="${orgAttributes.organizationCodeForReport}" property="benefittingOrganziation" />	
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Organization"  fieldConversions="organizationCodeForReport:benefittingOrganziation" />
                </td>				                      
            </tr>
            <tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${typeCodeAttributes.codeForReport}" readOnly="true" /></div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
					<kul:htmlControlAttribute attributeEntry="${typeCodeAttributes.codeForReport}" property="typeCode"  />	
                    <kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.TypeCode"  fieldConversions="code:typeCode"  />
                </td>				                      
            </tr>
            <tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purposeCodeAttributes.codeForReport}" readOnly="true" /></div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
					<kul:htmlControlAttribute attributeEntry="${purposeCodeAttributes.codeForReport}" property="purposeCode"  />	
                    <kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.PurposeCode"  fieldConversions="code:purposeCode"  />
                </td>				                      
            </tr>
            <tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${combineGroupCodeAttributes.codeForReport}" readOnly="true" /></div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
					<kul:htmlControlAttribute attributeEntry="${combineGroupCodeAttributes.codeForReport}" property="combineGroupCode"  />	
                    <kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.CombineGroupCode"  fieldConversions="code:combineGroupCode"  />
                </td>				                      
            </tr>            
            <tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right">As of Date:</div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
					<kul:htmlControlAttribute attributeEntry="${kemidAttributes.reportDate}" property="asOfDate" />					
                </td>				                      
            </tr> 
            <tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right">Endowment Option:</div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
					<input type="radio" name="endowmentOption" value="Y" />Endowment&nbsp;&nbsp;
					<input type="radio" name="endowmentOption" value="N" />Non-Endowed&nbsp;&nbsp;
					<input type="radio" name="endowmentOption" value="B" checked />Both<br/>									
                </td>				                      
            </tr>
            <tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right">Closed Indicator:</div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
					<input type="radio" name="closedIndicator" value="Y" />Yes&nbsp;&nbsp;
					<input type="radio" name="closedIndicator" value="N" />No&nbsp;&nbsp;
					<input type="radio" name="closedIndicator" value="B" checked />Both<br/>									
                </td>				                      
            </tr>            
            <tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right">List KEMIDs in Header:</div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
	                <input type="radio" name="listKemidsInHeader" value="Y" checked />Yes&nbsp;&nbsp;
					<input type="radio" name="listKemidsInHeader" value="N" />No&nbsp;&nbsp;
                </td>				                      
            </tr>                            
        </table>
    
     <c:set var="extraButtons" value="${KualiForm.extraButtons}"/>  	
  	
	
     <div id="globalbuttons" class="globalbuttons">
	        	
	        	<c:if test="${!empty extraButtons}">
		        	<c:forEach items="${extraButtons}" var="extraButton">
		        		<html:image src="${extraButton.extraButtonSource}" styleClass="globalbuttons" property="${extraButton.extraButtonProperty}" title="${extraButton.extraButtonAltText}" alt="${extraButton.extraButtonAltText}"/>
		        	</c:forEach>
	        	</c:if>
	</div>
	
	<div>
	  <c:if test="${!empty KualiForm.message }">
            	 ${KualiForm.message }
            </c:if>
   </div>
	
</kul:page>
