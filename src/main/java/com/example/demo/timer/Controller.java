package com.example.demo.timer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    @FXML
    private TableView<MessageItem> table;
    @FXML
    private TableColumn<MessageItem, String> msgColumn;
    @FXML
    private TableColumn<MessageItem, String> timeColumn;
    @FXML
    private TableColumn<MessageItem, Boolean> doneColumn;

    @FXML
    private TextField msgInput;
    @FXML
    private Spinner<Integer> secondsSpinner;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private RadioButton delayRadio, exactTimeRadio;

    private final ObservableList<MessageItem> messages = FXCollections.observableArrayList();
    private final Timer timer = new Timer(true);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        // Колонки
        msgColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        doneColumn.setCellValueFactory(new PropertyValueFactory<>("done"));

        table.setItems(messages);

        // Spinner для секунд
        SpinnerValueFactory<Integer> secFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3600, 10);
        secondsSpinner.setValueFactory(secFactory);
        secondsSpinner.setEditable(true);

        // Spinner для часу
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,12));
        hourSpinner.setEditable(true);
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,0));
        minuteSpinner.setEditable(true);

        // група радіо
        ToggleGroup group = new ToggleGroup();
        delayRadio.setToggleGroup(group);
        exactTimeRadio.setToggleGroup(group);
        delayRadio.setSelected(true);

        // Зчитуємо попередні повідомлення з файлу
        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File("messages.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Формат: text | time
                String[] parts = line.split("\\|");
                if (parts.length < 2) continue;

                String text = parts[0].trim();
                String timeStr = parts[1].trim();
                LocalDateTime msgTime = LocalDateTime.parse(timeStr, formatter);

                boolean done = msgTime.isBefore(LocalDateTime.now());
                MessageItem item = new MessageItem(text, timeStr, done);
                messages.add(item);

                // Плануємо таймер тільки для майбутніх повідомлень
                if (!done) scheduleMessage(item, msgTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addMessage() {
        String text = msgInput.getText().trim();
        if (text.isEmpty()) return;

        LocalDateTime triggerTime;
        if (delayRadio.isSelected()) {
            int sec = secondsSpinner.getValue();
            triggerTime = LocalDateTime.now().plusSeconds(sec);
        } else {
            if (datePicker.getValue() == null) return;
            triggerTime = datePicker.getValue().atTime(hourSpinner.getValue(), minuteSpinner.getValue());
        }

        MessageItem item = new MessageItem(text, triggerTime.format(formatter), false);
        messages.add(item);

        // Збереження в файл
        saveToFile(item);

        // Таймер
        scheduleMessage(item, triggerTime);

        msgInput.clear();
    }

    private void scheduleMessage(MessageItem item, LocalDateTime triggerTime) {
        long delay = java.time.Duration.between(LocalDateTime.now(), triggerTime).toMillis();
        if (delay < 0) delay = 0;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Reminder");
                    alert.setHeaderText("Повідомлення");
                    alert.setContentText(item.getText());
                    alert.show();

                    // Позначаємо як виконане
                    item.setDone(true);
                    table.refresh();
                });
            }
        }, delay);
    }

    private void saveToFile(MessageItem item) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("messages.txt", true))) {
            writer.write(item.getText() + " | " + item.getTime());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitApp() {
        Platform.exit();
        timer.cancel();
    }

    public static class MessageItem {
        private final String text;
        private final String time;
        private Boolean done;

        public MessageItem(String text, String time, Boolean done) {
            this.text = text;
            this.time = time;
            this.done = done;
        }

        public String getText() { return text; }
        public String getTime() { return time; }
        public Boolean getDone() { return done; }
        public void setDone(Boolean done) { this.done = done; }
    }
}