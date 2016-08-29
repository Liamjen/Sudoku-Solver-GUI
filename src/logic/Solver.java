package logic;

import java.util.*;

import application.GUI;


public class Solver 
{
	private Grid						problem;
	private ArrayList<Grid>				solutions;
	private int 						sleepTime = 10;
	private Evaluation 					eval;
	private Grid 						currentGrid;
	private boolean 					finished = false;
	
	public Solver(Grid problem, int sleepTime)
	{
		this.problem = problem;
		this.sleepTime = sleepTime;
	}
	
	
	public void solve()
	{
		solutions = new ArrayList<>();
		solveRecurse(problem);
	}
	
		
	// 
	// FINISH THIS.
	//
	// Standard backtracking recursive solver.
	//
	private void solveRecurse(Grid grid)
	{			
		
		Evaluation eval = evaluate(grid);
		this.eval = eval;
		
		currentGrid = grid;
		
		try
		{
			Thread.sleep(sleepTime);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		if (eval == Evaluation.ABANDON)
		{
			return;								//return will cut off current board and all children
		}
		else if (eval == Evaluation.ACCEPT)
		{
			solutions.add(grid);				//Add grid to solutions array if it is complete and correct
			finished = true;
		}
		else
		{
			for(Grid g : grid.next9Grids())		//If continue solve all next possible boards
				solveRecurse(g);
		}
		
	}
	
	//
	// COMPLETE THIS
	//
	// Returns Evaluation.ABANDON if the grid is illegal. 
	// Returns ACCEPT if the grid is legal and complete.
	// Returns CONTINUE if the grid is legal and incomplete.
	//
	public Evaluation evaluate(Grid grid)
	{
		boolean isLegal = grid.isLegal();				//Isolate boolean to avoid calling twice every evaluation
		
		if(!isLegal)
			return Evaluation.ABANDON;					//Abandon if illegal board
		else if(isLegal && !grid.isFull())
			return Evaluation.CONTINUE;					//Continue if board is legal and not full
		else
			return Evaluation.ACCEPT;					//If none of these conditions the board is complete and legal
		
	}
	
	public Evaluation getEval()
	{
		return eval;
	}
	
	public Grid getCurrentGrid()
	{
		return currentGrid;
	}
	
	public boolean getFinished()
	{
		return finished;
	}
	
	public ArrayList<Grid> getSolutions()
	{
		return solutions;
	}
}