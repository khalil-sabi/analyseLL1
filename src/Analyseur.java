import java.util.*;

public class Analyseur {
    private static final ArrayList<String> terminaux;
    private static final ArrayList<String> nonTerminaux;
    private static final ArrayList<String> reglesProd;
    private static final HashMap<String, HashMap<String, String>> tableAnalyse;
    private Stack<String> pile;

    static{
        terminaux = new ArrayList<>();
        terminaux.add("main");
        terminaux.add("id");
        terminaux.add("int");
        terminaux.add("float");
        terminaux.add("=");
        terminaux.add("if");
        terminaux.add("(");
        terminaux.add(")");
        terminaux.add("{");
        terminaux.add("}");
        terminaux.add(">");
        terminaux.add("<");
        terminaux.add("else");
        terminaux.add("nombre");
        terminaux.add("$");
        terminaux.add("vide");

        nonTerminaux = new ArrayList<>();
        nonTerminaux.add("<programme>");
        nonTerminaux.add("<liste_declarations>");
        nonTerminaux.add("<une_declaration>");
        nonTerminaux.add("<liste_instructions>");
        nonTerminaux.add("<une_instruction>");
        nonTerminaux.add("<type>");
        nonTerminaux.add("<affectation>");
        nonTerminaux.add("<test>");
        nonTerminaux.add("<condition>");
        nonTerminaux.add("<operateur>");

        reglesProd = new ArrayList<>();
        reglesProd.add("<programme>::=main(){<liste_declarations><liste_instructions>}");
        reglesProd.add("<liste_declarations>::=<une_declaration><liste_declarations>");
        reglesProd.add("<liste_declarations>::=vide");
        reglesProd.add("<une_declaration>::=<type>id");
        reglesProd.add("<liste_instructions>::=<une_instruction><liste_instructions>");
        reglesProd.add("<liste_instructions>::=vide");
        reglesProd.add("<une_instruction>::=<affectation>");
        reglesProd.add("<une_instruction>::=<test>");
        reglesProd.add("<type>::=int");
        reglesProd.add("<type>::=float");
        reglesProd.add("<affectation>::=id=nombre");
        reglesProd.add("<test>::=if <condition> <une_instruction> else <une_instruction>");
        reglesProd.add("<condition>::=id<operateur>nombre");
        reglesProd.add("<operateur>::=<");
        reglesProd.add("<operateur>::=>");
        reglesProd.add("<operateur>::==");

        tableAnalyse = new HashMap<>();

        ajoutRegle("main","<programme>","<programme>::=main(){<liste_declarations><liste_instructions>}");

        ajoutRegle("id","<liste_declarations>","<liste_declarations>::=vide");
        ajoutRegle("id","<liste_instructions>","<liste_instructions>::=<une_instruction><liste_instructions>");
        ajoutRegle("id","<une_instruction>","<une_instruction>::=<affectation>");
        ajoutRegle("id","<affectation>","<affectation>::=id=nombre");
        ajoutRegle("id","<condition>","<condition>::=id<operateur>nombre");

        ajoutRegle("int","<liste_declarations>","<liste_declarations>::=<une_declaration><liste_declarations>");
        ajoutRegle("int","<une_declaration>","<une_declaration>::=<type>id");
        ajoutRegle("int","<type>","<type>::=int");

        ajoutRegle("float","<liste_declarations>","<liste_declarations>::=<une_declaration><liste_declarations>");
        ajoutRegle("float","<une_declaration>","<une_declaration>::=<type>id");
        ajoutRegle("float","<type>","<type>::=float");

        ajoutRegle("=","<operateur>","<operateur>::==");

        ajoutRegle("if","<liste_declarations>","<liste_declarations>::=vide");
        ajoutRegle("if","<liste_instructions>","<liste_instructions>::=<une_instruction><liste_instructions>");
        ajoutRegle("if","<une_instruction>","<une_instruction>::=<test>");
        ajoutRegle("if","<test>","<test>::=if <condition> <une_instruction> else <une_instruction>");

        ajoutRegle("<","<operateur>","<operateur>::=<");

        ajoutRegle(">","<operateur>","<operateur>::=>");

        ajoutRegle("}","<liste_declarations>","<liste_declarations>::=vide");
        ajoutRegle("}","<liste_instructions>","<liste_instructions>::=vide");
    }

