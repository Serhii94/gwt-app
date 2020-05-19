package com.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
	private DialogBox dialogBox = new DialogBox();

	// -- --
	private List<Integer> randomNumbers = new ArrayList<>();
	private List<Integer> randomNumbersCopy = new ArrayList<>();
	private List<Button> numberedButtons = new ArrayList<>();
	
	ButtonsRender render = new ButtonsRender();

	private static final int BUTTONS_DISPLAY_INTERVAL = 20;
	
	List<Timer> timersList = new ArrayList<>();

	public void onModuleLoad() {
		configIntroScreen();
		configSortsScreen();

		RootPanel.get().add(introScreen);
		RootPanel.get().add(sortsScreen);
		RootPanel.get().add(dialogBox);

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
		// leftSide.setWidth("700px");

		rightSide.setWidth("50px");
		rightSide.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		rightSide.add(sortButton);
		sortButton.addClickHandler(new SortButtonHandler());
		rightSide.add(resetButton);
		resetButton.addClickHandler(new ResetButtonHandler());
		
		Label label = new Label("Please select a value smaller or equal 30.");
		dialogBox.add(label);
		dialogBox.setVisible(false);

	}

	private class EnterButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String input = inputField.getText();
			if (isValidInput(input)) {
				introScreen.setVisible(false);
				sortsScreen.setVisible(true);
				errorMessage.setVisible(false);
				
				int randomNumbersAmount = Integer.valueOf(input);
				generateRandomNumbers(randomNumbersAmount);

				Timer timer = new Timer() {

					@Override
					public void run() {
						render.scheduleRepeating(BUTTONS_DISPLAY_INTERVAL);
					}

				};
				timersList.add(timer);
				timersList.add(render);

				timer.schedule(800);

			} else {
				errorMessage.setVisible(true);
			}

		}

		private boolean isValidInput(String input) {

			if (input.isEmpty() || (input.length() > 4) || (input.charAt(0) == '0')) {
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

			for (int i = 0; i < timersList.size(); i++) {
				timersList.get(i).cancel();
			}
			timersList.clear();
			inputField.setText("");

			numberedButtons.clear();
			randomNumbers.clear();
			randomNumbersCopy.clear();
			leftSide.clear();
		}

	}

	private class SortButtonHandler implements ClickHandler {
		private String order = "asc";

		@Override
		public void onClick(ClickEvent event) {
			randomNumbersCopy.clear();
			leftSide.clear();
			randomNumbersCopy.addAll(randomNumbers);
			
			if (order.equals("asc")) {
				order = "desc";
			} else if (order.equals("desc")) {
				order = "asc";
			}
			numberedButtons.clear();
			for (int i = 0; i < randomNumbersCopy.size(); i++) {
				numberedButtons.add(new Button(randomNumbers.get(i).toString()));
			}
			render.scheduleRepeating(BUTTONS_DISPLAY_INTERVAL);

			Timer timer = new Timer() {

				@Override
				public void run() {
					quickSort(order);
				}

			};
			
			timersList.add(timer);
			timer.schedule(BUTTONS_DISPLAY_INTERVAL * randomNumbers.size() + 20);
		}

	}
	
	private class RundomButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			
			Button theButton = (Button) event.getSource();
			theButton.getText();
			dialogBox.setVisible(true);
			
		}
		
	}

	private void quickSort(String order) {
		if (order.equals("asc")) {
			ASCQuickSortTimer aqst = new ASCQuickSortTimer(0, randomNumbers.size() - 1);
			aqst.schedule(300);
			timersList.add(aqst);
		}

		if (order.equals("desc")) {
			DESCQuickSortTimer dqst = new DESCQuickSortTimer(0, randomNumbers.size() - 1);
			dqst.schedule(300);
			timersList.add(dqst);
		}

	}

	private class ASCQuickSortTimer extends Timer {
		private int low;
		private int high;

		public ASCQuickSortTimer(int low, int high) {
			this.low = low;
			this.high = high;
		}

		@Override
		public void run() {
			int i = low, j = high;
			// Get the pivot element from the middle of the list
			int pivot = randomNumbersCopy.get(low + (high - low) / 2);

			// Divide into two lists
			while (i <= j) {
				// If the current value from the left list is smaller than the pivot
				// element then get the next element from the left list
				while (randomNumbersCopy.get(i) < pivot) {
					i++;
				}
				// If the current value from the right list is larger than the pivot
				// element then get the next element from the right list
				while (randomNumbersCopy.get(j) > pivot) {
					j--;
				}

				// If we have found a value in the left list which is larger than
				// the pivot element and if we have found a value in the right list
				// which is smaller than the pivot element then we swap the
				// values.
				// As we are done we can increase i and j
				if (i <= j) {
					swap(i, j);
					swapButtonText(i, j);
					i++;
					j--;
				}
			}
			// Recursion
			if (low < j) {
				ASCQuickSortTimer qst1 = new ASCQuickSortTimer(low, j);
				qst1.schedule(1000);
				timersList.add(qst1);
			}

			if (i < high) {
				ASCQuickSortTimer qst2 = new ASCQuickSortTimer(i, high);
				qst2.schedule(1000);
				timersList.add(qst2);
			}

		}

	}

	private class DESCQuickSortTimer extends Timer {
		private int low;
		private int high;

		public DESCQuickSortTimer(int low, int high) {
			this.low = low;
			this.high = high;
		}

		@Override
		public void run() {
			int i = low, j = high;
			// Get the pivot element from the middle of the list
			int pivot = randomNumbersCopy.get(low + (high - low) / 2);

			// Divide into two lists
			while (i <= j) {
				while (randomNumbersCopy.get(i) > pivot) {
					i++;
				}

				while (randomNumbersCopy.get(j) < pivot) {
					j--;
				}

				if (i <= j) {
					swap(i, j);
					swapButtonText(i, j);
					i++;
					j--;
				}
			}
			// Recursion
			if (low < j) {
				DESCQuickSortTimer dqst1 = new DESCQuickSortTimer(low, j);
				dqst1.schedule(1000);
			}

			if (i < high) {
				DESCQuickSortTimer dqst2 = new DESCQuickSortTimer(i, high);
				dqst2.schedule(1000);
			}

		}

	}

	private void swap(int i, int j) {
		int temp = randomNumbersCopy.get(i);
		randomNumbersCopy.set(i, randomNumbersCopy.get(j));
		randomNumbersCopy.set(j, temp);
	}

	private void swapButtonText(int i, int j) {
		String temp = numberedButtons.get(i).getText();
		numberedButtons.get(i).setText(numberedButtons.get(j).getText());
		numberedButtons.get(j).setText(temp);
	}

	private void generateRandomNumbers(int amount) {
		int randomNumber;
		for (int i = 0; i < amount; i++) {
			randomNumber = com.google.gwt.user.client.Random.nextInt(1000);
			randomNumbers.add(randomNumber);
			randomNumbersCopy.add(randomNumber);
			Button button = new Button(Integer.toString(randomNumber));
			button.addClickHandler(new RundomButtonHandler());
			numberedButtons.add(button);
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
