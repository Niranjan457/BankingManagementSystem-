package model;


import database.TransactionDAO;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class BankingService {

   
    public static String createAccount(String userId, String type,
                                       double initialDeposit) {
        String accountNo = generateAccountNo();
        double interestRate = 0.0;
        String maturityDate = null;

       
        switch (type.toUpperCase()) {
            case "SAVINGS":       interestRate = 4.0;  break;
            case "FIXED_DEPOSIT": interestRate = 7.5;
                                  
                                  maturityDate = LocalDate.now().plusYears(1).toString();
                                  break;
            case "CURRENT":       interestRate = 0.0;  break;
        }

        boolean created = TransactionDAO.createAccount(
                accountNo, userId, type.toUpperCase(),
                initialDeposit, interestRate, maturityDate);

        if (created) {
            
            TransactionDAO.logTransaction(null, accountNo, initialDeposit,
                    "DEPOSIT", "Account opening deposit");
            return accountNo;
        }
        return null;
    }

    
    public static double deposit(String accountNo, double amount) {
        if (amount <= 0) return -1;

        String type    = TransactionDAO.getAccountType(accountNo);
        double balance = TransactionDAO.getBalance(accountNo);
        if (type == null || balance < 0) return -1;

        // Polymorphism: create appropriate account object, call deposit()
        BankAccount account = buildAccount(accountNo, "USER", balance, type);
        if (!account.deposit(amount)) return -1;

        // Persist to DB
        TransactionDAO.updateBalance(accountNo, account.getBalance());
        TransactionDAO.logTransaction(null, accountNo, amount, "DEPOSIT", "Deposit");
        return account.getBalance();
    }

    public static double withdraw(String accountNo, double amount) {
        if (amount <= 0) return -1;

        String type    = TransactionDAO.getAccountType(accountNo);
        double balance = TransactionDAO.getBalance(accountNo);
        if (type == null || balance < 0) return -1;

        BankAccount account = buildAccount(accountNo, "USER", balance, type);

        if ("FIXED_DEPOSIT".equals(type)) {
            return -2; 
        }

        if (!account.withdraw(amount)) return -1;

        TransactionDAO.updateBalance(accountNo, account.getBalance());
        TransactionDAO.logTransaction(accountNo, null, amount, "WITHDRAWAL", "Withdrawal");
        return account.getBalance();
    }

   
    public static String transferDomestic(String fromAcc, String toAcc, double amount) {
        if (!TransactionDAO.accountExists(toAcc)) return "Receiver account not found.";

        String type    = TransactionDAO.getAccountType(fromAcc);
        double balance = TransactionDAO.getBalance(fromAcc);
        BankAccount account = buildAccount(fromAcc, "USER", balance, type);

        if (!account.transferDomestic(amount)) return "Insufficient balance.";

       
        double receiverBal = TransactionDAO.getBalance(toAcc);
        TransactionDAO.updateBalance(fromAcc, account.getBalance());
        TransactionDAO.updateBalance(toAcc, receiverBal + amount);
        TransactionDAO.logTransaction(fromAcc, toAcc, amount,
                "TRANSFER_DOMESTIC", "Domestic transfer");
        return "SUCCESS";
    }

    
    public static String transferInternational(String fromAcc, String toAcc, double amount) {
        if (!TransactionDAO.accountExists(toAcc)) return "Receiver account not found.";

        String type    = TransactionDAO.getAccountType(fromAcc);
        double balance = TransactionDAO.getBalance(fromAcc);
        BankAccount account = buildAccount(fromAcc, "USER", balance, type);

        double charged = account.transferInternational(amount);
        if (charged == -1) return "Insufficient balance (includes 2% international fee).";

        double receiverBal = TransactionDAO.getBalance(toAcc);
        TransactionDAO.updateBalance(fromAcc, account.getBalance());
        TransactionDAO.updateBalance(toAcc, receiverBal + amount);
        TransactionDAO.logTransaction(fromAcc, toAcc, charged,
                "TRANSFER_INTERNATIONAL", "International transfer (fee included)");
        return "SUCCESS:" + charged;
    }

   
    public static List<String[]> getHistory(String accountNo, int limit) {
        return TransactionDAO.getTransactionHistory(accountNo, limit);
    }

    
    public static boolean generatePIN(String accountNo, String pin) {
        return TransactionDAO.saveATMPin(accountNo, pin);
    }

    public static boolean validatePIN(String accountNo, String pin) {
        return TransactionDAO.validateATMPin(accountNo, pin);
    }

    
    private static String generateAccountNo() {
        return "ACC" + (10000 + new Random().nextInt(89999));
    }

  
    private static BankAccount buildAccount(String no, String userId,
                                            double bal, String type) {
        switch (type.toUpperCase()) {
            case "SAVINGS":       return new SavingsAccount(no, userId, bal);
            case "CURRENT":       return new CurrentAccount(no, userId, bal);
            case "FIXED_DEPOSIT": {
                String mat = TransactionDAO.getMaturityDate(no);
                return new FixedDepositAccount(no, userId, bal,
                        mat != null ? mat : "N/A", 7.5);
            }
            default:              return new SavingsAccount(no, userId, bal);
        }
    }
}
