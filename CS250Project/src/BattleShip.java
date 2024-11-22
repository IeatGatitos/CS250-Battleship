//Battleship class
//add Oct 25, 2024
//import java.util.Random;

public class BattleShip {
    private int length;
    private int[][] position; //array to store the coordinates of each part of the ship
    private boolean[] hits;   //array to track if each part of the ship has been hit
    private boolean isHorizontal;

    //constructor to initialize a ship with a given length and orientation
    public BattleShip(int length, boolean isHorizontal) {
        this.length = length;
        this.isHorizontal = isHorizontal;
        this.hits = new boolean[length];
    }

    /*
     * place the ship at a given starting position
     * 
     */
    public void placeShip(int startX, int startY) {
        position = new int[length][2];
        for (int i = 0; i < length; i++) {
            position[i][0] = startX + (isHorizontal ? i : 0);
            position[i][1] = startY + (isHorizontal ? 0 : i);
        }
    }

    //check if a shot hits the ship
    public boolean checkHit(int x, int y) {
        for (int i = 0; i < length; i++) {
            if (position[i][0] == x && position[i][1] == y) {
                hits[i] = true;
                return true;
            }
        }
        return false;
    }

    //check if the ship has been sunk
    public boolean isSunk() {
        for (boolean hit : hits) {
            if (!hit) return false;
        }
        return true;
    }

    //getter methods
    public int getLength() {
        return length;
    }

    public int[][] getPosition() {
        return position;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }
    
    public void setHorizontal(boolean horizontal) {
    	isHorizontal = horizontal; 
    }
}