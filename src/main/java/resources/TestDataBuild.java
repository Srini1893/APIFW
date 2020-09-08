package resources;

import java.util.ArrayList;
import java.util.List;

import pojos.Users;

public class TestDataBuild {

	public Users userPayload(String name, String job) {
		Users upayload = new Users();

		upayload.setName(name);
		upayload.setJob(job);
		
		return upayload;
	}

}
