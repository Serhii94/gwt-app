package com.gwt.client;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	// Intro Screen widgets
	private VerticalPanel introScreen = new VerticalPanel();
	private Label label = new Label("How many numbers to display?");
	private TextBox inputField = new TextBox();
	private Label errorMessage = new Label("The input must be a number in the range from 1 to 1000");
	private Button enterButton = new Button("Enter");
	
	// Sorts Screen widgets
	private HorizontalPanel sortsScreen = new HorizontalPanel();
	private HorizontalPanel leftSide = new HorizontalPanel();
	private VerticalPanel rightSide = new VerticalPanel();
	private Button sortButton = new Button("Sort");
	private Button resetButton = new Button("Reset");
	
	// -- --
	List<Integer> randomNumbers = new ArrayList<>();
	List<Button> numberedButtons = new ArrayList<>();
	ButtonsRender render = new ButtonsRender();
	
	// -- --
	private Random random = new Random();
	
	public void onModuleLoad() {
		configIntroScreen();
		configSortsScreen();
		
		RootPanel.get().add(introScreen);
		RootPanel.get().add(sortsScreen);
		
	}
	
	private void configIntroScreen() {
		introScreen.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
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
		sortButton.addClickHandler(new SortButtonHandler());
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
						numberButtons();
						
						render.scheduleRepeating(20);
						
					}
					
				};
				
				timer.schedule(1000);
				
			} else {
				errorMessage.setVisible(true);
			}
			
		}
		
		private boolean isValidInput(String input) {

			if (input.isEmpty()) {
				return false;
			} 
			
			if (input.length() > 4) {
				return false;
			}
			
			if (input.charAt(0) == '0') {
				return false;
			}
			
			// Check whether input string is a number
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
			
			render.cancel();
			
			numberedButtons.clear();
			randomNumbers.clear();
			leftSide.clear();
		}
		
	}
	
	private class SortButtonHandler implements ClickHandler {
		private String order = "asc";

		@Override
		public void onClick(ClickEvent event) {
			if (order.equals("asc")) {
				order = "desc";
				//quickSort(order);
				leftSide.clear();
				render.scheduleRepeating(20);
			}
			
			if (order.equals("desc")) {
				order = "asc";
				//quickSort(order);
				leftSide.clear();
				render.scheduleRepeating(20);
			}
		}
		
	}
	
	private void quickSort(String order) {
		if (order.equals("ask")) {
			//quickSort(a, 0, a.size() - 1);
		}
		
		if (order.equals("desk")) {
			
		}
	}
	
	private void quickSort(List<Integer> a, int low, int high) {
		int i = low, j = high;
        // Get the pivot element from the middle of the list
        int pivot = a.get(low + (high-low)/2);

        // Divide into two lists
        while (i <= j) {
            // If the current value from the left list is smaller than the pivot
            // element then get the next element from the left list
            while (a.get(i) < pivot) {
                i++;
            }
            // If the current value from the right list is larger than the pivot
            // element then get the next element from the right list
            while (a.get(j) > pivot) {
                j--;
            }

            // If we have found a value in the left list which is larger than
            // the pivot element and if we have found a value in the right list
            // which is smaller than the pivot element then we swap the
            // values.
            // As we are done we can increase i and j
            if (i <= j) {
                swap(a, i, j);
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j)
            quickSort(a, low, j);
        if (i < high)
            quickSort(a, i, high);
	}
	
	private void swap(List<Integer> a, int i, int j) {
        int temp = a.get(i);
        a.set(i, a.get(j));
        a.set(j, temp);
    }
	
	
	private void generateRundomNumbers(int amount) {
		for (int i = 0; i < amount; i++) {
			randomNumbers.add(random.nextInt(1000));
		}
	}
	
	private void numberButtons() {
		for (int i = 0; i < randomNumbers.size(); i++) {
			numberedButtons.add(new Button(randomNumbers.get(i).toString()));
		}
	}
	
	// displaying numbered buttons
	private class ButtonsRender extends Timer {
		private int i = 0;
		private VerticalPanel vPanel = null;

		@Override
		public void run() {
			
			// create new column
			if ((i % 10) == 0) {
				vPanel = new VerticalPanel();
				leftSide.add(vPanel);
			}
			
			// put button in column
			vPanel.add(numberedButtons.get(i));
			
			// pick next button
			i++;
			
			// Reached end of numberedButtons list
			if (i == numberedButtons.size()) {
				i = 0;
				vPanel = null;
				this.cancel();
			}
		}
		
	}
	
}
