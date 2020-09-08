package resources;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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

	public RequestSpecification RequestSpecification(String api) throws IOException {
		if (req == null) {
			PrintStream log = new PrintStream(
					new FileOutputStream(System.getProperty("user.dir") + "\\logs\\" + date + ".txt"));

			req = new RequestSpecBuilder().setBaseUri(getGlobalProperties("baseURI_" + api))
					.addFilter(RequestLoggingFilter.logRequestTo(log))
					.addFilter(ResponseLoggingFilter.logResponseTo(log)).setRelaxedHTTPSValidation().build();
			return req;
		}
		return req;
	}

	public static String getGlobalProperties(String key) throws IOException {
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\main\\java\\resources\\Global.properties");
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

}
