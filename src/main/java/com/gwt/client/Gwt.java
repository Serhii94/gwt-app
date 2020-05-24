package com.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
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
    private VerticalPanel inputFieldsWrapper = new VerticalPanel();
    private Label introScreenTitle = new Label("Intro screen");
    private Label label = new Label("How many numbers to display?");
    private TextBox inputField = new TextBox();
    private Label errorMessage = new Label("The input must be a number in the range from 1 to 1000");
    private Button enterButton = new Button("Enter");

    // Sorts Screen widgets
    private VerticalPanel sortsScreen = new VerticalPanel();
    private HorizontalPanel contentWrapper = new HorizontalPanel();
    private Label sortsScreenTitle = new Label("Sorts screen");
    private HorizontalPanel randomButtonsArea = new HorizontalPanel();
    private VerticalPanel controlButtonsArea = new VerticalPanel();
    private Button sortButton = new Button("Sort");
    private Button resetButton = new Button("Reset");

    // Pop up message box
    private DecoratedPopupPanel popUp = new DecoratedPopupPanel();
    private Label restrictMessage = new Label("Please select a value smaller or equal 30.");
    private Timer popUpTimer;

    // Sorting components configs
    private String sortOrder = "desc";
    private static final int SORTING_ITERATION_INTERVAL = 50;

    // Utility objects and containers
    private List<Integer> randomNumbers = new ArrayList<>();
    private List<Button> numberedButtons = new ArrayList<>();
    private List<SwapElement> swapElementsList = new ArrayList<>();
    private List<Timer> timersList = new ArrayList<>();

    public void onModuleLoad() {
        configIntroScreen();
        configSortsScreen();
        RootPanel.get().add(introScreen);
        RootPanel.get().add(sortsScreen);
    }

    private void configIntroScreen() {
        introScreen.setStyleName("introScreen");
        introScreen.add(introScreenTitle);
        introScreen.add(inputFieldsWrapper);

        introScreenTitle.setStyleName("screenTitle");

        inputFieldsWrapper.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        inputFieldsWrapper.setSpacing(10);
        inputFieldsWrapper.add(label);
        inputFieldsWrapper.add(inputField);
        inputFieldsWrapper.add(enterButton);
        inputFieldsWrapper.add(errorMessage);
        inputFieldsWrapper.setStyleName("contentWrapper");

        errorMessage.setVisible(false);
        
        enterButton.setStyleName("enterButton");
        enterButton.addClickHandler(new EnterButtonHandler());
    }

    private void configSortsScreen() {
        sortsScreen.addStyleName("sortsScreen");
        sortsScreen.add(sortsScreenTitle);
        sortsScreen.add(contentWrapper);

        sortsScreenTitle.setStyleName("screenTitle");

        contentWrapper.setStyleName("contentWrapper");
        contentWrapper.add(randomButtonsArea);
        contentWrapper.add(controlButtonsArea);

        sortsScreen.setVisible(false);

        randomButtonsArea.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        randomButtonsArea.setStyleName("randomNumbersButtonPanel");

        controlButtonsArea.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        controlButtonsArea.add(sortButton);
        sortButton.addClickHandler(new SortButtonHandler());
        controlButtonsArea.add(resetButton);
        resetButton.addClickHandler(new ResetButtonHandler());

        sortButton.setStyleName("sortButton");
        resetButton.addStyleName("resetButton");

        popUp.add(restrictMessage);
        popUp.setWidth("260px");
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
                numerateButtons();

                Timer timer = new Timer() {

                    @Override
                    public void run() {
                        displayButtonts();
                    }

                };

                registerAndSchedule(timer, 600, false);
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
            cleanAll();
        }

    }

    private class SortButtonHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            popUp.hide();
            swapElementsList.clear();
            quickSort(sortOrder);

            if (sortOrder.equals("asc")) {
                sortOrder = "desc";
            } else if (sortOrder.equals("desc")) {
                sortOrder = "asc";
            }

        }

    }

    private class RandomButtonHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            Button theButton = (Button) event.getSource();
            int buttonValue = Integer.parseInt(theButton.getText());
            if ((buttonValue != 0) && (buttonValue <= 30)) {
                cleanAll();
                generateRandomNumbers(buttonValue);
                numerateButtons();
                displayButtonts();
            } else {
                // hide popUp if it is already shown
                if ((popUpTimer != null) && (popUpTimer.isRunning())) {
                    popUpTimer.cancel();
                    popUp.hide();
                }
                // show popUp in new coordinates
                int left = theButton.getAbsoluteLeft() + Window.getScrollLeft() + 10;
                int top = theButton.getAbsoluteTop() + 10;
                popUp.setPopupPosition(left, top);
                popUp.show();

                popUpTimer = new Timer() {

                    @Override
                    public void run() {
                        popUp.hide();
                    }

                };

                registerAndSchedule(popUpTimer, 900, false);
            }

        }

    }

    private void quickSort(String order) {
        if (order.equals("asc")) {
            sortAsc(0, randomNumbers.size() - 1);
        }
        if (order.equals("desc")) {
            sortDesc(0, randomNumbers.size() - 1);
        }
        SwapElementsPrinter sep = new SwapElementsPrinter();
        registerAndSchedule(sep, SORTING_ITERATION_INTERVAL, true);
    }

    private void sortAsc(int low, int high) {
        int i = low;
        int j = high;
        // Get the pivot element from the middle of the list
        int pivot = randomNumbers.get(low + (high - low) / 2);
        // Divide into two lists
        while (i <= j) {
            /*
             * If the current value from the left list is smaller than the pivot element
             * then get the next element from the left list
             */
            while (randomNumbers.get(i) < pivot) {
                i++;
            }
            /*
             * If the current value from the right list is larger than the pivot element
             * then get the next element from the right list
             */
            while (randomNumbers.get(j) > pivot) {
                j--;
            }
            /*
             * If we have found a value in the left list which is larger than the pivot
             * element and if we have found a value in the right list which is smaller than
             * the pivot element then we swap the values. As we are done we can increase i
             * and j
             */
            if (i <= j) {
                swap(i, j);
                swapElementsList.add(new SwapElement(i, j));
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j) {
            sortAsc(low, j);
        }
        if (i < high) {
            sortAsc(i, high);
        }
    }

    private void sortDesc(int low, int high) {
        int i = low;
        int j = high;
        // Get the pivot element from the middle of the list
        int pivot = randomNumbers.get(low + (high - low) / 2);
        // Divide into two lists
        while (i <= j) {
            while (randomNumbers.get(i) > pivot) {
                i++;
            }
            while (randomNumbers.get(j) < pivot) {
                j--;
            }
            if (i <= j) {
                swap(i, j);
                swapElementsList.add(new SwapElement(i, j));
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j) {
            sortDesc(low, j);
        }
        if (i < high) {
            sortDesc(i, high);
        }
    }

    private void swap(int i, int j) {
        int temp = randomNumbers.get(i);
        randomNumbers.set(i, randomNumbers.get(j));
        randomNumbers.set(j, temp);
    }

    private void swapButtonsText(int i, int j) {
        String temp = numberedButtons.get(i).getText();
        numberedButtons.get(i).setText(numberedButtons.get(j).getText());
        numberedButtons.get(j).setText(temp);
    }

    private void generateRandomNumbers(int numbersAmount) {
        boolean hasNumber = false;
        int randomNumber;
        for (int i = 0; i < numbersAmount; i++) {
            randomNumber = com.google.gwt.user.client.Random.nextInt(1000);
            randomNumbers.add(randomNumber);
            // Check whether there is at list one number that is <= 30 in the list
            if ((randomNumber <= 30) && (randomNumber != 0)) {
                hasNumber = true;
            }
        }

        if (!hasNumber) {
            // In case there isn't such number in list - generate and set it
            int position = com.google.gwt.user.client.Random.nextInt(numbersAmount);
            randomNumber = com.google.gwt.user.client.Random.nextInt(31);
            if (randomNumber == 0)
                randomNumber = 1;
            randomNumbers.set(position, randomNumber);
        }

    }

    private void numerateButtons() {
        for (int i = 0; i < randomNumbers.size(); i++) {
            Button button = new Button(Integer.toString(randomNumbers.get(i)));
            button.setStyleName("randomNumberButton");
            button.addClickHandler(new RandomButtonHandler());
            numberedButtons.add(button);
        }
    }
    
    private void displayButtonts() {
        VerticalPanel vPanel = null;
        for (int i = 0; i < numberedButtons.size(); i++) {
            // create new column
            if ((i % 10) == 0) {
                vPanel = new VerticalPanel();
                vPanel.setStyleName("column");
                randomButtonsArea.add(vPanel);
            }
            // put button in column
            vPanel.add(numberedButtons.get(i));
        }
    }

    private void cleanAll() {
        sortOrder = "desc";
        inputField.setText("");
        resetTimers();
        popUp.hide();
        randomNumbers.clear();
        numberedButtons.clear();
        swapElementsList.clear();
        randomButtonsArea.clear();
    }

    private void resetTimers() {
        for (int i = 0; i < timersList.size(); i++) {
            timersList.get(i).cancel();
        }
        timersList.clear();
    }

    private void registerAndSchedule(Timer timer, int interval, boolean repeating) {
        timersList.add(timer);
        if (repeating)
            timer.scheduleRepeating(interval);
        else
            timer.schedule(interval);
    }

    private class SwapElement {
        private int i;
        private int j;

        SwapElement(int i, int j) {
            this.i = i;
            this.j = j;
        }

        int getI() {
            return this.i;
        }

        int getJ() {
            return this.j;
        }
    }

    private class SwapElementsPrinter extends Timer {
        int index = 0;

        @Override
        public void run() {
            SwapElement se = swapElementsList.get(index);
            swapButtonsText(se.getI(), se.getJ());
            index++;
            if (index >= swapElementsList.size()) {
                index = 0;
                this.cancel();
            }
        }

    }

}
