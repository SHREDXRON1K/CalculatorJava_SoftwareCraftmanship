public class main {
    public static void main(String[] args){
        Calculator a = new Calculator();
        a.define("pi",3.14);
        a.define("A",10);
        a.define("B",2);
        System.out.println( a.Calculate("(((10+pi+22)))") + "\n\n" );

        System.out.println( a.Calculate("(A+B)*10") + "\n\n" );

        System.out.println( a.Calculate("A+B*10") + "\n\n" );

        System.out.println( a.Calculate("(A+B)*(10-3)") + "\n\n" );
        
        System.out.println( a.Calculate("A+C*100") + "\n\n" );

        System.out.println( a.Calculate("(pi*10*10)+22/0") + "\n\n" );
    }
}

