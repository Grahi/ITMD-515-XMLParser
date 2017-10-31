import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class SaxParser {
	
	static String XMLFileIdentifier = "";	
	static String entity = "";
	static String filename = "";
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, VerifyError {          

        SAXParserFactory saxDoc = SAXParserFactory.newInstance();
        SAXParser saxParser = saxDoc.newSAXParser();        
        
        DefaultHandler handler = new DefaultHandler() {
            String tmpElementName = null;
            String tmpElementValue = null;
            Map<String,String> tmpAtrb=null;           
            
            
            @Override
            public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException {
                tmpElementValue = "";
                tmpElementName = qName;
                tmpAtrb=new HashMap();
                System.out.println("Start Element :" + qName);
                /**
                 * Store attributes in HashMap
                 */
                for (int i=0; i<attributes.getLength(); i++) {
                    String aname = attributes.getLocalName(i);
                    String value = attributes.getValue(i);
                    tmpAtrb.put(aname, value);
                }
                
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException 
            { 
            	if (qName.contains("id") && qName.contains(entity)) {
	        		XMLFileIdentifier = tmpElementValue;					
				}
            	
                 	
                if(tmpElementName.equals(qName))
                {
                	System.out.println(XMLFileIdentifier + "|" + tmpElementName + "|" + tmpElementValue + "|" + entity);
                	try {
						DBConnect.storeEntityDetails(XMLFileIdentifier,tmpElementName,tmpElementValue,entity);
						
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
	            }
            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {
                tmpElementValue = new String(ch, start, length) ;
            }
			
            @Override
            public void startDocument() throws SAXException
            {
                 	
            	System.out.println("begin document...");            	
            }
            
            @Override
            public void endDocument() throws SAXException
            {
                 	
            	System.out.println("End document...");            	
            }
            
            
			
        };
        try {     	
      	        	
	        
        	//Call each XML file to parse
        	if (args.length < 1)
            {
                System.out.println("Please Enter File Name as an arg to main()\nUsage: java SAXDemo filename");
                System.exit(1);
            }
        	else {
	        	
        		int count = args.length;
        		System.out.println("number of files to be loaded :" + count) ;
        		
        		//clean out the table
	        	DBConnect.cleanEntityDetails();
        		
	        	for(String filename:args){
	        		System.out.println(filename);
	        		
		        	//get the filename
		        	String[] parts = filename.split("/");
		        	entity = parts[1].replaceAll(".xml", "");
		        	System.out.println(entity);	        
		        		        	
			        saxParser.parse(new File(filename), handler);
			        DBConnect.loadDBTables(entity);
	        	}
	          }
	        
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		} catch (SQLException e) {			
			e.printStackTrace();
		}      
        
    }
}