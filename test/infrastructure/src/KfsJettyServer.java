import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/*
 * Copyright 2010 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
