import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;



public class ChecksumService {
	private MessageDigest messageDigest;
	private Driver driver;
	private static byte[] key;
	private static SecretKeySpec secretKey;
	public ChecksumService(Driver driver,String secret) {

		try {
			setKey(secret);
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.driver=driver;

	}
	private String calculateChecksum (String filepath, MessageDigest md) throws FileNotFoundException, IOException  {
		// file hashing with DigestInputStream
		try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filepath), md)) {
			System.out.println(dis);
			while (dis.read() != -1) ; //empty loop to clear the data
			md = dis.getMessageDigest();
		}
		// bytes to hex
		StringBuilder result = new StringBuilder();
		for (byte b : md.digest()) {
			result.append(String.format("%02x", b));
		}
		System.out.println("res: "+result.toString());
		return result.toString();

	}

	public File[] listf(String directoryName,ArrayList<File> files) throws FileNotFoundException, IOException  {

		File[] fList = null;
		File directory = new File(directoryName);
		fList = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".DS_Store");
			}
		});
		for (File file : fList) {
			if (file.isFile()&&file.canRead()) {
				System.out.println("file: "+file.getAbsolutePath()+" "+file.getName());
				files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(),files);
			}
		}
		return fList;
	}
	public void createHashes(String directoryName) throws FileNotFoundException, IOException {
		//File[] fList=listf(directoryName);
		ArrayList<File> files=new ArrayList<>();
		listf(directoryName,files);
		for(int i=0;i<files.size();i++) {
			File file=files.get(i);
			//String calculated=file.getAbsolutePath()+","+Integer.toString(i)+",";
			String calculated=calculateChecksum(file.getAbsolutePath(),messageDigest)+","+Integer.toString(i)+":";;
			if(i==files.size()-1) {
				calculated+=Integer.toString(Level.LAST.getLevel());
			}
			else {
				calculated+=Integer.toString(Level.NOTLAST.getLevel());
			}
			//HashFile hFile=new HashFile(file.getAbsolutePath(),encrypt(calculateChecksum(file.getAbsolutePath(),messageDigest)));
			HashFile hFile=new HashFile(file.getAbsolutePath(),encrypt(calculated));
			driver.addElement(hFile);
		}
	}
	public ArrayList<HashFile> calculateNewHashes(String directoryName) throws FileNotFoundException, IOException{
		ArrayList<File> files=new ArrayList<>();
		listf(directoryName,files);
		ArrayList<HashFile> hashedFiles=new ArrayList<>();
		for(int i=0;i<files.size();i++) {
			File file=files.get(i);
			HashFile hFile=new HashFile(file.getAbsolutePath(),calculateChecksum(file.getAbsolutePath(),messageDigest));
			hashedFiles.add(hFile);
		}
		return hashedFiles;

	}
	public static void setKey(String myKey) 
	{
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); 
			secretKey = new SecretKeySpec(key, "AES");
		} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	public static String encrypt(String strToEncrypt) 
	{
		try
		{
			//setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} 
		catch (Exception e) 
		{
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}
	public static String decrypt(String strToDecrypt) 
	{
		try
		{
			//setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} 
		catch (Exception e) 
		{
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}
	public ArrayList<HashFile> getHashedFiles(){
		ArrayList<HashFile> encryptedFiles=new ArrayList<>();
		encryptedFiles=driver.getElements();
		for(int i=0;i<encryptedFiles.size();i++) {
			encryptedFiles.get(i).setHashValue(decrypt(encryptedFiles.get(i).getHashValue()));
		}
		return encryptedFiles;
		
		
		
	}

}
