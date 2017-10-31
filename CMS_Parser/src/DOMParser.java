import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DOMParser
{
	
   static String XMLFileIdentifier = "";
   static String entity = "";
   
   public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
   {
	      
	   		try {
				DBConnect.cleanEntityDetails();
			} catch (ClassNotFoundException e) {				
				e.printStackTrace();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
	  	  
	  	
   		if (args.length < 1)
        {
            System.out.println("Please Enter File Name as an arg to main()\nUsage: java DOM filename");
            System.exit(1);
        }
    	else {
    		
    		int count = args.length;
    		System.out.println("number of files to be loaded :" + count) ;
    		
    		for(String filename:args){
        		System.out.println(filename);
        		
	        	//get the filename
	        	String[] parts = filename.split("/");
	        	entity = parts[1].replaceAll(".xml", "");
	        	System.out.println(entity);	        
	        		        	
	        	loadDocument(filename);		        
        	}
    		
    	}
    	

   }
   
   private static void loadDocument(String filename){
	   try { 		  
		   	  
		   	  
		   	  //Get Document Builder
		      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();      
		      DocumentBuilder builder;
			
			  builder = factory.newDocumentBuilder();
			    
		      Document document = null;
		      
		      //Build Document		         
			  document = builder.parse(new File(filename));				      
		      
		      //Normalize the XML Structure; 
		      document.getDocumentElement().normalize();
		       
		      //Here comes the root node
		      Element root = document.getDocumentElement();
		      NodeList nList = document.getElementsByTagName(root.getNodeName());
		      
		      visitChildNodes(nList);
		      DBConnect.loadDBTables(entity);
		      
	    } 
	   catch (ParserConfigurationException e) {			
			e.printStackTrace();			   
	    } 
	   catch (ClassNotFoundException | SQLException e) {			
					e.printStackTrace();
		}
	   
	   catch (SAXException e) {		
			e.printStackTrace();
		} 
	   catch (IOException e) {		
			e.printStackTrace();
		}
   }   
 
   //This function is called recursively
   private static void visitChildNodes(NodeList nList)
   {
      for (int temp = 0; temp < nList.getLength(); temp++)
      {
         Node node = nList.item(temp);
         
         if (node.getNodeType() == Node.ELEMENT_NODE)
         {
        	
        	 if (node.getNodeName().contains("id") && node.getNodeName().contains(entity)) {
	        		XMLFileIdentifier = node.getTextContent();					
				}
        	
        	 
        	if(!node.getTextContent().contains("\n\t"))
        	{
        		System.out.println(XMLFileIdentifier + "|" + node.getNodeName() + "|" + node.getTextContent() + "|" + entity);
        		try {
					DBConnect.storeEntityDetails(XMLFileIdentifier,node.getNodeName(),node.getTextContent(),entity);
				} catch (ClassNotFoundException | SQLException e) {					
					e.printStackTrace();
				}
        	}
        	if (node.hasChildNodes()) 
            {
               //We got more childs; Let's visit them as well
               visitChildNodes(node.getChildNodes());
            }
         }
      }
   }
}