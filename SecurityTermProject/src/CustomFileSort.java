import java.util.Comparator;

public class CustomFileSort implements Comparator<HashFile>{

	@Override
	public int compare(HashFile o1, HashFile o2) {
		// TODO Auto-generated method stub
		return o1.getFileName().compareTo(o2.getFileName());
	}
	 
	   
}
