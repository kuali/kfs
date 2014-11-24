/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class KfsJettyServer {

    /**
     * This method...
     * @param args
     */
    public static void main(String[] args) {
        final String WEBROOT        = System.getProperty("webroot.directory");
        final int    SERVER_PORT    = Integer.parseInt(System.getProperty("jetty.port"));
        final String CONTEXT_PATH   = "/kfs-dev";

        Server server = new Server();

        Connector[] connectors = new Connector[1];
        connectors[0] = new SelectChannelConnector();
        connectors[0].setPort(SERVER_PORT);
        server.setConnectors(connectors);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(CONTEXT_PATH);
        webapp.setWar(WEBROOT);
        server.setHandler(webapp);

        try {
            server.start();
            server.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
