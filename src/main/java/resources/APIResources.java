package resources;

public enum APIResources {

	// Use this place to declare your API services
	Users("/users"),
	Unknown("/unknown");

	// Do not touch the below
	private String resource;

	APIResources(String resource) {
		this.resource = resource;
	}

	public String getResource() {
		return resource;
	}
}
