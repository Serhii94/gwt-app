package com.gwt.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class Gwt implements EntryPoint {
	
	// IntroScreen widgets
	private VerticalPanel introScreen = new VerticalPanel();
	private Label label = new Label("How many numbers to display?");
	private TextBox inputField = new TextBox();
	private Label errorMessage = new Label("The input must be a number in the range from 1 to 999");
	private Button enterButton = new Button("Enter");
	
	// SortsScreen widgets
	private HorizontalPanel sortsScreen = new HorizontalPanel();
	private HorizontalPanel leftSide = new HorizontalPanel();
	private VerticalPanel rightSide = new VerticalPanel();
	private Button sortButton = new Button("Sort");
	private Button resetButton = new Button("Reset");
	
	// -- --
	List<Integer> randomNumbers = new ArrayList<>();
	
	// -- --
	private Random random = new Random();
	
	public void onModuleLoad() {
		configIntroScreen();
		configSortsScreen();
		
		RootPanel.get().add(introScreen);
		RootPanel.get().add(sortsScreen);
		
	}
	
	private void configIntroScreen() {
		introScreen.add(label);
		introScreen.add(inputField);
		introScreen.add(enterButton);
		introScreen.add(errorMessage);
		errorMessage.setVisible(false);
		enterButton.addClickHandler(new EnterButtonHandler());
		
		introScreen.addStyleName("introScreen");
	}
	
	private void configSortsScreen() {
		sortsScreen.addStyleName("sortsScreen");
		sortsScreen.setVisible(false);
		sortsScreen.add(leftSide);
		sortsScreen.add(rightSide);
		
		leftSide.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		//leftSide.setWidth("700px");
		
		rightSide.setWidth("50px");
		rightSide.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		rightSide.add(sortButton);
		rightSide.add(resetButton);
		resetButton.addClickHandler(new ResetButtonHandler());
		
	}
	
	private class EnterButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String input = inputField.getText();
			int generateNumbers = 0;
			if (isValidInput(input)) {
				introScreen.setVisible(false);
				sortsScreen.setVisible(true);
				errorMessage.setVisible(false);
				
				Timer timer = new Timer() {

					@Override
					public void run() {
						int generateNumbers = Integer.valueOf(input);
						generateRundomNumbers(generateNumbers);
						displayNumbers(generateNumbers);
					}
					
				};
				timer.schedule(600);
				
				
			} else {
				errorMessage.setVisible(true);
			}
			
		}
		
		private boolean isValidInput(String input) {
			System.out.println();
			System.out.println(input);
			if (input.isEmpty()) {
				return false;
			} else if (input.length() > 4) {
				return false;
			} else if (input.charAt(0) == 0) {
				return false;
			}
			for (int i = 0; i < input.length(); i++) {
				if (!Character.isDigit(input.charAt(i))) {
					return false;
				}
			}
			if (Integer.valueOf(input) > 1000) {
				return false;
			}
			return true;
		}
		
		
	}
	
	private class ResetButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			sortsScreen.setVisible(false);
			introScreen.setVisible(true);
			
			randomNumbers.clear();
			leftSide.clear();
		}
		
	}
	
	private class SortButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			randomNumbers.sort(new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
				
			});
			displayNumbers(randomNumbers.size());
		}
		
	}
	
	private void generateRundomNumbers(int amount) {
		for (int i = 0; i < amount; i++) {
			randomNumbers.add(random.nextInt(1000));
		}
	}
	
	private void displayNumbers(int numbersAmount) {
		VerticalPanel vPanel = null;
		Button button = null;
		
		Timer timer = new Timer() {
			int i = 1;
			
			@Override
			public void run() {
				if (i < 10) {
					this.cancel();
					Button button = new Button("hi!");
					leftSide.add(button);
					i++;
				} else {
					this.cancel();
				}
				
			}
			
		};
		timer.scheduleRepeating(500);
		
//		// column size == 10 buttons
//		for (int i = 0; i < numbersAmount; i++) {
//			
//			// create new column and put this column in left panel
//			if ((i % 10) == 0) {
//				vPanel = new VerticalPanel();
//				leftSide.add(vPanel);
//			}
//			// fill column with buttons
//			button = new Button(randomNumbers.get(i).toString());
//			button.addStyleName("randomNumberButton");
//			vPanel.add(button);
					
		}
				
	
	
	
//	private void displayNumbers(int numbersAmount) {
//		VerticalPanel vPanel = null;
//		Button button = null;
//		
//		// column size == 10 buttons
//		for (int i = 0; i < numbersAmount; i++) {
//			// create new column and put this column in left panel
//			if ((i % 10) == 0) {
//				vPanel = new VerticalPanel();
//				leftSide.add(vPanel);
//			}
//			// fill column with buttons
//			button = new Button(randomNumbers.get(i).toString());
//			button.addStyleName("randomNumberButton");
//			vPanel.add(button);
//		}
//	}
	
	
	
}
