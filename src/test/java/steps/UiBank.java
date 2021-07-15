package steps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class UiBank{
	
	private Response response;
	private ValidatableResponse json;
	private RequestSpecification request;

	@Given("enable logs")
	public void setUp(){ 
		request = given().log().all();
	}
	
	
	@When("Authorized using header")
	public void authorize() throws FileNotFoundException, IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream(new File("src/test/resources/config.properties")));
		String auth = prop.getProperty("authorization");
		request = request.header(new Header("Authorization", auth));
	}
	
	@When("Get the account details with user details")
	public void getAccountDetails() {
		response = request
				.queryParam("filter[where][userId]", "5ecfddf3ec543e0044a05c6b")
				.get("/accounts");
	}
	
	
	@When("Login is posted with credentials file: (.*)$")
	public void login(String fileName){
		response = request
				.when()
					.contentType(ContentType.JSON)
					.body(new File("./data/"+fileName))
					.post("/users/login")
				.then()
					.log().all()
				 .extract().response();
	}

	@Then("Verify the status code is (\\d+)$")
	public void verify_status_code(int statusCode){
		json = response.then().statusCode(statusCode);
	}
	
	@Then("Print the userId")
	public void printUserInfo(){
		String user = response.jsonPath().get("userId");
		System.out.println(user);
	}
	
	
	@Then("Print the account number")
	public void printAccountNumber(){
		List<Integer> accountDetails = response.jsonPath().getList("accountNumber");
		for (Integer eachAccount : accountDetails) {
			System.out.println(eachAccount);
		}
	}

	@And("response includes the following$")
	public void response_equals(Map<String,String> responseFields){
		for (Map.Entry<String, String> field : responseFields.entrySet()) {
			if(StringUtils.isNumeric(field.getValue())){
				json.body(field.getKey(), equalTo(Integer.parseInt(field.getValue())));
			}
			else{
				json.body(field.getKey(), equalTo(field.getValue()));
			}
		}
	}

	@And("response includes the following in any order$")
	public void response_contains_in_any_order(Map<String,String> responseFields){
		for (Map.Entry<String, String> field : responseFields.entrySet()) {
			if(StringUtils.isNumeric(field.getValue())){
				json.body(field.getKey(), containsInAnyOrder(Integer.parseInt(field.getValue())));
			}
			else{
				json.body(field.getKey(), containsInAnyOrder(field.getValue()));
			}
		}
	}
}


