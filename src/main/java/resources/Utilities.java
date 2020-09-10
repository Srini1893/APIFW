package resources;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Utilities {

	public static RequestSpecification req;

	SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmmss");
	String date = formatter.format(new Date());
	static Properties prop;
	static FileInputStream fis;


	public String environment() throws IOException {
		
		String env=getGlobalProperties("Env");

	    return env;
	}

	public RequestSpecification RequestSpecification(String api) throws IOException {
		if (req == null) {
			PrintStream log = new PrintStream(new FileOutputStream(System.getProperty("user.dir") + "\\logs\\" + date + ".txt"));
			String baseUri=environment()+"_"+ api;
			req = new RequestSpecBuilder().setBaseUri(getGlobalProperties(baseUri))
					.addFilter(RequestLoggingFilter.logRequestTo(log))
					.addFilter(ResponseLoggingFilter.logResponseTo(log)).setRelaxedHTTPSValidation().build();
			
			
			return req;
		}
		return req;
	}

	public static String getGlobalProperties(String key) throws IOException {
		prop = new Properties();
		fis = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\java\\resources\\Global.properties");
		prop.load(fis);

		return prop.getProperty(key);

	}

	public static Integer getJsonPathInt(Response response, String key) {
		String complete = response.asString();
		JsonPath js = new JsonPath(complete);
		return Integer.parseInt(js.get(key).toString());
	}

	public static String getJsonPathString(Response response, String key) {
		String complete = response.asString();
		JsonPath js = new JsonPath(complete);
		return js.get(key).toString();
	}

	public static String getJsonFromFile(String location) throws IOException {

		String contents = new String(Files.readAllBytes(
				Paths.get(System.getProperty("user.dir") + "\\src\\test\\resources\\jsons\\" + location)));
		return contents;

	}

	public static Map<String, String> getTestDataInMap(String testDataFile, String sheetName, String testCaseId)
			throws Exception {
		Map<String, String> TestDataInMap = new TreeMap<String, String>();
		String query = null;
		query = String.format("SELECT * FROM %s WHERE TestCaseID='%s'", sheetName, testCaseId);
		Fillo fillo = new Fillo();
		Connection conn = null;
		Recordset recordset = null;
		try {
			conn = fillo
					.getConnection(System.getProperty("user.dir") + "\\src\\test\\resources\\sources\\" + testDataFile);
			recordset = conn.executeQuery(query);
			while (recordset.next()) {
				for (String field : recordset.getFieldNames()) {
					TestDataInMap.put(field, recordset.getField(field));
				}
			}
		} catch (FilloException e) {
			e.printStackTrace();
			throw new Exception("Test data not found");
		}
		conn.close();
		return TestDataInMap;
	}
	
	public static Map<String, String> getTestDataFromDB() throws SQLException{
		String url = "jdbc:mysql://db4free.net:3306/demotrial";
        String user = "wilfred7";
        String password = "d3708839";
        java.sql.Connection conn= null;
        Map<String, String> TestDataFromDB = new TreeMap<String, String>();

        try {
             conn =DriverManager.getConnection(url, user, password);
               {
                     if (conn != null) {

                            PreparedStatement pst = conn.prepareStatement("select * from Sample");
                            ResultSet rs = pst.executeQuery();
                            {
                                  while (rs.next()) {

                                         String Name = rs.getString("Name");
                                         String Job = rs.getString("Job");
                                         TestDataFromDB.put(Name, Job);
                                         //System.out.println("The value from the table is : " + Name);
                                         //System.out.println("The value from the table is : " + Job);
                                  }
                            }

                     } else
                            System.out.println("Failed to connect");
               }

        } catch (SQLException e) {
               System.out.println(e.getMessage());
        }
        conn.close();
		return TestDataFromDB;
		
	}
	
	public static Map<String, String> TEST(String sheetName, String testCaseId)
			throws Exception {
		String testDataFile ="Users.xlsx";
		Map<String, String> TestDataInMap = new TreeMap<String, String>();
		String query = null;
		query = String.format("SELECT * FROM %s WHERE TestCaseID='%s'", sheetName, testCaseId);
		Fillo fillo = new Fillo();
		Connection conn = null;
		Recordset recordset = null;
		try {
			conn = fillo
					.getConnection(System.getProperty("user.dir") + "\\src\\test\\resources\\sources\\" + testDataFile);
			recordset = conn.executeQuery(query);
			while (recordset.next()) {
				for (String field : recordset.getFieldNames()) {
					TestDataInMap.put(field, recordset.getField(field));
				}
			}
		} catch (FilloException e) {
			e.printStackTrace();
			throw new Exception("Test data not found");
		}
		conn.close();
		return TestDataInMap;
	}

}
