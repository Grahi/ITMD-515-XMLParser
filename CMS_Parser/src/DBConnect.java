import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class DBConnect {
	
	//static String connUrl = "jdbc:mysql://localhost:3306/cms";
	
	static Properties prop;
	
	static{
        InputStream is = null;
        try {
            
        	prop = new Properties();
            is = new FileInputStream("resources/config.properties");
            prop.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static String getPropertyValue(String key){
        return prop.getProperty(key);
    }
	     
	public static Connection getDBConnection(){
		Connection con;
		try{			
			Class.forName(getPropertyValue("drivername"));	
			con = DriverManager.getConnection(getPropertyValue("dbconnUrl"),getPropertyValue("dbuser"), getPropertyValue("dbpassword"));
			return con;
		}catch(SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}
		return null; 
		
	}
	
	public static void cleanEntityDetails() throws ClassNotFoundException, SQLException
	{
		
		try{
			Connection con = getDBConnection();						
			Statement s1 = con.createStatement();
			String query = "TRUNCATE TABLE RawXMLData";		 			
			s1.executeUpdate(query);
		}catch(SQLException e) {
			e.printStackTrace();
		} 
		
	}
	
	public static void storeEntityDetails(String Id,String AttributeName,String AttributeValue,String Entity) throws ClassNotFoundException, SQLException
	{
		try{
			Connection con = getDBConnection();
			Statement s1 = con.createStatement();
			String query =  "INSERT INTO RawXMLData(Id,AttributeName,AttributeValue,Entity) "
					+ "VALUES ('" + Id + "','" + AttributeName + "','" + AttributeValue + "','" + Entity + "')";	 
			s1.executeUpdate(query);
		}catch(SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public static void loadDBTables(String Entity) throws ClassNotFoundException, SQLException
	{
		try{
			Connection con = getDBConnection();
			CallableStatement cs = null;
			cs = con.prepareCall("{call LoadDataIntoTable(?)}");        
			cs.setString(1,Entity);
	        cs.execute();	
		}catch(SQLException e) {
			e.printStackTrace();
		} 
		
	}
	
}
