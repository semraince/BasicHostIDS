
public class HashFile {
	private String fileName;
	private String hashValue;
	public HashFile(String fileName,String hashValue) {
		this.fileName=fileName;
		this.hashValue=hashValue;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getHashValue() {
		return hashValue;
	}
	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}
	

}
