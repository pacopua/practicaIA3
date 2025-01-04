package problem_generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class problem_generator {

    private static boolean[] tiene_predecesor; // si el contenido i tiene un predecesor
    private static boolean[] se_ha_de_ver; // si el contenido i se ha de ver

    private static Random random = new Random();

    public static void main(String[] args) {
        int minDias = 1;
        int maxDias = 0;
        int minContenidos = 1;
        int maxContenidos = 0;
        System.out.println("Desea que pueda haber mas de un contenido por dia?\n Si/No");
        String respuesta = System.console().readLine();
        while (!respuesta.equals("Si") && !respuesta.equals("No")) {
            System.out.println("Por favor, introduzca una respuesta valida");
            respuesta = System.console().readLine();
        }
        if (respuesta.equals("No")) {
            while (maxDias < minDias) {
                try {
                    System.out.println("Elija el numero minimo y maximo de dias\n" + "minimo: ");
                    minDias = Integer.parseInt(System.console().readLine());
                    System.out.println("maximo: ");
                    maxDias = Integer.parseInt(System.console().readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, introduzca un numero valido");
                    minDias = 1;
                    maxDias = 0;
                }

                if (maxDias < minDias) {
                    System.out.println("El numero maximo de dias no puede ser menor que el minimo");
                }
            }

            //hacemos lo mismo pero con los contenidos
            while (maxContenidos < minContenidos || minContenidos < minDias) {
                try {
                    System.out.println("Elija el numero minimo y maximo de contenidos\n" + "minimo: ");
                    minContenidos = Integer.parseInt(System.console().readLine());
                    System.out.println("maximo: ");
                    maxContenidos = Integer.parseInt(System.console().readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, introduzca un numero valido");
                    minContenidos = 1;
                    maxContenidos = 0;
                }

                if (maxContenidos < minContenidos) {
                    System.out.println("El numero maximo de contenidos no puede ser menor que el minimo");
                } else if (minContenidos < minDias) {
                    System.out.println("El numero minimo de contenidos no puede ser menor que el numero minimo de dias");
                }
            }
        }
        else {
            boolean repetir = true;
            while (repetir) {
                try {
                    System.out.println("Elija el numero minimo y maximo de dias\n" + "minimo: ");
                    minDias = Integer.parseInt(System.console().readLine());
                    System.out.println("maximo: ");
                    maxDias = Integer.parseInt(System.console().readLine());
                    repetir = false;
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, introduzca un numero valido");
                    //repetir = true;
                }

                if (maxDias < minDias) {
                    System.out.println("El numero maximo de dias no puede ser menor que el minimo");
                }
            }

            //hacemos lo mismo pero con los contenidos
            repetir = true;
            while (repetir) {
                try {
                    System.out.println("Elija el numero minimo y maximo de contenidos\n" + "minimo: ");
                    minContenidos = Integer.parseInt(System.console().readLine());
                    System.out.println("maximo: ");
                    maxContenidos = Integer.parseInt(System.console().readLine());
                    repetir = false;
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, introduzca un numero valido");
                }

                if (maxContenidos < minContenidos) {
                    System.out.println("El numero maximo de contenidos no puede ser menor que el minimo");
                } else if (minContenidos < minDias) {
                    System.out.println("El numero minimo de contenidos no puede ser menor que el numero minimo de dias");
                }
            }
        }
        //Pair<Integer, Integer> dia = new Pair<>(minDias, maxDias);
        //Pair<Integer, Integer> contenido = new Pair<>(minContenidos, maxContenidos);
        generarPDDL("nivel-basico/random_problem1.pddl", minDias, maxDias, minContenidos, maxContenidos);
    }

    public static void
    generarPDDL(String filename, int minDia, int maxDia, int minContenido, int maxContenido) {
        int numDays = random.ints(minDia, maxDia + 1).findFirst().getAsInt();
        int numContents = random.ints(minContenido, maxContenido + 1).findFirst().getAsInt();

        tiene_predecesor = new boolean[numContents];
        se_ha_de_ver = new boolean[numContents];
        for(int i = 0; i < numContents; i++){
            tiene_predecesor[i] = false;
        }
        for(int i = 0; i < numContents; i++){
            se_ha_de_ver[i] = false;
        }

        List<String> contents = generateRandomStrings(numContents, "Content");
        List<String> days = generateRandomStrings(numDays, "Day");

        try (FileWriter file = new FileWriter(filename)) {
            file.write("(define (problem PROBLEMA_NIVELBASICO)\n");
            file.write("    (:domain DOMAIN_NIVELBASICO)\n");
            file.write("    (:objects\n");
            for (String content : contents) {
                file.write("        " + content + " - contenido\n");
            }
            for (String day : days) {
                file.write("        " + day + " - dia\n");
            }
            file.write("    )\n");
            file.write("    (:init\n");

            for (int i = 0; i < numContents; i++) {
                String content = contents.get(i);
                if (random.nextBoolean()) {
                    file.write("        (quierever " + content + ")\n");
                    se_ha_de_ver[i] = true;
                }
            }

            for (int i = 0; i < numContents - 1; i++) {
                if (random.nextBoolean() && !tiene_predecesor[i]) {
                    file.write("        (predecesor " + contents.get(i) + " " + contents.get(i + 1) + ")\n");
                    tiene_predecesor[i+1] = true;
                }
            }

            for (String content : contents) {
                file.write("        (not (havisto " + content + "))\n");
            }
            int z = 1;
            for (String day : days) {
                file.write("        (= (contenidosAgendados " + day + ") 0)\n");
                file.write("        (= (orden " + day + ") " + z + ")\n");
                ++z;
                //"(= (contenidosAgendados lunes) 0)"
                //file.write("        (not (lleno " + day + "))\n");
            }
            file.write("        (= (maxContenidosPorDia) 1)\n");
            file.write("        )\n");
            /*
                (:goal
                    (forall (?c - contenido)
                    (imply
                (quiereVer ?c)
                (exists (?d - dia)
                    (agendado ?c ?d)
                )
            )
        )
    )
             */

            file.write("    (:goal\n");
            file.write("    (forall (?c - contenido)\n");
            file.write("        (imply\n");
            file.write("            (quiereVer ?c)\n");
            file.write("            (exists (?d - dia)\n");
            file.write("                (agendado ?c ?d))))))\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> generateRandomStrings(int count, String prefix) {
        List<String> strings = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            strings.add(prefix + i);
        }
        return strings;
    }
}