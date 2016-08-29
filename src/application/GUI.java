package application;

import java.util.HashMap;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logic.Evaluation;
import logic.Grid;
import logic.Solver;
import logic.TestGridSupplier;

public class GUI extends Application
{
	private final int ROOT_WIDTH = 600;
	private final int ROOT_HEIGHT = 720;
	
	private final int DEFAULT_SPACING = 20;
	private final int SUDOKU_SIZE = 9;
	private int sleepTime = 10;
	
	private int[][] startingGrid;
	
	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setTitle("Sudoku Solver");
		
		//Default Grid g to default puzzle (TestGridSupplier Puzzle 1)
		Grid g = TestGridSupplier.getPuzzle1();
		
		//Initialize startingGrid[][] as default puzzle's values (TestGridSupplier Puzzle 1)
		startingGrid = TestGridSupplier.getPuzzle1().getValues();
		
		//Start root element for all elements vertically
		VBox root = new VBox();
		root.setPadding(new Insets(DEFAULT_SPACING));
		root.setSpacing(DEFAULT_SPACING);
		root.setAlignment(Pos.TOP_CENTER);
		//End root element
		
		//Start all buttons (Start solving and setting speed in ms respectively)
		Button btnStart = new Button("Start");
		Button btnSetSpeed = new Button("Set Speed");
		Button btnReset = new Button("Reset");
		//Reset button starts off disabled
		btnReset.setDisable(true);
		btnSetSpeed.setTooltip(new Tooltip("Set limit in ms on speed per calculation. Default: 10ms\nCircle indicates Evaluation enum"));
		//End all buttons
		
		//Start Text field (to input speed of calculations in ms)
		TextField txtSetSpeed = new TextField();
		txtSetSpeed.setPromptText("Mouse over button");
		//End Text field
		
		//Start Label for displaying current speed limit
		Label lblCurrentSpeed = new Label("The current speed limit is " + sleepTime + " ms");
		//End label for displaying current speed limit
		
		//Start crclStatus (Used for Evaluation)
		Circle crclStatus = new Circle(15, Color.BLACK);
		//End crclStatus
		
		//Start Choice Box for Grid selection
		ObservableList<String> options = 
			    FXCollections.observableArrayList(
			    		//Grids are TestGridSupplier 1, 2 and 3 respectively
			            "Grid 1",
			            "Grid 2",
			            "Grid 3"
			        );
		final ChoiceBox<String> chsbxGridSelection = new ChoiceBox<String>(options);
		//End Choice Box for Grid selection
		
		//Start HashMap to store Choice Box selection to Grid
		HashMap<String, Grid> choiceToGrid = new HashMap<>();
		choiceToGrid.put("Grid 1", TestGridSupplier.getPuzzle1());
		choiceToGrid.put("Grid 2", TestGridSupplier.getPuzzle2());
		choiceToGrid.put("Grid 3", TestGridSupplier.getPuzzle3());
		//End HashMap for choice to Grid
		
		//Default 
		chsbxGridSelection.setValue("Grid 1");
		chsbxGridSelection.setOnAction(e ->
		{
			//Set g (starting grid) to TestGridSupplier Puzzle 1, 2, and 3 respectively
			
			//Get Grid from HashMap of String to Grid
			g.setValues(choiceToGrid.get(chsbxGridSelection.getSelectionModel().getSelectedItem()).getValues());
			
			//Set our starting Grid to whichever Grid is selected
			startingGrid = choiceToGrid.get(chsbxGridSelection.getSelectionModel().getSelectedItem()).getValues();
			
			//Redraw the Sudoku Grid VBox after grid is changed
			//Index 2 of root (order at bottom) is the sudokuGrid
			root.getChildren().set(3, sudokuGridToVBox(g.getValues()));
			
		});
		//End Combo Box for grid selection
		
		//Start HBox for Combo Box (Puzzle Choices) and Start Button
		HBox comboAndStart = new HBox();
		comboAndStart.getChildren().addAll(btnStart, chsbxGridSelection);
		comboAndStart.setAlignment(Pos.CENTER);
		comboAndStart.setSpacing(DEFAULT_SPACING);
		//End HBox for Combo Box (Puzzle Choices) and Start Button
		
		//Start HBox (text field and setspeed button
		HBox setSpeedBox = new HBox();
		setSpeedBox.getChildren().addAll(txtSetSpeed, btnSetSpeed);
		setSpeedBox.setAlignment(Pos.CENTER);
		setSpeedBox.setSpacing(DEFAULT_SPACING);
		//End HBox speed
		
		//Upon closing program, shut down all threads TODO: Is this the correct way to close threads?
		primaryStage.setOnCloseRequest(e -> 
		{
			for(Thread t : Thread.getAllStackTraces().keySet())
				t.interrupt();
			Platform.exit();
		});
		
