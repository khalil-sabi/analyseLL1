import java.util.ArrayList;
import java.util.Stack;

public class Analyse {
    public static void main( String[] args){
        Analyseur a = new Analyseur();
        String test = "main(){}";

        try{
            if(a.analyser(test)){
                System.out.println("oui");
            }else{
                System.out.println("non");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
