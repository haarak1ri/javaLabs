package com.example.labs;

import com.example.labs.core.Habitat;
import com.example.labs.core.TimerService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.Map;
import java.util.Optional;

public class HelloController  {
    //лаба 1
    @FXML
    private BorderPane root;
    @FXML
    private Canvas gameCanvas;
    //панель инструментов
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button showTimerButton;
    @FXML
    private Label timerLabel;
    @FXML
    private Label boyCountLabel;
    @FXML
    private Label girlCountLabel;
    @FXML
    private Label totalCountLabel;
    @FXML
    private Label totalSimulationTime;
    //лаба 2 новый интерфейс
    //панель управления
    @FXML private Button startButtonPanel;
    @FXML private Button stopButtonPanel;
    // Radio buttons для времени
    @FXML private RadioButton showTimeRadio;
    @FXML private RadioButton hideTimeRadio;
    @FXML private ToggleGroup timeToggleGroup;
    // CheckBox
    @FXML private CheckBox ShowInfoCheckBox;
    // Текстовые поля
    @FXML private TextField n1Field;
    @FXML private TextField n2Field;
    // ComboBox для вероятностей
    @FXML private ComboBox<String> p1Combo;
    @FXML private ComboBox<String> p2Combo;

    //лаба 3
    @FXML private TextField n1TimeOfLifeField;
    @FXML private TextField n2TimeOfLifeField;
    @FXML private Button currentObjects;
    //лаба 4
    @FXML private Button boysAIstart;
    @FXML private Button boysAIstop;
    @FXML private Button girlsAIstart;
    @FXML private Button girlsAIstop;
    @FXML private Label boysMoveLabel;
    @FXML private Label girlssMoveLabel;
    @FXML private ComboBox<Integer> bpr;
    @FXML private ComboBox<Integer> gpr;


    private GraphicsContext gc;

    private Habitat habitat;
    private TimerService timerService;


    private BooleanProperty simulationRunning = new SimpleBooleanProperty(false);
    private BooleanProperty boysAIRunning = new SimpleBooleanProperty(true); //устанавливаем здесь чтобы не выносить в отдельный метод
    private BooleanProperty girlsAIRunning = new SimpleBooleanProperty(true);

    @FXML
    public void initialize() {
        root.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        this.gc = gameCanvas.getGraphicsContext2D();
        setupDefaultValues();
        bindAllStates(); // доступности кнопок, видимости таймеров
        setupAllValidations(); //валидации ввода вывода

        startStopSimulationSetup();
        threadsSetup();
        clearCanvas();

    }

    private void clearCanvas() {
        if (gc != null) {
            gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        }
    }

    //сетап видимости кнопок, валидации для полей и списков
    private void bindAllStates() {
        bindAIButtonsState();
        bindStartStopButtonsState();
        bindCurrentObjButtonState();
        bindParamsState();
        bindTimerLabelState();
    }
    private void setupAllValidations() {
        setupN1N2Validation();
        setupPriorityValidation();
        setupLifeTimeValidation();
    }
    //сетап значений по умолчанию
    private void setupDefaultValues() {
        n1Field.setText("1.0");
        n2Field.setText("1.5");
        p1Combo.setValue("70%");
        p2Combo.setValue("50%");
        n1TimeOfLifeField.setText("5.0");
        n2TimeOfLifeField.setText("7.0");
        bpr.setValue(5);
        gpr.setValue(5);
    }
    //реализация доступной и видимостей кнопок
    private void bindAIButtonsState() {

        boysAIstart.disableProperty().bind(boysAIRunning.or(simulationRunning.not()));
        boysAIstop.disableProperty().bind(boysAIRunning.not().or(simulationRunning.not()));
        girlsAIstart.disableProperty().bind(girlsAIRunning.or(simulationRunning.not()));
        girlsAIstop.disableProperty().bind(girlsAIRunning.not().or(simulationRunning.not()));
    }
    private void bindStartStopButtonsState() {
        startButton.disableProperty().bind(simulationRunning);
        stopButton.disableProperty().bind(simulationRunning.not());
        startButtonPanel.disableProperty().bind(simulationRunning);
        stopButtonPanel.disableProperty().bind(simulationRunning.not());
    }
    private void bindCurrentObjButtonState() {
        currentObjects.disableProperty().bind(simulationRunning.not());

    }
    private void bindParamsState() {
        n1Field.disableProperty().bind(simulationRunning);
        n2Field.disableProperty().bind(simulationRunning);
        n1TimeOfLifeField.disableProperty().bind(simulationRunning);
        n2TimeOfLifeField.disableProperty().bind(simulationRunning);
        p1Combo.disableProperty().bind(simulationRunning);
        p2Combo.disableProperty().bind(simulationRunning);
        bpr.disableProperty().bind(simulationRunning);
        gpr.disableProperty().bind(simulationRunning);
    }
    private void bindTimerLabelState() {
        timerLabel.visibleProperty().bind(timeToggleGroup.selectedToggleProperty().isEqualTo(showTimeRadio));
    }