    public boolean analyser(String mot) throws InconnuException{
        pile = new Stack<>();
        pile.push("$");
        pile.push("<programme>");

        Stack<String> Entree = preparer(mot);
        String hautEntree,hautPile;
        while( !Entree.isEmpty() && !pile.isEmpty() ){
            if( Entree.isEmpty() || pile.isEmpty() ){// si une pile est vide sans l'autre donc la chaine n est pas acceptée
                return false;
            }

            hautEntree = Entree.peek();
            hautPile = pile.peek();
            if(nonTerminaux.contains(hautPile) && tableAnalyse.get(hautEntree).containsKey(hautPile)){
                //si le haut de la pile est non terminal on cherche la regle correspondant dans la table d analyse et on l empile
                if(tableAnalyse.get(hautEntree).containsKey(hautPile)){
                    String regle = tableAnalyse.get(hautEntree).get(hautPile);
                    System.out.println(regle);
                    empilerRegle(regle);// on empile la regle
                }else{
                    //si on trouve pas de regle entre ce non terminal et le terminal dans la pile d entree donc la chaine n est pas acceptée
                    return false;
                }
            }else if(terminaux.contains(hautPile)){// si le haut de pile est un non terminal
                if(hautEntree == hautPile){
                    Entree.pop();
                    pile.pop();
                }else{// si le haut de pile non terminal et le haut de l entree ne se correspondent pas la mot n est pas accepté
                    return false;
                }
            }else{
                return false;
            }
        }
        //si la pile et l entree sont vide la chaine est acceptée
        return true;
    }

    private Stack<String> preparer(String mot) throws InconnuException{ //transforme le mot en pile et envoie une erreur s'il rencontre un symbole qu'il ne reconnait pas
        Stack<String> mot_pile = new Stack<>();
        mot_pile.push("$");//fond de pile $
        mot = mot.trim(); // on enleve les espace entourant la chaine
        while(mot.length() != 0){
            boolean trouve = false;
            for(String terminal:terminaux){//on compare la chaine avec les symboles terminaux
                int t = terminal.length();
                int taille_chaine = mot.length();
                if(mot.length() >= t && mot.substring(taille_chaine-t,taille_chaine).equals(terminal) ){//on commence par la fin de la chaine puisque le dernier symbole doit etre au fond de la pile
                    trouve = true;
                    mot_pile.push(terminal);
                    mot = mot.substring(0,taille_chaine - t);
                    break;
                }
            }
            mot = mot.trim();
            if(!trouve && mot.length()!= 0){// si on ne trouve aucun mot correspondant et la chaine n'est pas vide donc on a rencontré un symble qu'on ne connait pas
                throw new InconnuException();
            }
        }

        return mot_pile;
    }

    public boolean empilerRegle(String regle){
        String[] regle_tab = regle.split("::=");
        if(pile.peek().equals(regle_tab[0]) ){
            pile.pop();

            String cote_droit = regle_tab[1];
            boolean trouve;
            while(cote_droit.length() != 0){
                trouve = false;

                cote_droit = cote_droit.trim();
                for(String nonTerminal:nonTerminaux){
                    int t = nonTerminal.length();
                    int taille_cote_droit = cote_droit.length();
                    if(cote_droit.length() >= t && cote_droit.substring(taille_cote_droit - t,taille_cote_droit).equals(nonTerminal) ){

                        pile.push(nonTerminal);
                        cote_droit = cote_droit.substring(0,taille_cote_droit - t);
                        trouve = true;
                        break;
                    }
                }

                if(!trouve){
                    for(String terminal:terminaux){
                        int t = terminal.length();
                        int taille_cote_droit = cote_droit.length();
                        if(cote_droit.length() >= t && cote_droit.substring(taille_cote_droit - t,taille_cote_droit).equals(terminal) ){
                            if(terminal != "vide"){ //le vide on n'empile rien
                                pile.push(terminal);
                            }
                            cote_droit = cote_droit.substring(0,taille_cote_droit - t);

                            break;
                        }
                    }
                }

                cote_droit = cote_droit.trim();
            }

            return true;
        }else{
            return false;
        }
    }

    private static void ajoutRegle(String indexTerminal, String indexNonTerminal, String regle){
        try{
            if(terminaux.contains(indexTerminal) && nonTerminaux.contains(indexNonTerminal) && reglesProd.contains(regle)){
                if(tableAnalyse.containsKey(indexTerminal)){
                    if(tableAnalyse.get(indexTerminal).containsKey(indexNonTerminal)){
                        tableAnalyse.get(indexTerminal).put(indexNonTerminal,regle);
                    }else{
                        HashMap<String,String> m = new HashMap<>();
                        m.put(indexNonTerminal,regle);
                        tableAnalyse.get(indexTerminal).put(indexNonTerminal,regle);
                    }
                }else{
                    HashMap<String,String> m = new HashMap<>();
                    m.put(indexNonTerminal,regle);
                    tableAnalyse.put(indexTerminal,m);
                }
            }else{
                throw new InconnuException();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
