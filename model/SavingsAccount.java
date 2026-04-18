package model;



public class SavingsAccount extends BankAccount {

    private static final double MIN_BALANCE    = 500.0;  
    private static final double INTEREST_RATE  = 4.0;  

    public SavingsAccount(String accountNo, String userId, double balance) {
        super(accountNo, userId, balance, "SAVINGS");
    }

    @Override
    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        setBalance(getBalance() + amount);
        return true;
    }

    @Override
    public boolean withdraw(double amount) {
        
        if (amount <= 0) return false;
        if (getBalance() - amount < MIN_BALANCE) return false;
        setBalance(getBalance() - amount);
        return true;
    }

    @Override
    public String getAccountRules() {
        return "Savings Account | Interest: " + INTEREST_RATE + "% p.a. | Min Balance: ₹" + MIN_BALANCE;
    }

    public double getInterestRate() { return INTEREST_RATE; }
}




class CurrentAccount extends BankAccount {

    private static final double OVERDRAFT_LIMIT = 10000.0; 

    public CurrentAccount(String accountNo, String userId, double balance) {
        super(accountNo, userId, balance, "CURRENT");
    }

    @Override
    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        setBalance(getBalance() + amount);
        return true;
    }

    @Override
    public boolean withdraw(double amount) {
        
        if (amount <= 0) return false;
        if (getBalance() - amount < -OVERDRAFT_LIMIT) return false; 
        setBalance(getBalance() - amount);
        return true;
    }

    @Override
    public String getAccountRules() {
        return "Current Account | No Interest | Overdraft Limit: ₹" + OVERDRAFT_LIMIT;
    }
}




class FixedDepositAccount extends BankAccount {

    private String maturityDate;     
    private double interestRate;

    public FixedDepositAccount(String accountNo, String userId,
                               double balance, String maturityDate, double interestRate) {
        super(accountNo, userId, balance, "FIXED_DEPOSIT");
        this.maturityDate = maturityDate;
        this.interestRate = interestRate;
    }

    @Override
    public boolean deposit(double amount) {
        
        return false;
    }

    @Override
    public boolean withdraw(double amount) {
       
        System.out.println("[FD] Withdrawal blocked: cannot withdraw before maturity date " + maturityDate);
        return false;
    }

    @Override
    public String getAccountRules() {
        return "Fixed Deposit | Interest: " + interestRate + "% p.a. | Matures: " + maturityDate
               + " | No early withdrawal";
    }

    public String getMaturityDate()  { return maturityDate; }
    public double getInterestRate()  { return interestRate; }
}
