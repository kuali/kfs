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
package org.kuali.impex.ant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.jetel.graph.Result;
import org.jetel.graph.TransformationGraph;
import org.jetel.graph.TransformationGraphXMLReaderWriter;
import org.jetel.graph.runtime.EngineInitializer;
import org.jetel.graph.runtime.GraphRuntimeContext;
import org.jetel.main.runGraph;
import org.kuali.impex.ETLHelper;


public class RunExportGraphs extends EtlJdbcTask {

	public boolean verbose = false;
	public String graphExclusionRegex = ""; 
	public Pattern graphExclusionPattern;
	public Vector<FileSet> graphFiles = new Vector<FileSet>();
	
	public void addFileSet( FileSet fileSet ) {
		if ( !graphFiles.contains(fileSet) ) {
			graphFiles.add(fileSet);
		}
	}
	
	@Override
	public void execute() throws BuildException {
		EngineInitializer.initEngine((String)null, getProject().getBaseDir().getAbsolutePath() + "/" + "cloveretl.properties", null);
		TransformationGraphXMLReaderWriter graphReader = new TransformationGraphXMLReaderWriter(null);
		for ( FileSet fs : graphFiles ) {
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			File dir = ds.getBasedir();
			String[] filesInSet = ds.getIncludedFiles();
			for ( String fileName : filesInSet ) {
				if ( graphExclusionPattern != null && !graphExclusionPattern.matcher(fileName).matches() ) {
					if ( !ETLHelper.isTableEmpty(getConnection(), getSchemaName(), fileName.replace("_export.grf.xml", "" ) ) ) {
						File file = new File( dir, fileName );
						log( "Running graph: " + fileName );
						try {
							runGraph(file,graphReader);
						} catch ( Exception ex ) {
							throw new BuildException( file.toString(), ex );
						}
					} else {
						log( "Skipping graph: " + fileName + " -- Table Empty" );
					}
				} else {
					log( "Skipping graph: " + fileName + " excluded by Regex" );
				}
			}
		}
	}
	
	private void runGraph( File graphFile, TransformationGraphXMLReaderWriter graphReader ) throws Exception {		
		TransformationGraph graph = graphReader.read( new BufferedInputStream( new FileInputStream( graphFile ) ) );
		//graph.dumpGraphConfiguration();
		
		Future<Result> result;
		try {
			GraphRuntimeContext runtimeContext = new GraphRuntimeContext();
			runtimeContext.setVerboseMode(verbose);
			//log( "Verbose Mode: " + verbose );
			runtimeContext.setUseJMX(false);
			result = runGraph.executeGraph(graph, runtimeContext);

			while (result.isDone()) {
				Thread.sleep( 100 );
			}
			if (!result.get().equals(Result.FINISHED_OK)) {
				log( "Failed graph execution!\n" + result.get().message() );
				return;
			}
		} catch (Exception e) {
			log("Exception during graph execution!\n" + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public String getGraphExclusionRegex() {
		return graphExclusionRegex;
	}

	public void setGraphExclusionRegex(String graphExclusionRegex) {
		this.graphExclusionRegex = graphExclusionRegex;
		graphExclusionPattern = Pattern.compile(graphExclusionRegex, Pattern.CASE_INSENSITIVE);
	}
}
