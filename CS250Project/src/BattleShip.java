//Battleship class
//add Oct 25, 2024
//import java.util.Random;

public class BattleShip {
	private int length = 10;
	private char ship;
	private char hit;
	private char miss;
	
	
	
	public BattleShip(int SIZE, char S, char X, char O) {
		length = SIZE;
		char ship = S;
		char hit = X;
		char miss = O;
	}
	
	public void setLength(int SIZE){ //setter method
		length = SIZE; // set
	}
	
	public void setShip(char S){ //setter method
		ship = S; // set
	}
	public void setHit(char X){ //setter method
		hit = X; // set
	}
	public void setMiss(char O){ //setter method
		miss = O; // set
	}
	public int getLength(){ //setter method
		return length; // set
	}
	
	public char getShip(){ //setter method
		return ship; // set
	}
	public char getHit(){ //setter method
		return hit; // set
	}
	public char getMiss(){ //setter method
		return miss; // set
	}
}
