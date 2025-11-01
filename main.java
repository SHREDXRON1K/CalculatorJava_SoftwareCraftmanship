public class main {
    public static void main(String[] args){
        Calculator a = new Calculator();
        a.define("pi",3.14);
        System.out.println( a.Calculate("10+pi+10") );
    }
}

