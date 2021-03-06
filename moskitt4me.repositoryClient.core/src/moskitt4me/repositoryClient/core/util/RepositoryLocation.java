package moskitt4me.repositoryClient.core.util;

/**
 * A repository location points to a FTP repository that, in the context of MOSKitt4ME, contains reusable
 * assets that are stored as ZIP files according to the Reusable Asset Specification (RAS) standard.
 *
 *  @author Mario Cervera
 */
public class RepositoryLocation {

	// The properties that uniquely identify a repository location are: host and repository path
	
	private String host;
	private String repositoryPath;
	
	// User credentials
	
	private String user;
    	private String password;
    	
    	// Type of repository location (based on the method fragments that it contains):
    	// Empty, Conceptual, or Technical
    	
    	private String type;
    
    	// Constructors
    	
    	public RepositoryLocation() {
    	
	    	this.host = "";
	    	this.repositoryPath = "";
	    	this.user = "";
	    	this.password = "";
	    	
	    	this.type = "";
	}
    
    	public RepositoryLocation(String host, String repositoryPath, String user,
			String password) {
    	
		this.host = host;
		this.repositoryPath = repositoryPath;
		this.user = user;
		this.password = password;
		
		this.type = "";
	}
	
	// Getters and setters
    
	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}

	public String getType() {
	    return type;
	}
	
	public void setType(String type) {
	    this.type = type;
	}
    
}