		//Start btnStart commands
		btnStart.setOnAction(e ->
		{
			//Disable all accessible controls once solving is started
			btnStart.setDisable(true);
			btnSetSpeed.setDisable(true);
			chsbxGridSelection.setDisable(true);
			txtSetSpeed.setDisable(true);
			
			//Solver solves beginning Sudoku Grid with a wait of sleepTime (in ms)
			Solver solver = new Solver(g, sleepTime);
			
			//Construct new thread for solving sudoku
			Thread t = new Thread(() ->
			{
				solver.solve();
			});
			
			//Sudoku Solver solves on a thread separate from JavaFX thread (allows updating GUI)
			t.start();
			
			//AnimationTimer incrementing 60 x a second, refreshing GUI as solver solves Sudoku on Thread t
			new AnimationTimer()
			{
				@Override
				public void handle(long now)
				{
					
					//As long as solver is not done, update GUI with current Grid and Evaluation (as colored circle)
					if(!solver.getFinished())
					{
						if(solver.getEval() == Evaluation.ABANDON)
							crclStatus.setFill(Color.RED);
						else
							crclStatus.setFill(Color.BLUE);
						
						root.getChildren().set(3, sudokuGridToVBox(solver.getCurrentGrid().getValues()));
						root.getChildren().set(4, crclStatus);
					}
					//If solver is finished, set Grid to solution and set Evaluation circle to Green if solution exists
					else
					{
						//Case where solver found a solution
						if(solver.getSolutions().size() > 0)
						{
							crclStatus.setFill(Color.GREEN);
							root.getChildren().set(3, sudokuGridToVBox(solver.getSolutions().get(0).getValues()));
							root.getChildren().set(4, crclStatus);
							
							//Once finished, reset button is enabled
							btnReset.setDisable(false);
							
							//End AnimationTimer after solver is finished
							stop();
						}
						//If no solution, Circle remains RED and viewable Grid is last Grid used
						else
						{
							crclStatus.setFill(Color.RED);
							root.getChildren().set(3, sudokuGridToVBox(solver.getCurrentGrid().getValues()));
							root.getChildren().set(4, crclStatus);
							
							//Once finished, reset button is enabled
							btnReset.setDisable(false);
							
							//End AnimationTimer after program is finished
							stop();
						}
					}
					
				}
			}.start();
			
			
		});
		//End btnStart commands
		
		//Start btnSetSpeed commands
		btnSetSpeed.setOnAction(e ->
		{
			sleepTime = Integer.parseInt(txtSetSpeed.getText()) > 0 ? Integer.parseInt(txtSetSpeed.getText()) : 0;
			txtSetSpeed.setText(Integer.parseInt(txtSetSpeed.getText()) > 0 ? txtSetSpeed.getText() : "0");
			lblCurrentSpeed.setText("The current speed limit is " + sleepTime + " ms");
		});
		//End btnSetSpeed commands
		
		btnReset.setOnAction(e ->
		{
			//Enable all accessible controls at restart
			btnStart.setDisable(false);
			btnSetSpeed.setDisable(false);
			chsbxGridSelection.setDisable(false);
			txtSetSpeed.setDisable(false);
			
			//Upon resetting, reset button is disabled
			btnReset.setDisable(true);
			
			//Set the grid back to starting Grid values[][]
			g.setValues(startingGrid);
			
			//Set the sudoku grid VBox back to original value
			root.getChildren().set(3, sudokuGridToVBox(startingGrid));
			
			//Set circle back to default color
			crclStatus.setFill(Color.BLACK);
			
		});
		
		//Default root configuration upon opening program
		root.getChildren().addAll(comboAndStart, setSpeedBox, lblCurrentSpeed, sudokuGridToVBox(g.getValues()), crclStatus, btnReset);
		
		Scene scene = new Scene(root, ROOT_WIDTH, ROOT_HEIGHT);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public VBox sudokuGridToVBox(int[][] values)
	{
		//Root VBox containing the viewable Sudoku Grid
		VBox sudokuGrid = new VBox();
		
		for(int i = 0; i < SUDOKU_SIZE; i++)
		{
			//SUDOKU_SIZE number of HBoxes containing individual squares with number / empty space
			HBox row = new HBox();
			for(int j = 0; j < SUDOKU_SIZE; j++)
			{
				//Stack Pane to have Squares under numbers e.g. [2]
				StackPane stack = new StackPane();
				Rectangle r = new Rectangle(50, 50);
				if(startingGrid[i][j] != 0)
					r.setFill(Color.WHITE);
				else
					r.setFill(Color.BEIGE);
				r.setStroke(Color.GRAY);
				//Turn all 0s from values[][] to an empty square, or the number if not 0.
				if((values[i][j]) != 0)
				{
					Text t = new Text(Integer.toString(values[i][j]));
					t.setFont(Font.font(20));
					t.setFill(Color.BLACK);
					stack.getChildren().addAll(r, t);
				}
				else
					stack.getChildren().addAll(r);
				
				//Add all SUDUKU_SIZE # of squares to each row
				row.getChildren().add(stack);
			}
			row.setAlignment(Pos.CENTER);
			//Add all rows to root VBox individually
			sudokuGrid.getChildren().add(row);
		}
		sudokuGrid.setAlignment(Pos.CENTER);
		return sudokuGrid;
	}
	
	public static void main(String[] args)
	{	
		launch(args);
	}
}
