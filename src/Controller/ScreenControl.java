package Controller;

import Database.UserDB;
import Models.GameApplication;
import Models.Letters;
import Models.Money;
import Models.User;
import Views.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ScreenControl implements ActionListener {
    JFrame frame = new JFrame();
    JPanel panelCont = new JPanel();

    CardLayout cl = new CardLayout();
    MainMenu mainMenu;
    PlayerMenu playerMenu;
    NewPlayerScreen newPlayerScreen;
    ReturnPlayerScreen returnPlayerScreen;
    QuestionPanel questionPanel;
    int height = 450;
    int width = 650;
    GameApplication currentGame;
    ConfirmScreen confirmScreen;
    CorrectAnswerPanel correctAnswerPanel;
    InCorrectAnswerPanel inCorrectAnswerPanel;
    EndGamePanel endGamePanel;
    InstructionPanel instructionPanel;
    CreditPanel creditPanel;
    LeaderBoard leaderBoardPanel;
    private final int LEVELS_TO_WIN = Money.LEVEL15.getPrizeLevel()+1;

    public ScreenControl() {
        // Make UI look nice
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Make program follow system's theme.
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }


        frame.setSize(width, height);

        panelCont.setLayout(cl);

        // Create each panel
        mainMenu = new MainMenu(this);
        playerMenu = new PlayerMenu(this);
        newPlayerScreen = new NewPlayerScreen(this);
        returnPlayerScreen = new ReturnPlayerScreen(this);
       // questionPanel = new QuestionPanel(currentGame, this);

        // Add each panel to main JPanel
        panelCont.add(mainMenu, mainMenu.NAME);
        panelCont.add(playerMenu, playerMenu.NAME);
        panelCont.add(newPlayerScreen, newPlayerScreen.NAME);
        panelCont.add(returnPlayerScreen, returnPlayerScreen.NAME);
       // panelCont.add(questionPanel, questionPanel.NAME);

        changeCard(mainMenu.NAME);

        frame.add(panelCont);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public void cardSwitch(JPanel newPanel, String name, JPanel oldPanel ){
        addCard(newPanel, name);
        changeCard(name);
        removeCard(oldPanel);
    }


    public void changeCard(String newCard){
        cl.show(panelCont, newCard);
    }

    public void removeCard(JPanel panel){
        if (!panel.equals(mainMenu)){ // mainMenu panel should never be removed
            panelCont.remove(panel);
        }
    }

    public void addCard(JPanel panel, String name){
        panelCont.add(panel, name);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        // Cast our event source into a Component, grab the components parent JPanel
        Component source = ((Component) e.getSource()).getParent();

        // Route the event to the correct handler
        if (source instanceof ReturnPlayerScreen || source instanceof NewPlayerScreen) {
            playerSelectionHandler(e);
        }
        else if (source instanceof QuestionPanel) {
            questionEventHandler(e);
        }
        else if (source instanceof PlayerMenu) {
            playerMenuHandler(e);
        }
         else if (source instanceof ConfirmScreen) {
            confirmScreenHandler(e);
        }
        else if (source instanceof MainMenu) {
            enterGameHandler(e);
        }
        else if (source instanceof InCorrectAnswerPanel) {
            inCorrectAnswerPanelHandler(e);
        }
        else if (source instanceof CorrectAnswerPanel){
            correctAnswerPanelHandler(e);
        }
    }

    public void correctAnswerPanelHandler(ActionEvent e){
        JPanel source = (JPanel) ((Component) e.getSource()).getParent();

        if (e.getSource() == correctAnswerPanel.getContinueButton() && !currentGame.isGameWon()){
            questionPanel = new QuestionPanel(currentGame,this);
            cardSwitch(questionPanel, questionPanel.NAME, source);
//            addCard(questionPanel, questionPanel.NAME);
//            changeCard(questionPanel.NAME);
//            removeCard(source);
        }

        if (e.getSource() == correctAnswerPanel.getExitButton()
                || (e.getSource() == correctAnswerPanel.getContinueButton()
                && currentGame.isGameWon())){
            endGame(source);
        }
    }

    public void inCorrectAnswerPanelHandler(ActionEvent e){
        JPanel source = (JPanel) ((Component) e.getSource()).getParent();

        if (e.getSource() == inCorrectAnswerPanel.getContinueButton()){
            endGame(source);
        }
    }

    public void endGame(JPanel source){
        endGamePanel = new EndGamePanel(currentGame,this);
        cardSwitch(endGamePanel, endGamePanel.NAME, source);
//        addCard(endGamePanel, endGamePanel.NAME);
//        changeCard(endGamePanel.NAME);
//        removeCard(source);

        endGamePanel.getContinueButton().addActionListener(e -> {
            changeCard(mainMenu.NAME);
            removeCard(endGamePanel);

            currentGame = null;
            System.gc(); //garbage collect old game
        });
    }



    public void enterGameHandler(ActionEvent e){
        if (e.getSource() == mainMenu.getEnterButton()){
            playerMenu = new PlayerMenu(this);
            addCard(playerMenu, playerMenu.NAME);
            changeCard(playerMenu.NAME);

            startGame();
        }
        else if (e.getSource() == mainMenu.getInstructionButton()){
            instructionPanel = new InstructionPanel();
            addCard(instructionPanel, instructionPanel.NAME);
            changeCard(instructionPanel.NAME);

            instructionPanel.getBackButton().addActionListener(e1 -> {
                changeCard(mainMenu.NAME);
                removeCard(instructionPanel);
            });

        }
        else if (e.getSource() == mainMenu.getCreditButton()){
            creditPanel = new CreditPanel();
            addCard(creditPanel, creditPanel.NAME);
            changeCard(creditPanel.NAME);

            creditPanel.getBackButton().addActionListener(e12 -> {
                changeCard(mainMenu.NAME);
                removeCard(creditPanel);
            });

        }
        else if (e.getSource() == mainMenu.getHighScoreButton()){
            leaderBoardPanel = new LeaderBoard(this);
            addCard(leaderBoardPanel, leaderBoardPanel.NAME);
            changeCard(leaderBoardPanel.NAME);

            leaderBoardPanel.getBackButton().addActionListener(e13 -> {
                changeCard(mainMenu.NAME);
                removeCard(leaderBoardPanel);
            });

        }
    }

    public void playerMenuHandler(ActionEvent e){
        if (e.getSource() == playerMenu.getNewPlayerButton()){
            newPlayerScreen = new NewPlayerScreen(this);
            cardSwitch(newPlayerScreen, newPlayerScreen.NAME, playerMenu);
//            addCard(newPlayerScreen, newPlayerScreen.NAME);
//            changeCard(newPlayerScreen.NAME);
//            removeCard(playerMenu);
        }

        if (e.getSource() == playerMenu.getReturnPlayerButton()){
            returnPlayerScreen = new ReturnPlayerScreen(this);
            cardSwitch(returnPlayerScreen, returnPlayerScreen.NAME, playerMenu);
//            addCard(returnPlayerScreen, returnPlayerScreen.NAME);
//            changeCard(returnPlayerScreen.NAME);
//            removeCard(playerMenu);
        }

        if (e.getSource() == playerMenu.getExitButton()){
            changeCard(mainMenu.NAME);
            removeCard(playerMenu);
        }
    }



    public void confirmScreenHandler(ActionEvent e){
        if (e.getSource() == confirmScreen.getYesButton()){
            removeCard(questionPanel);

            String string = confirmScreen.getCurrentLifeLine();

            if (string.equals("exit")){
                JPanel source = (JPanel) ((Component) e.getSource()).getParent();
                endGame(source);
            }
            else {
                switch (string) {
                    case "Fifty Fifty":
                        currentGame.useFiftyFiftyLifeLine();
                        break;
                    case "Ask The Audience":
                        StringBuilder audienceDecision = currentGame.useAskAudience();
                        if (!currentGame.isAskTheAudience()){
                            JOptionPane.showMessageDialog(null, audienceDecision.toString(), "AUDIENCE",
                                    JOptionPane.QUESTION_MESSAGE);
                        }
                        //currentGame.setAskTheAudience(false);
                        break;
                    case "Phone A Friend":
                        StringBuilder friendString = currentGame.usePhoneAFriend();

                        if (!currentGame.isHasFiftyFifty()){
                            JOptionPane.showMessageDialog(null, friendString.toString(), "PHONE A FRIEND",
                                    JOptionPane.QUESTION_MESSAGE);
                        }

                        //currentGame.setPhoneAFriend(false);
                        break;
                }

                questionPanel = new QuestionPanel(currentGame, this);
                cardSwitch(questionPanel, questionPanel.NAME, confirmScreen);
//                addCard(questionPanel, questionPanel.NAME);
//                changeCard(questionPanel.NAME);
//                removeCard(confirmScreen);
            }

        }
        else {
            changeCard(questionPanel.NAME);
        }
    }


    public void playerSelectionHandler(ActionEvent e){
        removeCard(playerMenu);
        boolean playerCreated = false;
        JPanel source = (JPanel) ((Component) e.getSource()).getParent();

        // USER Database
        UserDB userDB = new UserDB();
        // USER OBJECT
        User gameUser = new User();

        if (e.getSource() == returnPlayerScreen.getSubmitButton()) {

            // Grab the text the user has entered
            String text = returnPlayerScreen.userNameInput.getText();
            // Retrieve the user's details
            gameUser = userDB.retrieveUser(text);

            // If the user's details can't be retrieved
            // Show an error and prompt them to try again
            if (gameUser == null){
                JOptionPane.showMessageDialog(null, "Your name couldn't be found" +
                                " please try another user name or create a new player.", "INFO",
                        JOptionPane.ERROR_MESSAGE);
            }
            else { // Setup the user's game
                currentGame.setGameUser(gameUser);
                playerCreated = true;
            }
        }

        //TODO will allow entry on nothing in text box
        if (e.getSource() == newPlayerScreen.getSubmitButton()){

            // Grab the text the user has entered
            String text = newPlayerScreen.userNameInput.getText();

            // Check if the username is available
            // Otherwise show an error
            if (!(userDB.checkUsernameAvailability(text))) {
                JOptionPane.showMessageDialog(null, "Sorry that UserName is already being used," +
                                " or you have entered a invalid UserName format with a space or whitespace. Please try again.", "INFO",
                        JOptionPane.ERROR_MESSAGE);
            }
           else if ((userDB.checkUsernameAvailability(text))) {
               // Make sure that gameUser isn't null
                assert gameUser != null;
                // Set username
                gameUser.setUserName(text);
                // Add to the database
                userDB.addUserToDatabase(gameUser);
                // Set the current gameUser
                currentGame.setGameUser(gameUser);
                // Update boolean
                playerCreated = true;
            }
        }

        //go back to player selection
        if (e.getSource() == newPlayerScreen.getBackButton() || e.getSource() == returnPlayerScreen.getBackButton()){
            playerMenu = new PlayerMenu(this);
            cardSwitch(playerMenu, playerMenu.NAME, source);
//            addCard(playerMenu, playerMenu.NAME);
//            changeCard(playerMenu.NAME);
//            removeCard(source);
        }

        //exit to mainMenu
        if (e.getSource() == newPlayerScreen.getExitButton() || e.getSource() == returnPlayerScreen.getExitButton()){
            changeCard(mainMenu.NAME);
            removeCard(source);
        }

        if (playerCreated){
            questionPanel = new QuestionPanel(currentGame, this);
            cardSwitch(questionPanel, questionPanel.NAME, playerMenu);
//            addCard(questionPanel, questionPanel.NAME);
//            changeCard(questionPanel.NAME);
//            removeCard(playerMenu);
            removeCard(source);
        }
    }

    public void questionEventHandler(ActionEvent e){
        boolean checkGameStatus = false;

        if (e.getSource() == questionPanel.getFiftyFifty() && currentGame.isHasFiftyFifty()){
            confirmScreen = new ConfirmScreen(currentGame.getFiftyFiftyString(), this);
//            addCard(confirmScreen, confirmScreen.NAME);
//           // panelCont.add(confirmScreen, confirmScreen.NAME);
//            changeCard(confirmScreen.NAME);
        }
        else if (e.getSource() == questionPanel.getPhoneFriend() && currentGame.isPhoneAFriend()){
            confirmScreen = new ConfirmScreen(currentGame.getPhoneAFriend(), this);
//            addCard(confirmScreen, confirmScreen.NAME);
//            //panelCont.add(confirmScreen, confirmScreen.NAME);
//            changeCard(confirmScreen.NAME);
        }
        else if (e.getSource() == questionPanel.getAskAudience() && currentGame.isAskTheAudience()){
            confirmScreen = new ConfirmScreen(currentGame.getAskTheAudience(), this);
//            addCard(confirmScreen, confirmScreen.NAME);
//            //panelCont.add(confirmScreen, confirmScreen.NAME);
//            changeCard(confirmScreen.NAME);
        }
        addCard(confirmScreen, confirmScreen.NAME);
        changeCard(confirmScreen.NAME);

        if (e.getSource() == questionPanel.getButtonA()){
            currentGame.verifyAnswer(String.valueOf(Letters.A));
            checkGameStatus = true;
        }
        else if (e.getSource() == questionPanel.getButtonB()){
            currentGame.verifyAnswer(String.valueOf(Letters.B));
            checkGameStatus = true;
        }
        else if (e.getSource() == questionPanel.getButtonC()){
            currentGame.verifyAnswer(String.valueOf(Letters.C));
            checkGameStatus = true;
        }
        else if (e.getSource() == questionPanel.getButtonD()){
            currentGame.verifyAnswer(String.valueOf(Letters.D));
            checkGameStatus = true;
        }
        else if (e.getSource() == questionPanel.getExitButton()){
            confirmScreen = new ConfirmScreen("exit", this);
            panelCont.add(confirmScreen, confirmScreen.NAME);
            changeCard(confirmScreen.NAME);

        }

        if (checkGameStatus){
            removeCard(questionPanel);

            if (currentGame.isRunning()){
                correctAnswerPanel = new CorrectAnswerPanel(this);
                addCard(correctAnswerPanel, correctAnswerPanel.NAME);
                changeCard(correctAnswerPanel.NAME);
            }

            if (!currentGame.isRunning()){
                if (currentGame.isGameWon()){
                    correctAnswerPanel = new CorrectAnswerPanel(this);
                    addCard(correctAnswerPanel, correctAnswerPanel.NAME);
                    changeCard(correctAnswerPanel.NAME);
                }
                else {
                    inCorrectAnswerPanel = new InCorrectAnswerPanel(currentGame.getGameRounds(), currentGame.currentQuestion.getCorrectAnswerStr(), this);
                    addCard(inCorrectAnswerPanel, inCorrectAnswerPanel.NAME);
                    changeCard(inCorrectAnswerPanel.NAME);
                }

            }

        }

    }

    public void startGame(){
        currentGame = new GameApplication();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScreenControl::new);
    }


}