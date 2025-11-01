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
            // if (variables_valuePairs.isEmpty()) throw new Exception("Variable found but none defined: " + variable);
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

            if (expression_element.equals("+")) toReturn.add(new Token(expression_element, 1, 0));
            else if (expression_element.equals("-")) toReturn.add(new Token(expression_element, 1, 0));
            else if (expression_element.equals("*")) toReturn.add(new Token(expression_element, 2, 0));
            else if (expression_element.equals("/")) toReturn.add(new Token(expression_element, 2, 0));
            else if (expression_element.equals("(")) toReturn.add(new Token(expression_element, 0, 0));
            else if (expression_element.equals(")")) toReturn.add(new Token(expression_element, 0, 0));
        }
    

    if (number_buffer.length() > 0) {
    number = number_buffer.toString();
    toReturn.add(new Token(number, -1, Double.parseDouble(number)));
    }

        return toReturn;
    }


    public void computeNestingLevel(List<Token> expression){
        int level = 0;
        for(int i=0; i<expression.size(); i++){
            if( expression.get(i).getSymbol().compareTo("(") == 0) level++;
            if( expression.get(i).getSymbol().compareTo(")") == 0) level--;
        } 
    } 

    public List<Token> Degree2Operation(int i, List<Token> expression){
        double value_operand1 = expression.get(i-1).getValue();
        double value_operand2 = expression.get(i+1).getValue();
        String string_operator = expression.get(i).getSymbol();

        int pos = i-1;

        double newValue;
        String string_newValue;
        if( string_operator.equals("*") ){ //reduce the code here
            newValue = value_operand1 * value_operand2;
            string_newValue = String.valueOf(newValue);
            expression.remove(i+1);
            expression.remove(i);
            expression.remove(i-1);
            expression.add(pos, new Token(string_newValue, -1, newValue));
        }
        if( string_operator.equals("/") ) {
            newValue=value_operand1 / value_operand2;
            string_newValue = String.valueOf(newValue);
            expression.remove(i+1);
            expression.remove(i);
            expression.remove(i-1);
            expression.add(pos, new Token(string_newValue, -1, newValue));
        }
        return expression;
    }

    public List<Token> Degree1Operation(int i, List<Token> expression){
        double value_operand1 = expression.get(i-1).getValue();
        double value_operand2 = expression.get(i+1).getValue();
        String string_operator = expression.get(i).getSymbol();

        int pos = i-1;

        double newValue;
        String string_newValue;
        if( string_operator.equals("+") ){
            newValue = value_operand1 + value_operand2;
            string_newValue = String.valueOf(newValue);
            expression.remove(i+1);
            expression.remove(i);
            expression.remove(i-1);
            expression.add(pos, new Token(string_newValue, -1, newValue));
        }
        if( string_operator.equals("-") ) {
            newValue=value_operand1 - value_operand2;
            string_newValue = String.valueOf(newValue);
            expression.remove(i+1);
            expression.remove(i);
            expression.remove(i-1);
            expression.add(pos, new Token(string_newValue, -1, newValue));
        }
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

    while (expression.size() != 1) {

        degree = checkHighestDegree(expression);

        for (int i = 0; i < expression.size(); i++) {
            if (expression.get(i).getSymbol().equals("(")) {
                expression = handleBracket(i, expression);
                i = -1; // restart after modification
                continue;
            }

            operator = expression.get(i);

            if (operator.isOperator()) {

                // ✅ Fix: skip only invalid operator positions
                if (i <= 0 || i >= expression.size() - 1) {
                    continue;
                }

                operand1 = expression.get(i - 1);
                operand2 = expression.get(i + 1);

                if (!operand1.isOperator() && !operand2.isOperator()) {
                    if (operator.strength() == 2 && degree == 2) {
                        expression = Degree2Operation(i, expression);
                        i = -1; // restart after list change
                    } else if (operator.strength() == 1 && degree == 1) {
                        expression = Degree1Operation(i, expression);
                        i = -1;
                    }
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
            Token end;
            end = applyRules(term);
            return end.getValue();
        }
        catch(Exception e){
            System.out.println("Exception : " + e.getMessage());
        }
        return Double.NaN;
    }
}