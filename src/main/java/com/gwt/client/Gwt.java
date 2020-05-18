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
	Timer1 timer1 = new Timer1();
	
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
						
						timer1.scheduleRepeating(20);
						
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
			
			timer1.cancel();
			
			numberedButtons.clear();
			randomNumbers.clear();
			leftSide.clear();
		}
		
	}
	
	private class SortButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			int[] a = new int[randomNumbers.size()];
			for (int i = 0; i < randomNumbers.size(); i++) {
				a[i] = randomNumbers.get(i).intValue();
			}
			quickSort(a);
			for (int i = 0; i < randomNumbers.size(); i++) {
				randomNumbers.set(i,  Integer.valueOf(a[i]));
			}
			for (int i = 0; i < numberedButtons.size(); i++) {
				numberedButtons.get(i).setText(randomNumbers.get(i).toString());
			}
		}
		
	}
	
	private void quickSort(int[] a) {
		quickSort(a, 0, a.length - 1);
	}
	
	private void quickSort(int[] a, int low, int high) {
		if (low < high + 1) {
			int pivot = partition(a, low, high);
			quickSort(a, low, pivot - 1);
			quickSort(a, pivot + 1, high);
		}
	}
	
	private void swap(int[] a, int index1, int index2) {
		int temp = a[index1];
		a[index1] = a[index2];
		a[index2] = temp;
	}
	
	// returns random pivot index between low and high inclusive
	private int getPivot(int low, int high) {
		return com.google.gwt.user.client.Random.nextInt((high - low) + 1) + low;
	}

	private int partition(int[] a, int low, int high) {
		swap(a, low, getPivot(low, high));
		int border = low + 1;
		for (int i = border; i <= high; i++) {
			if (a[i] < a[low]) {
				swap(a, i, border++);
			}
		}
		swap(a, low, border - 1);
		return border - 1;
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
	private class Timer1 extends Timer {
		private int i = 0;
		private VerticalPanel vPanel = null;

		@Override
		public void run() {
			
			// Reached end of numberedButtons list
			if (i == numberedButtons.size()) {
				this.cancel();
			}
			
			// create new column
			if ((i % 10) == 0) {
				vPanel = new VerticalPanel();
				leftSide.add(vPanel);
			}
			
			// put button in column
			vPanel.add(numberedButtons.get(i));
			
			// pick next button
			i++;
		}
		
	}
	
}
