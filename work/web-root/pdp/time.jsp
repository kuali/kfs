<%
    java.lang.Thread.sleep( java.lang.Long.parseLong(request.getParameter("time")) * 1000 );
 %>
Time's up <%= request.getParameter("time") %> seconds.
