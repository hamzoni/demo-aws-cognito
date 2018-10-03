package app.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserResult;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;

@RestController
public class MainController {

	private String accessKey = "AKIAJF3KWZYADJWJAQZA";
	private String secretKey = "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";
	private String poolId = "us-west-2_dPNRxjpTh";

	private AWSCognitoIdentityProvider getAWSCognitoClient() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
		return AWSCognitoIdentityProviderClientBuilder.standard().withCredentials(provider)
				.withRegion("us-west-2").build();
	}
	
	@PostMapping("/login")
	public Object OauthApplication(@RequestBody LoginDto loginDto) {
		
		AdminInitiateAuthRequest adminAuthRequest = new AdminInitiateAuthRequest();
		adminAuthRequest.withUserPoolId(poolId);
		adminAuthRequest.withClientId("72gl06t8742i94rgf1pn3vd0t8");
		adminAuthRequest.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("USERNAME", loginDto.getUsername());
		params.put("PASSWORD", loginDto.getPassword());
		adminAuthRequest.withAuthParameters(params);
		
		// Test
		
		AdminInitiateAuthResult authResult = getAWSCognitoClient().adminInitiateAuth(adminAuthRequest);
		return authResult;
	}
	
	@DeleteMapping("{username}")
	public Object deleteUser(@PathVariable("username") String username) {
		AdminDeleteUserRequest deleteUserRequest = new AdminDeleteUserRequest();
		deleteUserRequest.withUserPoolId(poolId);
		deleteUserRequest.withUsername(username);
		
		AdminDeleteUserResult deleteResult = getAWSCognitoClient().adminDeleteUser(deleteUserRequest);
		return deleteResult;
	}

	@PostMapping
	public Object createUser(@RequestBody UserDto userDto) {
		AdminCreateUserRequest createUserRequest = new AdminCreateUserRequest()
				.withUserPoolId(poolId)
				.withUsername(userDto.getUsername());
		
		List<AttributeType> userAttributes = new ArrayList<AttributeType>();
		
		AttributeType attName = new AttributeType();
		attName.setName("name");
		attName.setValue(userDto.getName());
		
		AttributeType attEmail = new AttributeType();
		attEmail.setName("email");
		attEmail.setValue(userDto.getEmail());
		
		userAttributes.add(attName);
		userAttributes.add(attEmail);
		userAttributes.add(new AttributeType().withName("custom:qt").withValue("123"));
		
		createUserRequest.setUserAttributes(userAttributes);
		AdminCreateUserResult createResult = getAWSCognitoClient().adminCreateUser(createUserRequest);
		return createResult;
	}
	
	@GetMapping
	public Object listUsers() {

		ListUsersRequest listUserRequest = new ListUsersRequest().withUserPoolId(poolId);
		ListUsersResult listUsersResult = getAWSCognitoClient().listUsers(listUserRequest);

		return listUsersResult.getUsers();
	}
	
	@GetMapping("${username}")
	public Object findUser(@PathVariable("username") String username) {
		AdminGetUserRequest adminGetUserRequest = new AdminGetUserRequest().withUserPoolId(poolId)
				.withUsername(username);
		AdminGetUserResult adminGetUserResult = getAWSCognitoClient().adminGetUser(adminGetUserRequest);
		
		return adminGetUserResult;
	}

}
