import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PasswordGeneratorGUI extends JFrame {

    private JCheckBox upperCheck, lowerCheck, digitsCheck, specialCheck;
    private JTextField lengthField, passwordField;
    private JButton generateButton, copyButton, resetButton;
    private JLabel strengthLabel;

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{};:,.<>?";

    public PasswordGeneratorGUI() {
        setTitle("Password Generator");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(11, 1, 5, 5));

        lengthField = new JTextField("12");
        upperCheck = new JCheckBox("Include Uppercase Letters", true);
        lowerCheck = new JCheckBox("Include Lowercase Letters", true);
        digitsCheck = new JCheckBox("Include Numbers", true);
        specialCheck = new JCheckBox("Include Special Characters", false);

        passwordField = new JTextField();
        passwordField.setEditable(false);

        strengthLabel = new JLabel("Password Strength: ");

        generateButton = new JButton("Generate Password");
        copyButton = new JButton("Copy to Clipboard");
        resetButton = new JButton("Reset");

        panel.add(new JLabel("Password Length:"));
        panel.add(lengthField);
        panel.add(upperCheck);
        panel.add(lowerCheck);
        panel.add(digitsCheck);
        panel.add(specialCheck);
        panel.add(generateButton);
        panel.add(passwordField);
        panel.add(strengthLabel);
        panel.add(copyButton);
        panel.add(resetButton);

        add(panel);

        generateButton.addActionListener(e -> {
            int length;
            try {
                length = Integer.parseInt(lengthField.getText());
                if (length < 4) {
                    JOptionPane.showMessageDialog(this, "Length must be at least 4", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid length", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String password = generatePassword(length, upperCheck.isSelected(), lowerCheck.isSelected(),
                    digitsCheck.isSelected(), specialCheck.isSelected());
            passwordField.setText(password);
            String strength = getStrength(password);
            strengthLabel.setText("Password Strength: " + strength);
            savePasswordToFile(password, strength);
        });

        copyButton.addActionListener(e -> {
            String password = passwordField.getText();
            if (!password.isEmpty()) {
                StringSelection stringSelection = new StringSelection(password);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(this, "Password copied to clipboard");
            }
        });

        resetButton.addActionListener(e -> {
            lengthField.setText("12");
            upperCheck.setSelected(true);
            lowerCheck.setSelected(true);
            digitsCheck.setSelected(true);
            specialCheck.setSelected(false);
            passwordField.setText("");
            strengthLabel.setText("Password Strength: ");
        });
    }

    private String generatePassword(int length, boolean useUpper, boolean useLower, boolean useDigits, boolean useSpecial) {
        String charPool = "";
        if (useUpper) charPool += UPPER;
        if (useLower) charPool += LOWER;
        if (useDigits) charPool += DIGITS;
        if (useSpecial) charPool += SPECIAL;

        if (charPool.isEmpty()) {
            return "Select at least one character type!";
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charPool.length());
            password.append(charPool.charAt(index));
        }
        return password.toString();
    }

    private String getStrength(String password) {
        int types = 0;
        if (password.matches(".*[A-Z].*")) types++;
        if (password.matches(".*[a-z].*")) types++;
        if (password.matches(".*[0-9].*")) types++;
        if (password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{};:,.<>?].*")) types++;

        if (password.length() < 8 || types < 2) return "Weak";
        else if (password.length() < 12) return "Medium";
        else return "Strong";
    }

    private void savePasswordToFile(String password, String strength) {
        try (FileWriter writer = new FileWriter("saved_passwords.txt", true)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String time = LocalDateTime.now().format(dtf);
            writer.write("[" + time + "] " + password + " (" + strength + ")\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save password to file.", "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasswordGeneratorGUI().setVisible(true));
    }
}
