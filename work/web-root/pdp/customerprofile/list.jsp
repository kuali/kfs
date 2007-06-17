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
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
<head><title>Customer List</title></head>
<body>

<h1><strong>Customer Profile Maintenance</strong></h1>
  <jsp:include page="${request.getContextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="20">
    	&nbsp;
    </td>
    <td>
      <br>
			<font color="#800000">
			    <html:errors/>
			  <br>
			</font>
		</td>
	</tr>
</table>

<table width="100%" border=0 cellspacing=0 cellpadding=0>
  <tr>
    <td width=20>&nbsp;</td>
    <td>
      <table width="100%" height=40 border=0 cellpadding=0 cellspacing=0>
        <tr>
					<!-- Fix this back link -->
						<td><strong>Customer Profile List:</strong></td>
          <td>
            <div align=right>
 					    <a href="customerprofile.do?profile=0">Create a New Profile</a>
				    </div>
          </td>
        </tr>
      </table>
    </td>
    <td width=20>&nbsp;</td>
  </tr>
</table>
<br>	  
	<br><br>

    <logic:present name="customers">
        
        <table width="90%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t" align="center" >
          <tbody>
            <tr>
              <th align="right" >
                <div align="center">Chart&nbsp;-&nbsp;Organization&nbsp;-&nbsp;Sub-Unit</div>
              </th>
              <th align=left>
                <div align="center">Profile Description</div>
              </th>
              <th align=left>
                <div align="center">Primary Contact Name</div>
              </th>
              <th>
                <div align="center">Profile E-Mail</div>
              </th>
              <th align=left>
                <div align="center">Campus Process Location</div>
              </th>
              <th align=left>
                <div align="center">Active Indicator</div>
              </th>
              
            </tr>

          <logic:iterate id="profile" name="customers" indexId="i">

    		    <tr valign=middle align=left>

	            <td nowrap=nowrap class="datacell">
	              <a href="customerprofile.do?profile=<bean:write name="profile" property="id"/>"><bean:write name="profile" property="chartCode"/>-<bean:write name="profile" property="orgCode"/>-<bean:write name="profile" property="subUnitCode"/></a>
			        	<!-- a href="ProfileDetail.do?btnUpdateProfile=param&coacd=<bean:write name="profile" property="chartCode"/>&org=<bean:write name="profile" property="orgCode"/>&sbunt=<bean:write name="profile" property="subUnitCode"/>"><bean:write name="profile" property="chartCode"/>-<bean:write name="profile" property="orgCode"/>-<bean:write name="profile" property="subUnitCode"/></a -->
			        	&nbsp;
			        </td>
			        <td nowrap=nowrap class="datacell">
			        	<bean:write name="profile" property="customerDescription"/>
			        	&nbsp;
			        </td>
			        <td nowrap=nowrap class="datacell">
			        	<bean:write name="profile" property="contactFullName"/>
			        	&nbsp;
			        </td>
			        <td nowrap=nowrap class="datacell">
			        	<bean:write name="profile" property="firstFiftyProcessingEmailAddr"/>
			        	&nbsp;
			        </td>
			        <td nowrap=nowrap class="datacell">
			        	<bean:write name="profile" property="defaultPhysicalCampusProcessingCode"/>
			        	&nbsp;
			        </td>
			        <td nowrap=nowrap class="datacell">
			          <logic:equal name="profile" property="customerActive" value="true">
			            Active
			          </logic:equal>
			          <logic:notEqual name="profile" property="customerActive" value="true">
			            Not Active
			          </logic:notEqual>   
			        	
			        	&nbsp;
			        </td>
			        <!--td nowrap=nowrap class="datacell" align="center">
			        	<a href>Open</a>
			        	&nbsp;
			        </td-->
			        <!--td nowrap=nowrap class="datacell" align="center">
	          	  <a href="javascript:popup('Vendor/Search/ContactPopup.jsp')">Click Here</a>
			        	&nbsp;
			        </td-->
		      	</tr>
			    </logic:iterate>
		      </tbody>
		    </table>

	  </logic:present>
<br>
<c:import url="/backdoor.jsp"/>
</body>
</html>