    private void startStopSimulationSetup() {
        simulationRunning.addListener((obs, oldVal, newVal) -> {
            if (newVal ) {
                startSimulation();
            }
            else if (!newVal) {
                stopSimulation();
            }

        });
    }
    private void threadsSetup() {
        boysAIRunning.addListener((obs,oldVal, newVal)-> {
            if(habitat != null) {
                if(newVal) {
                    habitat.boysAIresume();
                } else {
                    habitat.boysAIpause();
                }
            }

        });

        girlsAIRunning.addListener((obs,oldVal, newVal)-> {
            if(habitat != null) {
                if(newVal) {
                    habitat.girlsAIresume();
                } else {
                    habitat.girlsAIpause();
                }
            }
        });
    }
    private void setupPriorityValidation() {
        bpr.valueProperty().addListener((obs,oldVal,newVal) -> {
            if(newVal == null){
                bpr.setValue(5);
            }
        });
        gpr.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                gpr.setValue(5);
            }
        });
    }

    //работа с dependecy inj
    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }
    public void setTimer(TimerService timerService) {
        this.timerService = timerService;
        timerService.setOnUpdate(() -> {refreshDisplay();
            updateTimerDisplay();
         });
    }

    private void updateTimerDisplay() {
        if (timerLabel != null && habitat != null) {

            float time = habitat.getSimulationTime();
            timerLabel.setText(String.format("Время: %.1f сек", time));
        }
    }
    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        // Обрабатываем только нужные клавиши
        if (code == KeyCode.B || code == KeyCode.E || code == KeyCode.T) {
            executeActionForKey(code);
            event.consume();
        }

    }
    private void executeActionForKey(KeyCode code) {
        switch (code) {
            case B:
                simulationRunning.set(true);
                break;
            case E:
                simulationRunning.set(false);
                break;
            case T:
                toggleTimer();
                break;
        }
    }
    private void toggleTimer() {
        if (showTimeRadio.isSelected()) {
            hideTimeRadio.setSelected(true);
        } else {
            showTimeRadio.setSelected(true);
        }
    }

    private void startSimulation() {
        if(!timerService.isRunning()) {
            float n1 = Float.parseFloat(n1Field.getText());
            float n2 = Float.parseFloat(n2Field.getText());
            double p1 = getProbabilityFromCombo(p1Combo);
            double p2 = getProbabilityFromCombo(p2Combo);
            int boysPriority = bpr.getValue() != null ? bpr.getValue() : 5;
            int girlsPriority = gpr.getValue() != null ? gpr.getValue() : 5;

            float nt1 = Float.parseFloat(n1TimeOfLifeField.getText());
            float nt2 = Float.parseFloat(n2TimeOfLifeField.getText());
            habitat.setParams(n1, n2, p1, p2, nt1, nt2, boysPriority,girlsPriority);
            habitat.reset();
            habitat.startAI();
            timerService.start();
        }
        System.out.println("Созданы два потока с приоритетмами Boy: " + habitat.getBoysPriority() + " Girl " + habitat.getGirlsPriority());
    }

    private void stopSimulation() {

        if(timerService.isRunning()) {
            timerService.pause();
            if(ShowInfoCheckBox.isSelected()) {
                showModalDialog();
            } else {
                habitat.stopAI();
                timerService.stop();
                habitat.reset();
                clearCanvas();
                timerLabel.setText("Время: 0.0 сек");
            }
        }

    }



    public void handleStartButton(ActionEvent actionEvent) {
        simulationRunning.set(true);
    }
    public void handleStopButton(ActionEvent actionEvent) {
        simulationRunning.set(false);
    }
    public void handleShowTimerButton(ActionEvent actionEvent) {
        toggleTimer();
    }
    public void refreshDisplay() {
        if (habitat != null && gc != null) {
            clearCanvas();
            habitat.renderAll(gc);
        }
    }



