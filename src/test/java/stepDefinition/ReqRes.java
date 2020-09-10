package stepDefinition;

import java.io.IOException;
import java.util.Map;
import io.cucumber.java.en.*;
import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.assertEquals;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import resources.APIResources;
import resources.TestDataBuild;
import resources.Utilities;

public class ReqRes extends Utilities {

	RequestSpecification request;
	APIResources resourceAPI;
	TestDataBuild payload = new TestDataBuild();
	Response res;

	@Given("I make a call to {string} of {string} API")
	public void i_make_a_call_to_of_api(String resource, String api) throws IOException {

		resourceAPI = APIResources.valueOf(resource);

		request = given().spec(RequestSpecification(api));

	}

	@Given("inputs are {string} and {string}")
	public void inputs_are_and(String name, String job) {

		if (resourceAPI.getResource().equalsIgnoreCase("/users")) {
			request.body(payload.userPayload(name, job));
		}

	}

	@Given("inputs for {string} are fetched from {string} of {string}")
	public void inputs_for_are_fetched_from_of(String TCID, String dataSource, String path) throws Exception {
		
		if(dataSource=="Sheet1") {
		Map<String, String> TestDataInMap = getTestDataInMap(path,dataSource,TCID);
		if (resourceAPI.getResource().equalsIgnoreCase("/users")) {
			request.body(payload.userPayload(TestDataInMap.get("Name"), TestDataInMap.get("Job")));
		}
		else if(dataSource=="DB"){
			Map<String, String> TestDataFromDB = getTestDataFromDB();
			if (resourceAPI.getResource().equalsIgnoreCase("/users")) {
				request.body(payload.userPayload(TestDataFromDB.get("Name"), TestDataFromDB.get("Job")));
		}
		}
	}
		
	}
	
	/*@Given("inputs for {string} are fetched from {string}")
	    public void inputs_for_something_are_fetched_from_something(String TCID, String DataSource) throws Throwable {
		
		if(DataSource=="Sheet1") {
			System.out.println("Inside sheet");
			System.out.println("kjabdfkabakfbak");
			Map<String, String> TestDataInMap = TEST(DataSource, TCID);
			if (resourceAPI.getResource().equalsIgnoreCase("/users")) {
				request.body(payload.userPayload(TestDataInMap.get("Name"), TestDataInMap.get("Job")));
			}
		}
		else if(DataSource=="DB") {
			System.out.println("Inside DB");
			Map<String, String> TestDataInMap = getTestDataFromDB();
			if (resourceAPI.getResource().equalsIgnoreCase("/users")) {
				System.out.println(TestDataInMap.get("Name"));
				System.out.println(TestDataInMap.get("Job"));
				request.body(payload.userPayload(TestDataInMap.get("Name"), TestDataInMap.get("Job")));
			}
		}
		
		
	    }*/

	@When("Method is {string} type is {string} with parameter {string} and value as {string}")
	public void method_is_type_is_with_parameter_and_value_as(String method, String type, String parameter,
			String value) {

		if (method.equalsIgnoreCase("get")) {

			if (type.equalsIgnoreCase("query")) {
				res = request.when().queryParam(parameter, value).get(resourceAPI.getResource());
			} else if (type.equalsIgnoreCase("path")) {
				res = request.when().get(resourceAPI.getResource() + "/" + value);
			}

		}
	}

	@When("Method is {string}")
	public void method_is(String method) {

		if (method.equalsIgnoreCase("get")) {
			res = request.when().get(resourceAPI.getResource());
		} else if (method.equalsIgnoreCase("post")) {
			res = request.contentType(ContentType.JSON).when().post(resourceAPI.getResource());
		}
	}

	@Then("response has status code as {int}")
	public void response_has_status_code_as(Integer code) {
		assertEquals(code.intValue(), res.getStatusCode());
	}

	@Then("{string} in response body is {int}")
	public void in_response_body_is(String key, Integer value) {
		assertEquals(value, getJsonPathInt(res, key));
	}

	@Then("{string} in response body is {string}")
	public void in_response_body_is(String key, String value) {
		assertEquals(value, getJsonPathString(res, key));
	}

	@Then("schema matches {string}")
	public void schema_matches(String schemaName) {
		res.then().body(matchesJsonSchemaInClasspath(schemaName + ".json"));
	}

	@Then("json matches with {string}")
	public void json_matches_with(String path) throws IOException {

		String actual = res.then().extract().asString();
		assertThatJson(actual).isEqualTo(getJsonFromFile(path));

	}
}


/*Scenario Outline: Test to verify if we are able to post data to "Users" resource by fetching input from excel
Given I make a call to "Users" of "ReqRes" API
And inputs for "<TCID>" are fetched from "Sheet1" of "Users.xlsx"
And inputs for "<TCID>" are fetched from "Sample" of "DEMOTrial"
When Method is "Post"
Then response has status code as 201*/