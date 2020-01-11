
public enum Level {
	LAST(0),
	NOTLAST(1);
	
	private int level;

    Level(int level) {
        this.level = level;
    }

    public int getLevel() { 
        return level;
    }
}
