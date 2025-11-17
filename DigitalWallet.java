import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DigitalWallet extends JFrame {

    JTextField txtUser, txtAmount;
    JPasswordField txtPin;
    JLabel lblBalance;

    Connection con;

    public DigitalWallet() {

        // CONNECT TO DATABASE
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/walletdb",
                "root",
                "Saicharan@06" // change to your own MySQL password
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!");
        }

        // GUI DESIGN
        setTitle("Digital Wallet Simulator");
        setSize(450, 420);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel l1 = new JLabel("Username:");
        l1.setBounds(40, 40, 100, 30);
        add(l1);

        txtUser = new JTextField();
        txtUser.setBounds(150, 40, 200, 30);
        add(txtUser);

        JLabel l2 = new JLabel("PIN:");
        l2.setBounds(40, 90, 100, 30);
        add(l2);

        txtPin = new JPasswordField();
        txtPin.setBounds(150, 90, 200, 30);
        add(txtPin);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(160, 140, 100, 30);
        add(btnLogin);

        lblBalance = new JLabel("Balance: ₹0");
        lblBalance.setBounds(40, 190, 300, 30);
        lblBalance.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblBalance);

        JLabel l3 = new JLabel("Amount:");
        l3.setBounds(40, 240, 100, 30);
        add(l3);

        txtAmount = new JTextField();
        txtAmount.setBounds(150, 240, 200, 30);
        add(txtAmount);

        JButton btnDeposit = new JButton("Deposit");
        btnDeposit.setBounds(70, 290, 120, 30);
        add(btnDeposit);

        JButton btnWithdraw = new JButton("Withdraw");
        btnWithdraw.setBounds(220, 290, 120, 30);
        add(btnWithdraw);

        // EVENT HANDLERS
        btnLogin.addActionListener(e -> login());
        btnDeposit.addActionListener(e -> deposit());
        btnWithdraw.addActionListener(e -> withdraw());
    }

    // LOGIN FUNCTION
    void login() {
        try {
            String sql = "SELECT balance FROM users WHERE username=? AND pin=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtUser.getText());
            ps.setString(2, new String(txtPin.getPassword()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblBalance.setText("Balance: ₹" + rs.getDouble(1));
                JOptionPane.showMessageDialog(this, "Login Successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or PIN!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error:" + ex.getMessage());
        }
    }

    // DEPOSIT FUNCTION
    void deposit() {
        try {
            double amount = Double.parseDouble(txtAmount.getText());

            String sql = "UPDATE users SET balance = balance + ? WHERE username=? AND pin=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDouble(1, amount);
            ps.setString(2, txtUser.getText());
            ps.setString(3, new String(txtPin.getPassword()));

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Amount Deposited!");
                login();
            } else {
                JOptionPane.showMessageDialog(this, "Login Required!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter valid amount");
        }
    }

    // WITHDRAW FUNCTION
    void withdraw() {
        try {
            double amount = Double.parseDouble(txtAmount.getText());

            String sql = "UPDATE users SET balance = balance - ? WHERE username=? AND pin=? AND balance >= ?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setDouble(1, amount);
            ps.setString(2, txtUser.getText());
            ps.setString(3, new String(txtPin.getPassword()));
            ps.setDouble(4, amount);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Amount Withdrawn!");
                login();
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient Balance or Wrong Login!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter valid amount");
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {
        new DigitalWallet().setVisible(true);
    }
}
