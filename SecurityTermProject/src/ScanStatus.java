import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class ScanStatus {
	///Users/semra/Desktop/deneme/
	public static void main(String[] args) {
		Scanner scanner=new Scanner(System.in);
		int input=-1;
		do {
			System.out.println("For default scan press 0\nFor specific scan press 1");
			input=scanner.nextInt();
		}while((input!=0)&&(input!=1));
		scanner.nextLine();
		System.out.println("Enter the secret key: ");
		String secret=scanner.next();
		//scanner.nextLine();
		Driver driver=new Driver();
		driver.openConnection();
		driver.truncateTable();
		scanner.nextLine();
		ChecksumService checksumService=new ChecksumService(driver,secret);
		String name;
		switch(input) {
		case 0:
			name="/usr/bin/";

			try {
				checksumService.createHashes(name);
				driver.closeConnection();
			} catch (/*IOException | */SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case 1:
			System.out.println("Enter directory path or file path:");
			name=scanner.nextLine();
			try {
				checksumService.createHashes(name);
				//driver.addFileLocation(name, 1);
				driver.closeConnection();
			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		}
		scanner.close();
	}

}
