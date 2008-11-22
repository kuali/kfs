import java.io.*;
import java.util.regex.*;

/**
 *  Description of the Class
 *
 *@author     szmcglin
 *@created    November 21, 2008
 */
public class MakeFragments {

	/**
	 *  Constructor for the MakeFragments object
	 *
	 *@param  fileType  Description of the Parameter
	 */
	MakeFragments(String fileType) {
//def fileType="ruleTemplate" //ruleAttribute, documentType
		String bigType = fileType.substring(0, 1).toUpperCase() + fileType.substring(1);
		Pattern p = Pattern.compile("([A-Z]+.*)");
		Matcher m = p.matcher(fileType);
		String sf = "";
		if (m.find()) {
			sf = m.group();
		}

		String sourceFile = sf + "s.xml";
		if (sf.equals("Type")) {
			sourceFile = "DocTypes.xml";
		}
		BufferedReader attrib = null;
		try {
			attrib = new BufferedReader(new FileReader(sourceFile));
		} catch (FileNotFoundException fnfe) {
			System.out.println("File not found");
		}
		String hdrx = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		hdrx = hdrx + "<data xmlns=\"ns:workflow\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"ns:workflow resource:WorkflowData\">\n";
		hdrx = hdrx + "<" + fileType + "s xmlns=\"ns:workflow/" + bigType + "\" xsi:schemaLocation=\"ns:workflow/" + bigType + " resource:" + bigType + "\">\n";
		String tlx = "</" + fileType + ">\n";
		tlx = tlx + "</" + fileType + "s>\n";
		tlx = tlx + "</data>\n";

		boolean flag = false;
		String tempPage = "";
		String name = "blank";
		String tempVar;
		try {
			while ((tempVar = attrib.readLine()) != null) {
				p = Pattern.compile("<" + fileType + ">");
				m = p.matcher(tempVar);
				if (m.find()) {
					tempVar = m.group();
					flag = true;
					tempPage = tempPage + hdrx;
				}
				p = Pattern.compile("</" + fileType + ">");
				m = p.matcher(tempVar);
				if (m.find()) {
					tempVar = m.group();
					flag = false;
					tempPage = tempPage + tlx;
					File outFile = new File("./temp/" + name + ".xml");
					outFile.delete();

					FileWriter outWriter = new FileWriter(outFile);
					outWriter.write(tempPage);
					outWriter.close();
					tempPage = "";
					System.out.println(name + ".xml created\n");
					name = "blank";
				}
				p = Pattern.compile("<name>(.*)</name>");
				m = p.matcher(tempVar);
				if (m.find()) {
					tempVar = m.group();
					if (name.equals("blank")) {
						name = m.group(1);
					}
				}
				if (flag) {
					tempPage = tempPage + tempVar + "\n";
				}
			}
		} catch (IOException ioe) {
			System.out.println("IOException "+ioe);
		}
	}

}
