import java.util.ArrayList;
import java.util.Stack;

public class Analyse {
    public static void main( String[] args){
        Analyseur a = new Analyseur();
        String correct = "main(){ int id float id id = nombre if id>nombre id=nombre else id=nombre }";
        String nonCorrect = "main(){int int id}";
        try{
            if(a.analyser(correct)){
                System.out.println("accepté");
            }else{
                System.out.println("non accepté");
            }

            if(a.analyser(nonCorrect)){
                System.out.println("accepté");
            }else{
                System.out.println("non accepté");
            }

        }catch(Exception InconnuException){
            System.out.println("symbole non reconnu");
        }

    }
}
