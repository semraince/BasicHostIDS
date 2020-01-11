import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class ModifiedStatus {

	public static void main(String[] args) {
		System.out.println("File checking is starting...");
		Scanner scanner=new Scanner(System.in);
		System.out.println("Enter the secret key: ");
		String secret=scanner.next();
		scanner.nextLine();
		int input=-1;
		do {
			System.out.println("For default scan press 0\nFor specific scan press 1");
			input=scanner.nextInt();
		}while((input!=0)&&(input!=1));
		scanner.nextLine();
		String name;
		switch(input) {
		case 0:
			name="/usr/bin/";
			try {
				checkStatus(name,secret);
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("TO DO");
			
			break;
		case 1:
			System.out.println("Enter directory path or file path to compare:");
			name=scanner.nextLine();
			try {
				checkStatus(name,secret);
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		}
		scanner.close();

	}
	public static void checkStatus(String name,String secret) throws SQLException, FileNotFoundException, IOException {
		Driver driver=new Driver();
		driver.openConnection();
		ChecksumService checksumService=new ChecksumService(driver,secret);
		ArrayList<HashFile> newHashedFiles = null;
		//if(status==1) {
		newHashedFiles=checksumService.calculateNewHashes(name);

		//}
		ArrayList<HashFile> hashedFiles=checksumService.getHashedFiles();//driver.getElements();
		Collections.sort(newHashedFiles, new CustomFileSort());
		Collections.sort(hashedFiles, new CustomFileSort());
		ArrayList<String> copyHashedFiles=new ArrayList<String>();
		boolean control=true;
		int sum=0;//sum of all
		int total=0;//just last bit
		//for(HashFile x:hashedFiles) {
		for(int i=0;i<hashedFiles.size();i++) {
			HashFile x=hashedFiles.get(i);
			String hashdigest=x.getHashValue().substring(0,x.getHashValue().lastIndexOf(','));
			String check=x.getHashValue().substring(x.getHashValue().lastIndexOf(',')+1);
			System.out.println(check+"     "+hashdigest);
			String[] parts = check.split(":");
			//System.out.println(parts[0]+"   "+parts[1]);
			int level = Integer.parseInt(parts[0]); // 004
			int lastbit = Integer.parseInt(parts[1]); // 03
			System.out.println("hash file name:" +x.getFileName());
			sum+=level;
			if(lastbit==Level.LAST.getLevel()) {
				total=level;
			}
			hashedFiles.get(i).setHashValue(hashdigest);
		}
		//System.out.println("sum is "+sum+ " total is "+total);
		if((total*(total+1)/2)!=sum) {
			System.out.println("There was a conflict. Some file(s) were deleted in database");
			control=false;
		}
		//if(control) {
			for(HashFile x:hashedFiles) {
				copyHashedFiles.add(x.getFileName());
				//System.out.println (x.getHashValue());
			}
			ArrayList<String> copyNewHashedFiles=new ArrayList<String>();
			for(HashFile x:newHashedFiles) {
				copyNewHashedFiles.add(x.getFileName());
			}
			//check database structure

			boolean check=true;
			for(HashFile curFile:newHashedFiles)  {
				for(HashFile pastFile:hashedFiles) {
					if(curFile.getFileName().equals(pastFile.getFileName())) {//file names match
						if(!(curFile.getHashValue().equals(pastFile.getHashValue()))) {
							System.out.println("The file "+curFile.getFileName()+" has changed.");
							check=false;
						}
						copyNewHashedFiles.remove(curFile.getFileName());
						copyHashedFiles.remove(pastFile.getFileName());
					}
				}
			}
			if(copyNewHashedFiles.size()==0&&copyHashedFiles.size()==0&&check&&control) {
				System.out.println("There are no new files were added,and no files were removed. All hashes are as before");
			}
			if(copyNewHashedFiles.size()>0) {
				for(String curFile:copyNewHashedFiles) {
					System.out.println("The file "+curFile+" is in folder but there is no such a file before in database.");
				}
			}
			if(copyHashedFiles.size()>0) {
				for(String pastFile:copyHashedFiles) {
					System.out.println("The file "+pastFile+" is in database but not in folder.");
				}
			}
		//}
		driver.closeConnection();

	}
}
