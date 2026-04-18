package model;


public abstract class BankAccount {

   
    private String accountNo;
    private String userId;
    private double balance;
    private String accountType;

  
    public BankAccount(String accountNo, String userId, double balance, String accountType) {
        this.accountNo   = accountNo;
        this.userId      = userId;
        this.balance     = balance;
        this.accountType = accountType;
    }

    
    public String getAccountNo()   { return accountNo; }
    public String getUserId()      { return userId; }
    public double getBalance()     { return balance; }
    public String getAccountType() { return accountType; }

    protected void setBalance(double balance) { this.balance = balance; }

    
    public abstract boolean deposit(double amount);

    
    public abstract boolean withdraw(double amount);

   
    public abstract String getAccountRules();

    
    public boolean transferDomestic(double amount) {
        if (amount <= 0 || amount > balance) return false;
        return withdraw(amount); 
    }

    
    public double transferInternational(double amount) {
        double fee   = amount * 0.02;       
        double total = amount + fee;
        if (total > balance) return -1;     
        if (withdraw(total)) return total;  
        return -1;
    }

    
    @Override
    public String toString() {
        return String.format("Account: %s | Type: %s | Balance: ₹%.2f",
                accountNo, accountType, balance);
    }
}
