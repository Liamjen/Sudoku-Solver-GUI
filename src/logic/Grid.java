package logic;

import java.awt.Point;
import java.util.*;


public class Grid 
{
	private int[][]						values;
	

	//
	// DON'T CHANGE THIS.
	//
	// See TestGridSupplier for examples of input.
	// Dots in input strings become 0s in values[][].
	//
	public Grid(String[] rows)
	{
		values = new int[9][9];
		for (int j=0; j<9; j++)
		{
			String row = rows[j];
			char[] charray = row.toCharArray();
			for (int i=0; i<9; i++)
			{
				char ch = charray[i];
				if (ch != '.')
					values[j][i] = ch - '0';
			}
		}
	}
	
	/**
	 * Method used to set values of Grid from Combo Box / Choice Box (Since Grid must be final)
	 * @param rows
	 */
	public void setValues(int[][] values)
	{
		this.values = values;
	}
	
	
	//
	// DON'T CHANGE THIS.
	//
	public String toString()
	{
		String s = "";
		for (int j=0; j<9; j++)
		{
			for (int i=0; i<9; i++)
			{
				int n = values[j][i];
				if (n == 0)
					s += '.';
				else
					s += (char)('0' + n);
			}
			s += "\n";
		}
		return s;
	}
	
	
	//
	// COMPLETE THIS
	//
	//
	// Finds an empty member of values[][]. Returns an array list of 9 grids that look like the current grid,
	// except the empty member contains 1, 2, 3 .... 9. Returns null if the current grid is full.
	//
	// Example: if this grid = 1........
	//                         .........
	//                         .........
	//                         .........
	//                         .........
	//                         .........
	//                         .........
	//                         .........
	//                         .........
	//
	// Then the returned array list would contain:
	//
	// 11.......          12.......          13.......          14.......    and so on     19.......
	// .........          .........          .........          .........                  .........
	// .........          .........          .........          .........                  .........
	// .........          .........          .........          .........                  .........
	// .........          .........          .........          .........                  .........
	// .........          .........          .........          .........                  .........
	// .........          .........          .........          .........                  .........
	// .........          .........          .........          .........                  .........
	// .........          .........          .........          .........                  .........
	//
	public ArrayList<Grid> next9Grids()
	{	
		ArrayList<Grid> possibleNextGrids = new ArrayList<Grid>();
		
		for(int i = 0; i < 9; i++)							//Checks value row
		{
			for(int j = 0; j < 9; j++)						//Checks value column
			{
				if(values[i][j] == 0)						//Finds next empty spot in values
				{
					int[][] valuesCopy = new int[9][9];
					for(int v = 0; v < 9; v++)				//Copies values to valueCopy
						System.arraycopy(values[v], 0, valuesCopy[v], 0, 9);
					
					for(int k = 1; k <= 9; k++)				//1-9 next possible numbers loop
					{
						valuesCopy[i][j] = k;				//Sets empty space to 1-9
						String[] stringValues = new String[9];
						for(int n = 0; n < 9; n++)
						{
							stringValues[n] = "";			//Initialize the stringValues string at n
							for(int m = 0; m < 9; m++)		//Changes valuesCopy to Grid ctor format through stringValues
							{
								if(valuesCopy[n][m] != 0)
									stringValues[n] += Integer.toString(valuesCopy[n][m]);
								else
									stringValues[n] += ".";	//Changes 0 to .
							}
						}
						possibleNextGrids.add(new Grid(stringValues));
					}
					return possibleNextGrids;
				}
			}
		}
		return null;
	}
	
	
	//
	// COMPLETE THIS
	//
	// Returns true if this grid is legal. A grid is legal if no row, column, or zone contains
	// a repeated 1, 2, 3, 4, 5, 6, 7, 8, or 9.
	//
	public boolean isLegal()
	{
		//Check all rows
		for(int row = 0; row < 9; row++)							//Row iterator
		{
			int[] checkRepeats = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };		//Array of possible numbers
			for(int i = 0; i < 9; i++)
			{
				if(values[row][i] == 0) continue;					//Do not do anything if value is 0
				
				if(checkRepeats[values[row][i] - 1] == -1)			//If array spot at current number is -1 board is illegal
					return false;
				else
					checkRepeats[values[row][i] - 1] = -1;			//Change array spot of number to -1 if not -1
			}
		}
				
		//Check all columns
		for(int column = 0; column < 9; column++)					//Column iterator
		{
			int[] checkRepeats = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };		//Array of possible numbers
			for(int i = 0; i < 9; i++)
			{
				if(values[i][column] == 0) continue;				//Do not do anything if value is 0
				
				if(checkRepeats[values[i][column] - 1] == -1)		//If array spot at current number is -1 board is illegal
					return false;
				else
					checkRepeats[values[i][column] - 1] = -1;		//Change array spot of number to -1 if not -1
			}
		}
		
		//Get all zones into an ArrayList of ArrayList of Integer
		ArrayList<ArrayList<Integer>> zones = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < 9; i += 3)
			for(int j = 0; j < 9; j += 3)							//First 2 loops find all zones by 3x3
			{
				ArrayList<Integer> zone = new ArrayList<Integer>();	//Construct ArrayList for individual zones
				for(int k = 0; k < 3; k++)
				{
					for(int n = 0; n < 3; n++)
					{
						zone.add(values[j + k][i + n]);				//Adds 3x3 zones to zone ArrayList
					}
				}
				zones.add(zone);									//Adds individual zones to ArrayList of ArrayList of zones
			}
		
		for(ArrayList<Integer> zone : zones)						//Iterate through all individual zones
		{
			int[] checkRepeats = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };		//Array of possible numbers
			for(int i = 0; i < zone.size(); i++)
			{
				if(zone.get(i) == 0) continue;						//Do nothing if value is 0
					
				if(checkRepeats[zone.get(i) - 1] == -1)				//If array spot at current number is -1 board is illegal	
					return false;
				else
					checkRepeats[zone.get(i) - 1] = -1;				//Change array spot of number to -1 if not -1
			}
		}
		
		return true;
		
	}
	
	//
	// COMPLETE THIS
	//
	// Returns true if every cell member of values[][] is a digit from 1-9.
	//
	public boolean isFull()
	{
		for(int i = 0; i < 9; i++)
			for(int j = 0; j < 9; j++)
				if(values[i][j] == 0)			//Iterates through every value of values and if a spot is 0 the board is not full
					return false;
		
		return true;
	}
	
	//
	// COMPLETE THIS
	//
	// Returns true if x is a Grid and, for every (i,j), 
	// x.values[i][j] == this.values[i][j].
	//
	public boolean equals(Object x)
	{
		Grid that = (Grid)x;
		for(int i = 0; i < 9; i++)
			for(int j = 0; j < 9; j++)
				if(this.values[i][j] != that.values[i][j])	//Checks every individual spot in values and if they are not equal return false
					return false;
		
		return true;
	}
	
	public int[][] getValues()
	{
		return values;
	}
}