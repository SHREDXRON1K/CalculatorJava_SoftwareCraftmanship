import java.util.ArrayList;
import java.util.List;

public class Calculator{


    public List<Token> createTokenList(String expression) {
        List <Token> toReturn = new ArrayList<>();

        String expression_element;

        StringBuffer number_buffer = new StringBuffer();

        String number;

        for(int i=0; i<expression.length(); i++){
            if (expression.charAt(i) >= '0' && expression.charAt(i) <= '9' || expression.charAt(i) == '.') {
            number_buffer.append(expression.charAt(i));

            }
            else{
                if(number_buffer.length() > 0){
                number = number_buffer.toString();
                toReturn.add(new Token(number, -1, Double.parseDouble(number)));
                number_buffer.setLength(0);
                }

                expression_element = String.valueOf(expression.charAt(i));

                if (expression_element.equals("+")) toReturn.add(new Token(expression_element, 1, 0));
                else if (expression_element.equals("-")) toReturn.add(new Token(expression_element, 1, 0));
                else if (expression_element.equals("*")) toReturn.add(new Token(expression_element, 2, 0));
                else if (expression_element.equals("/")) toReturn.add(new Token(expression_element, 2, 0));
                else if (expression_element.equals("(")) toReturn.add(new Token(expression_element, 0, 0));
                else if (expression_element.equals(")")) toReturn.add(new Token(expression_element, 0, 0));
            }
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

    public Token applyRules(List<Token> expression){

        int degree;

        Token operator;
        Token operand1;
        Token operand2;
        int i;

        while(expression.size()!=1){

            degree = checkHighestDegree(expression);

            for(i=0; i<expression.size(); i++){
                if(expression.get(i).getSymbol().equals("(") ){
                    expression = handleBracket(i, expression);
                    continue;
                }
                
                operator = expression.get(i);
                if(operator.isOperator()){
                    operand1 = expression.get(i-1);
                    operand2 = expression.get(i+1);
                    if(!operand1.isOperator() && !operand2.isOperator()){
                        if(operator.strength()==2 && degree==2) expression = Degree2Operation(i, expression);
                        else if(operator.strength()==1 && degree==1) expression = Degree1Operation(i, expression); 
                    }
                }               
            }
        }
        return expression.get(0);

    }

    public double Calculate(String expression){
        List<Token> term = createTokenList(expression);
        Token end;
        end = applyRules(term);
        return end.getValue();

    }
}