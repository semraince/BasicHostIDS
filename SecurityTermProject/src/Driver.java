
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
public class Driver {
	private String db_connect_string;
	private String userName;
	private String password;
	private Connection connection;
	private Statement statement;

	public  void openConnection() {
		FileReader reader;
		try {
			reader = new FileReader("db.properties");
			Properties p=new Properties();  
			p.load(reader);  
			// you can get values you want as properties using
			this.db_connect_string = p.getProperty("db_connect_string");  
			this.userName=p.getProperty("userName");
			this.password=p.getProperty("password");
			System.out.println(password);
			System.out.println(db_connect_string);
			connection=DriverManager.getConnection(this.db_connect_string,this.userName,this.password);
			statement=connection.createStatement();


		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

	}

	public ArrayList<HashFile> getElements(){
		ArrayList<HashFile> hashedFiles=new ArrayList<>();
		try {
			ResultSet resultset=statement.executeQuery("SELECT * FROM hashtable;");
			while(resultset.next()) {
				System.out.println(resultset.getString("name")+resultset.getString("hashvalue"));
				String name=resultset.getString("name");
				String hashValue=resultset.getString("hashvalue");
				hashedFiles.add(new HashFile(name,hashValue));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hashedFiles;
	}
	public void addElement(HashFile hashFile) {
	
		String query=String.format("INSERT INTO HashDB.hashtable (name,hashvalue) VALUES (\"%s\",\"%s\");", hashFile.getFileName(),hashFile.getHashValue());
		try {
			System.out.println(hashFile.getFileName()+"   "+hashFile.getHashValue());
			statement.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addFileLocation(String name,int id) {
		String query=String.format("INSERT INTO HashDB.hashtable (name,hashvalue) VALUES (\"%s\",\"location-%d\");", name,id);
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void truncateTable() {
		String query="TRUNCATE HashDB.hashtable";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void closeConnection() throws SQLException {
		connection.close();
	}



}
