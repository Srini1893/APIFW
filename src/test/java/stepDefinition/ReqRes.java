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
	public void inputs_for_are_fetched_from_of(String TCID, String sheet, String path) throws Exception {
		Map<String, String> TestDataInMap = getTestDataInMap(path, sheet, TCID);
		if (resourceAPI.getResource().equalsIgnoreCase("/users")) {
			request.body(payload.userPayload(TestDataInMap.get("Name"), TestDataInMap.get("Job")));
		}
	}

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