//лаба 2============================================================================================================v
    public void handleStartButtonPanel(ActionEvent actionEvent) {
        simulationRunning.set(true);;
}
    public void handleStopButtonPanel(ActionEvent actionEvent) {
        simulationRunning.set(false);
    }
    private void setupN1N2Validation() {
        n1Field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                n1Field.setText(oldVal);
            }

            else if (newVal.startsWith("-")) {
                n1Field.setText(oldVal);
            }
            else if (newVal.startsWith(".")) {
                n1Field.setText(oldVal);
            }

            else if (!newVal.isEmpty() && !newVal.equals(".") && Float.parseFloat(newVal) < 1) {
                n1Field.setText(oldVal);
            }
            else if (!newVal.isEmpty() && newVal.length() > 1 && newVal.startsWith("0") && !newVal.startsWith("0.")) {
                n1Field.setText(oldVal);
            }
        });

        n2Field.textProperty().addListener((obs, oldVal, newVal) -> {

            if (!newVal.matches("\\d*\\.?\\d*")) {
                n2Field.setText(oldVal);
            }

            else if (newVal.startsWith("-")) {
                n2Field.setText(oldVal);
            }
            else if (newVal.startsWith(".")) {
                n2Field.setText(oldVal);
            }

            else if (!newVal.isEmpty() && !newVal.equals(".") && Float.parseFloat(newVal) < 1) {
                n2Field.setText(oldVal);
            }
            else if (!newVal.isEmpty() && newVal.length() > 1 && newVal.startsWith("0") && !newVal.startsWith("0.")) {
                n2Field.setText(oldVal);
            }
        });
    }


    private double getProbabilityFromCombo(ComboBox<String> combo) {
        String value = combo.getValue();
        if (value == null) return 0.0;
        try {
            String numberPart = value.replace("%", "");
            double percent = Double.parseDouble(numberPart);
            return percent / 100.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void showModalDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Информация о симуляции");
        dialog.setHeaderText("Статистика по окончании симуляции");

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefRowCount(4);
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);

        textArea.setText(
                "Мальчики: " + habitat.getBoyCount() + "\n" +
                "Девочки: " + habitat.getGirlCount() + "\n" +
                "Всего: " + habitat.getTotalCount() + "\n" +
                "Время симуляции: " + String.format("%.1f", habitat.getSimulationTime()) + " сек"
        );

        dialog.getDialogPane().setContent(textArea);

        ButtonType okButton = new ButtonType("Ок",ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена",ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(okButton,cancelButton);

        dialog.setResultConverter(button -> {
            if (button == okButton) {
                return true;
            } else if (button == cancelButton) {
                return false;
            } else {
                return false;
            }
        });

        Optional<Boolean> result = dialog.showAndWait();
        if(result.isPresent() && result.get()) {
            habitat.stopAI();
            timerService.stop();
            habitat.reset();
            clearCanvas();
            timerLabel.setText("Время: 0.0 сек");
        }
        else {
            timerService.start();
            simulationRunning.set(true);

        }
    }

//лаба 3=================================================================================================================

    private void setupLifeTimeValidation() {
        n1TimeOfLifeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                n1TimeOfLifeField.setText(oldVal);
            }

            else if (newVal.startsWith("-")) {
                n1TimeOfLifeField.setText(oldVal);
            }
            else if (newVal.startsWith(".")) {
                n1TimeOfLifeField.setText(oldVal);
            }

            else if (!newVal.isEmpty() && !newVal.equals(".") && Float.parseFloat(newVal) < 1) {
                n1TimeOfLifeField.setText(oldVal);
            }
            else if (!newVal.isEmpty() && newVal.length() > 1 && newVal.startsWith("0") && !newVal.startsWith("0.")) {
                n1TimeOfLifeField.setText(oldVal);
            }
        });

        n2TimeOfLifeField.textProperty().addListener((obs, oldVal, newVal) -> {

            if (!newVal.matches("\\d*\\.?\\d*")) {
                n2TimeOfLifeField.setText(oldVal);
            }

            else if (newVal.startsWith("-")) {
                n2TimeOfLifeField.setText(oldVal);
            }
            else if (newVal.startsWith(".")) {
                n2TimeOfLifeField.setText(oldVal);
            }
            else if (!newVal.isEmpty() && !newVal.equals(".") && Float.parseFloat(newVal) < 1) {
                n2TimeOfLifeField.setText(oldVal);
            }
            else if (!newVal.isEmpty() && newVal.length() > 1 && newVal.startsWith("0") && !newVal.startsWith("0.")) {
                n2TimeOfLifeField.setText(oldVal);
            }
        });
    }
    public void handleGetCurrentObjectsButton(ActionEvent actionEvent) {
       getCurrentObjects();
    }
    private void getCurrentObjects() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Информация о симуляции");
        dialog.setHeaderText("Живых студентов:");
        dialog.setWidth(400);
        dialog.setHeight(300);
        dialog.setResizable(false);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));


        Label timeHeader = new Label("Время рождения");
        timeHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label idHeader = new Label("ID");
        idHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Separator separator = new Separator();
        separator.setMaxWidth(Double.MAX_VALUE);

        grid.add(timeHeader,0,0); // столб / строка
        grid.add(idHeader,1,0);
        grid.add(separator, 0, 1, 2, 1);
        int row = 2;
        boolean hasLivingObjects = false;

        System.out.println("=== getCurrentObjects вызван ===");
        System.out.println("Размер birthToId: " + habitat.getBirthToId().size());
        System.out.println("Размер activeIds: " + habitat.getActiveIds().size());

        for(Map.Entry<Long,Integer> entry : habitat.getBirthToId().entrySet()) {
            long creationTime = entry.getKey();
            float realTime = creationTime / 1e9f;
            String s = String.format("%.2f", realTime);
            int id = entry.getValue();
            if(habitat.getActiveIds().contains(id)) {
                grid.add(new Label(s), 0, row);
                grid.add(new Label("id " + id), 1, row);
                row++;
                hasLivingObjects = true;
            }
        }

        if (!hasLivingObjects) {

            Label emptyLabel = new Label("нет ни одного живого студента :(");
            emptyLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
            grid.add(emptyLabel, 0, 2, 2, 1);
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportWidth(300);
        scrollPane.setPrefViewportHeight(350);
        scrollPane.setPadding(new Insets(0));

        dialog.getDialogPane().setContent(scrollPane);

        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.CANCEL,
                ButtonType.CLOSE
        );
        dialog.setResultConverter(button -> {
            if (button == ButtonType.CLOSE || button == ButtonType.CANCEL) {
                dialog.close();
            }
            return null;
        });
        dialog.showAndWait();
    }
    public void handleBoysAIStartButton(ActionEvent actionEvent) {
        boysAIRunning.set(true);
    }
    public void handleBoysAIStopButton(ActionEvent actionEvent) {
        boysAIRunning.set(false);
    }
    public void handleGirlsAIStartButton(ActionEvent actionEvent) {
        girlsAIRunning.set(true);
    }
    public void handleGirlsAIStopButton(ActionEvent actionEvent) {
        girlsAIRunning.set(false);
    }
}