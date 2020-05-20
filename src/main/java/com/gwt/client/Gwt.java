package com.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Gwt implements EntryPoint {

	// Intro Screen widgets
	private VerticalPanel introScreen = new VerticalPanel();
	private VerticalPanel contentWrapper1 = new VerticalPanel();
	private Label introScreenTitle = new Label("Intro screen");
	private Label label = new Label("How many numbers to display?");
	private TextBox inputField = new TextBox();
	private Label errorMessage = new Label("The input must be a number in the range from 1 to 1000");
	private Button enterButton = new Button("Enter");

	// Sorts Screen widgets
	private VerticalPanel sortsScreen = new VerticalPanel();
	private HorizontalPanel contentWrapper2 = new HorizontalPanel();
	private Label sortsScreenTitle = new Label("Sorts screen");
	private HorizontalPanel leftSide = new HorizontalPanel();
	private VerticalPanel rightSide = new VerticalPanel();
	private Button sortButton = new Button("Sort");
	private Button resetButton = new Button("Reset");

	// Pop up message box
	private DecoratedPopupPanel popUp = new DecoratedPopupPanel();
	private Label restrictMessage = new Label("Please select a value smaller or equal 30.");

	// -- --
	private List<Integer> randomNumbers = new ArrayList<>();
	private List<Integer> randomNumbersCopy = new ArrayList<>();
	private List<Button> numberedButtons = new ArrayList<>();

	ButtonsRender render = new ButtonsRender();

	private static final int BUTTONS_DISPLAY_INTERVAL = 20;
	private static final int SORTING_ITERATION_DELAY = 300;

	List<Timer> timersList = new ArrayList<>();

	public void onModuleLoad() {
		configIntroScreen();
		configSortsScreen();
		RootPanel.get().add(introScreen);
		RootPanel.get().add(sortsScreen);

	}

	private void configIntroScreen() {
		introScreen.setStyleName("introScreen");
		introScreen.add(introScreenTitle);
		introScreen.add(contentWrapper1);

		introScreenTitle.setStyleName("screenTitle");

		contentWrapper1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contentWrapper1.setSpacing(10);
		contentWrapper1.add(label);
		contentWrapper1.add(inputField);
		contentWrapper1.add(enterButton);
		contentWrapper1.add(errorMessage);
		contentWrapper1.setStyleName("contentWrapper");

		errorMessage.setVisible(false);
		enterButton.addClickHandler(new EnterButtonHandler());

	}

	private void configSortsScreen() {

		sortsScreen.addStyleName("sortsScreen");
		sortsScreen.add(sortsScreenTitle);
		sortsScreen.add(contentWrapper2);

		sortsScreenTitle.setStyleName("screenTitle");

		contentWrapper2.setStyleName("contentWrapper");
		contentWrapper2.add(leftSide);
		contentWrapper2.add(rightSide);

		sortsScreen.setVisible(false);

		leftSide.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		leftSide.setStyleName("randomNumbersButtonPanel");

		rightSide.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		rightSide.add(sortButton);
		sortButton.addClickHandler(new SortButtonHandler());
		rightSide.add(resetButton);
		resetButton.addClickHandler(new ResetButtonHandler());

		sortButton.setStyleName("sortButton");
		resetButton.addStyleName("resetButton");

		popUp.add(restrictMessage);

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
				generateRandomNumbers(randomNumbersAmount);                        /* 2 */
				randomNumbersCopy.addAll(randomNumbers);
				numerateButtons();

				Timer timer = new Timer() {

					@Override
					public void run() {
						registerAndSchedule(render, BUTTONS_DISPLAY_INTERVAL, true);
					}

				};
				registerAndSchedule(timer, 800, false);

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

			inputField.setText("");

			resetTimers();
			numberedButtons.clear();
			randomNumbers.clear();                                          /* 1 */
			randomNumbersCopy.clear();
			leftSide.clear();
			popUp.hide();
		}

	}

	private class SortButtonHandler implements ClickHandler {
		private String order = "asc";

		@Override
		public void onClick(ClickEvent event) {
			popUp.hide();
			randomNumbersCopy.clear();
			leftSide.clear();                                               /* 1 */
			numberedButtons.clear();
			resetTimers();

			if (order.equals("asc")) {
				order = "desc";
			} else if (order.equals("desc")) {
				order = "asc";
			}

			Timer timer = new Timer() {

				@Override
				public void run() {
					quickSort(order);
				}

			};

			randomNumbersCopy.addAll(randomNumbers);                                         /* 2 */
			numerateButtons();

			registerAndSchedule(render, BUTTONS_DISPLAY_INTERVAL, true);
			/* timersList.add(render) - хз нада ця строка її тут не було */

			registerAndSchedule(timer, BUTTONS_DISPLAY_INTERVAL * randomNumbers.size() + 20, false);

		}

	}

	private class RandomButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			Button theButton = (Button) event.getSource();
			int buttonValue = Integer.parseInt(theButton.getText());
			if ((buttonValue != 0) && (buttonValue <= 30)) {

				// Clean all values in lists
				resetTimers();
				numberedButtons.clear();
				randomNumbers.clear();                                                   /* 1 */
				randomNumbersCopy.clear();
				leftSide.clear();
				popUp.hide();

				// Generate new values
				generateRandomNumbers(buttonValue);                                       /* 2 */
				randomNumbersCopy.addAll(randomNumbers);
				numerateButtons();

				registerAndSchedule(render, BUTTONS_DISPLAY_INTERVAL, true);

			} else {
				int left = theButton.getAbsoluteLeft() + 10;
				int top = theButton.getAbsoluteTop() + 10;
				popUp.setPopupPosition(left, top);
				popUp.show();

				Timer timer = new Timer() {

					@Override
					public void run() {
						popUp.hide();
					}

				};

				registerAndSchedule(timer, 1000, false);

			}

		}

	}

	private void resetTimers() {
		for (int i = 0; i < timersList.size(); i++) {
			timersList.get(i).cancel();
		}
		timersList.clear();
	}

	private void quickSort(String order) {
		if (order.equals("asc")) {
			ASCQuickSortTimer aqst = new ASCQuickSortTimer(0, randomNumbers.size() - 1);
			registerAndSchedule(aqst, SORTING_ITERATION_DELAY, false);
		}

		if (order.equals("desc")) {
			DESCQuickSortTimer dqst = new DESCQuickSortTimer(0, randomNumbers.size() - 1);
			registerAndSchedule(dqst, SORTING_ITERATION_DELAY, false);
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
				registerAndSchedule(qst1, SORTING_ITERATION_DELAY, false);
			}

			if (i < high) {
				ASCQuickSortTimer qst2 = new ASCQuickSortTimer(i, high);
				registerAndSchedule(qst2, SORTING_ITERATION_DELAY, false);
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
				registerAndSchedule(dqst1, SORTING_ITERATION_DELAY, false);
			}

			if (i < high) {
				DESCQuickSortTimer dqst2 = new DESCQuickSortTimer(i, high);
				registerAndSchedule(dqst2, SORTING_ITERATION_DELAY, false);
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
		boolean hasNumber = false;
		int randomNumber;
		for (int i = 0; i < amount; i++) {
			randomNumber = com.google.gwt.user.client.Random.nextInt(1000);
			randomNumbers.add(randomNumber);
			
			// Check whether there is at list one number that is <= 30 in the list
			if (randomNumber <= 30) {
				hasNumber = true;
			}
		}

		if (!hasNumber) {
			// In case there isn't such number in list - generate and set it
			randomNumber = com.google.gwt.user.client.Random.nextInt(30);
			int position = com.google.gwt.user.client.Random.nextInt(amount);
			randomNumbers.set(position, randomNumber);
		}

	}

	private void numerateButtons() {
		for (int i = 0; i < randomNumbersCopy.size(); i++) {
			Button button = new Button(Integer.toString(randomNumbersCopy.get(i)));
			button.setStyleName("randomNumberButton");
			button.addClickHandler(new RandomButtonHandler());
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
				vPanel.setStyleName("column");
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

	private void registerAndSchedule(Timer timer, int interval, boolean repeating) {
		timersList.add(timer);
		if (repeating)
			timer.scheduleRepeating(interval);
		else
			timer.schedule(interval);
	}

}
