public class Token {
    private String symbol;
    private int degree;
    private double value;
    private int nestingLevel;
    /* + : 1 d:1
     * - : 2 d:1
     * * : 3 d:2
     * / : 4 d:2
     * ( : 5 d:0
     * ) : 6 d:0
     * num : -1 d:-1
     */

    public Token(String symbol, int degree, double number){
        this.symbol = symbol;
        this.degree = degree;
        this.value = number;
    }

    public String getSymbol(){
        return symbol;
    }

    public double getValue(){
        return value;
    }

    public int getNestingLevel(){
        return nestingLevel;
    }

    public boolean isOperator(){
        if(degree>=0) return true;
        else return false;
    }

    public int strength(){
        return degree;
    }

    public void setNumValue(int num){
        symbol = String.valueOf(num);
        value = num;

    }

    public void setNestingLevel(int nestingLevel){
        this.nestingLevel = nestingLevel;
    }


}
