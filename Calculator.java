import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Calculator{

private List< Map.Entry<String, Double> > variables_valuePairs = new ArrayList<>();


    public List<Token> createTokenList(String expression) throws Exception {
    List<Token> toReturn = new ArrayList<>();

    String expression_element;
    StringBuilder number_buffer = new StringBuilder();
    StringBuilder variable_buffer = new StringBuilder();
    String number;
    String variable;
    char expressionPosition;
    Map.Entry<String, Double> pairsPosition;
    boolean found = false;

    for (int i = 0; i < expression.length(); i++) {
        expressionPosition = expression.charAt(i);

        if ((expressionPosition >= '0' && expressionPosition <= '9') || expressionPosition == '.') {
            number_buffer.append(expressionPosition);
            continue;
        }

        if ((expressionPosition >= 'a' && expressionPosition <= 'z')
                || (expressionPosition >= 'A' && expressionPosition <= 'Z')) {
            variable_buffer.append(expressionPosition);
            continue;
            }

        if(variable_buffer.length()>0){
            variable = variable_buffer.toString();
            for (int j = 0; j < variables_valuePairs.size(); j++) {
                pairsPosition = variables_valuePairs.get(j);
                if (variable.equals(pairsPosition.getKey())) {
                    toReturn.add(new Token(variable, -1, pairsPosition.getValue()));
                    found = true;
                    break;
                }
            }
            if (!found) throw new Exception("Variable is not defined: " + variable);
        }
            found = false;
            variable_buffer.setLength(0);
            
            if(number_buffer.length() > 0){
            number = number_buffer.toString();
            toReturn.add(new Token(number, -1, Double.parseDouble(number)));
            number_buffer.setLength(0);
            }

            expression_element = String.valueOf(expressionPosition);

            switch(expression_element){
                case "+" : toReturn.add(new Token(expression_element, 1, 0)); break;
                case "-" : toReturn.add(new Token(expression_element, 1, 0)); break;
                case "*" : toReturn.add(new Token(expression_element, 2, 0)); break;
                case "/" : toReturn.add(new Token(expression_element, 2, 0)); break;
                case "(" : toReturn.add(new Token(expression_element, 0, 0)); break;
                case ")" : toReturn.add(new Token(expression_element, 0, 0)); break;
            }    
        }
    
        if (number_buffer.length() > 0) {
        number = number_buffer.toString();
        toReturn.add(new Token(number, -1, Double.parseDouble(number)));
        }

        System.out.println("Operation : " + expression);
        return toReturn;
    }


    public void computeNestingLevel(List<Token> expression) {
    int level = 0;
    Token tokenAt_i;
    String string_TokenAt_i;
    for (int i = 0; i < expression.size(); i++) {
        tokenAt_i = expression.get(i);
        string_TokenAt_i = tokenAt_i.getSymbol();
        if (string_TokenAt_i.equals("(")) {
            level++;
            tokenAt_i.setNestingLevel(level);
        } else if (string_TokenAt_i.equals(")")) {
            tokenAt_i.setNestingLevel(level);
            level--;
        } else {
            tokenAt_i.setNestingLevel(level);
        }
    }
    for(int i=0; i<expression.size(); i++){
        tokenAt_i = expression.get(i);
        System.out.print(tokenAt_i.getSymbol() + "{" +tokenAt_i.getNestingLevel() + "}" + "  ");
    }
    System.out.println("\n");

}

    public List<Token> performOperation(int i, List<Token> expression) throws Exception{
        double value_operand1 = expression.get(i-1).getValue();
        double value_operand2 = expression.get(i+1).getValue();
        String string_operator = expression.get(i).getSymbol();

        double newValue;
        String string_newValue;

        switch (string_operator) {
            case "+": newValue = value_operand1 + value_operand2;
                break;
            case "-": newValue = value_operand1 - value_operand2;
                break;
            case "*": newValue = value_operand1 * value_operand2;
                break;
            case "/": 
                if(value_operand2 == 0) throw new Exception("Zero divisor is not allowed!");
                else newValue = value_operand1 / value_operand2;
                break;
            default:
                return expression;
        }
        string_newValue = String.valueOf(newValue);
        expression.remove(i+1);
        expression.remove(i);
        expression.remove(i-1);
        expression.add(i-1,new Token(string_newValue, -1, newValue) );
        return expression;
    }

    public int checkHighestDegree(List<Token> expression){
        int degree=0;
        for(int i=0; i<expression.size(); i++){
            if(expression.get(i).strength()==2) {
                degree=2;
                break;
            }
            else if(expression.get(i).strength()==1){
                degree = 1;
            }
        }
        return degree;
    }

    public List<Token> handleBracket(int i, List<Token> expression){
        List<Token> inside_bracket = new ArrayList<>();
        int j = i+1;
        while( j<expression.size() && !expression.get(j).getSymbol().equals(")") ){
            if(expression.get(j).getSymbol().equals("(")) handleBracket(j, expression);
            else {inside_bracket.add( expression.get(j) );
            j++;}
        }

        for(int k=j; k>=i; k--){
            expression.remove(k);
        }

        expression.add(i, applyRules(inside_bracket));
        return expression;   
    }

    public Token applyRules(List<Token> expression) {

    int degree;
    Token operator;
    Token operand1;
    Token operand2;

    String stringAt_i;

    while (expression.size() != 1) {
        computeNestingLevel(expression);
        degree = checkHighestDegree(expression);

        for (int i = 0; i < expression.size(); i++) {
            stringAt_i = expression.get(i).getSymbol();
            if (stringAt_i.equals("(")) {
                expression = handleBracket(i, expression);
                i = -1; // restart after modification
                continue;
            }

            operator = expression.get(i);

            if (operator.isOperator()) {
            
                operand1 = expression.get(i - 1);
                operand2 = expression.get(i + 1);

                if (!operand1.isOperator() && !operand2.isOperator()) try{
                    if (operator.strength() == degree && (degree == 1 || degree == 2) ) {
                        expression = performOperation(i, expression);
                        i = 0; // restart after list change
                    }

                }catch(Exception e){
                    System.err.println("Exception : " + e.getMessage());
                    return new Token("NaN", -1, Double.NaN);
                }
            }
        }
    }
    return expression.get(0);
}

    //Erweiterung fuer 1. Software Craftmanship Praktikum    
    public void define(String variable, double value){
        Map.Entry<String, Double> variable_value = new AbstractMap.SimpleEntry<>(variable, value);
        variables_valuePairs.add(variable_value);
    }

    public double Calculate(String expression){
        try{ 
            List<Token> term = createTokenList(expression);
            Token end = applyRules(term);
            return end.getValue();
        }
        catch(Exception e){
            System.out.println("Exception : " + e.getMessage());
        }
        return Double.NaN;
    }
}