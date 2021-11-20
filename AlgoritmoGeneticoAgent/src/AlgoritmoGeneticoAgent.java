import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import java.util.ArrayList;
import java.util.Random;

public class AlgoritmoGeneticoAgent extends Agent {

    protected void setup() {
        System.out.println("Agent "+getLocalName()+" started.");
        addBehaviour(new MyOneShotBehaviour());
    }

    private static int tamPoblacion = 5;
    private static int Generacion = 0, contBoolB0 = 0, contBoolB1 = 0;
    private static int generacionParaB0 = 0;
    private static int generacionParaB1 = 0;
    private static boolean bandB0Fin = false;
    private static boolean bandB1Fin = false;
    private static ArrayList<AlgoritmoGeneticoAgent.Individuo> generacionB0;
    private static ArrayList<AlgoritmoGeneticoAgent.Individuo> generacionB1;
    private static ArrayList<AlgoritmoGeneticoAgent.Individuo> hijosB0;
    private static ArrayList<AlgoritmoGeneticoAgent.Individuo> hijosB1;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static class Individuo
    {
        private long Genoma;
        private float Fitness;
        private float Probabilidad;
        private float ProbabilidadAcumulada;

        public String DecimalToBinario(boolean b0, long num)
        {
            if(num<0) //absoluto
                num = num * -1;
            long n = num;
            String bin = "";

            if(n == 0)
            {
                if(b0 == true)
                    return "00000000";
                else
                    return "000000";
            }

            while(n>0)
            {
                if(n%2 == 0)
                    bin = "0" + bin;
                else
                    bin = "1" + bin;
                n = n/2;
            }

            if(b0 == true)
                while (bin.length() < 8) //FORZAR 8 bitAMutars
                    bin = "0" + bin;
            else
                while (bin.length() < 5) //FORZAR 5 bitAMutars
                    bin = "0" + bin;

                return bin;
        }

        public long BinarioToDecimal(String binario)
        {
            long decimal = 0;
            int posicion = 0;
            for (int x = binario.length() - 1; x >= 0; x--)
            {
                short digito = 1;
                if (binario.charAt(x) == '0')
                    digito = 0;
                double multiplicador = Math.pow(2, posicion);
                decimal += digito * multiplicador;
                posicion++;
            }
            return decimal;
        }
    }

    public static int ObtenerRandomEntero(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static void CrearPrimeraGeneracion()
    {
        //Se inicializan las las listas
        generacionB0 = new ArrayList<>();
        hijosB0 = new ArrayList<>();
        generacionB1 = new ArrayList<>();
        hijosB1 = new ArrayList<>();

        for (int i = 0; i < tamPoblacion; i++)
        {
            AlgoritmoGeneticoAgent.Individuo b0 = new AlgoritmoGeneticoAgent.Individuo();
            b0.Genoma = ObtenerRandomEntero(1, 256); //B0 1<168<255
            AlgoritmoGeneticoAgent.Individuo b1 = new AlgoritmoGeneticoAgent.Individuo();
            b1.Genoma = ObtenerRandomEntero(1, 32); //B1 1<23<31
            generacionB0.add(b0);
            generacionB1.add(b1);
        }
    }

    public static void MetodoBurbuja(){
        //Metodo de burbuja
        boolean sw = false;
        while (!sw)
        {
            sw = true;
            for (int i = 1; i < tamPoblacion; i++)
            {
                if(bandB0Fin == false)
                    if (generacionB0.get(i).Fitness > generacionB0.get(i - 1).Fitness)
                    {
                        AlgoritmoGeneticoAgent.Individuo b0 = generacionB0.get(i);
                        generacionB0.set(i, generacionB0.get(i - 1));
                        generacionB0.set(i - 1, b0);
                        sw = false;
                    }

                if(bandB1Fin == false)
                    if (generacionB1.get(i).Fitness > generacionB1.get(i - 1).Fitness)
                    {
                        AlgoritmoGeneticoAgent.Individuo b1 = generacionB1.get(i);
                        generacionB1.set(i, generacionB1.get(i - 1));
                        generacionB1.set(i - 1, b1);
                        sw = false;
                    }
            }
        }
    }

    static double FuncionFitness(boolean mayor, float x, int cualBeta)
    {
        int beta;
        if(cualBeta == 0)
            beta = 168;
        else
            beta = 23;
        if(mayor == false)
            return x/beta;
        else
            return beta/x;
    }

    public static void DeterminarFitness(){
        boolean mayor;
        for (int i = 0; i < tamPoblacion; i++)
        {
            if(bandB0Fin == false)
            {
                mayor = true;
                AlgoritmoGeneticoAgent.Individuo b0 = generacionB0.get(i);
                if (generacionB0.get(i).Genoma < 168)
                    mayor = false;
                b0.Fitness = (float) (FuncionFitness(mayor, generacionB0.get(i).Genoma, 0));
                generacionB0.set(i, b0);
            }

            if(bandB1Fin == false)
            {
                mayor = true;
                AlgoritmoGeneticoAgent.Individuo b1 = generacionB1.get(i);
                if(generacionB1.get(i).Genoma < 23)
                    mayor = false;
                b1.Fitness = (float) (FuncionFitness(mayor, generacionB1.get(i).Genoma, 1));
                generacionB1.set(i, b1);
            }
        }
    }

    public static void ImprimirValores()
    {
        System.out.println("Generacion no. " + Generacion);
        StringBuilder s = new StringBuilder();
        System.out.println("BETA 0:");
        for (int i = 0; i < tamPoblacion; i++)
        {
            if(generacionB0.get(i).Fitness == 1f || bandB0Fin == true)
                System.out.println("(" + i + ")" + ANSI_GREEN + generacionB0.get(i).Genoma + ANSI_RESET + " p:" +  ANSI_GREEN + generacionB0.get(i).Fitness + ANSI_RESET);
            else
                System.out.println("(" + i + ")" + generacionB0.get(i).Genoma + " p:" + generacionB0.get(i).Fitness);
        }
        System.out.println("BETA 1:");
        for (int i = 0; i < tamPoblacion; i++)
        {
            if(generacionB1.get(i).Fitness == 1f || bandB1Fin == true)
                System.out.println("(" + i + ")" + ANSI_GREEN + generacionB1.get(i).Genoma + ANSI_RESET + " p:" +  ANSI_GREEN + generacionB1.get(i).Fitness + ANSI_RESET);
            else
                System.out.println("(" + i + ")" + generacionB1.get(i).Genoma + " p:" + generacionB1.get(i).Fitness);
        }
        System.out.println("\n");
        Generacion++;
    }

    static void Combinacion()
    {
        float Fitness, FitnessAcumBeta0 = 0, FitnessAcumIndBeta0 = 0, FitnessAcumBeta1 = 0, FitnessAcumIndBeta1 = 0, buff0=0, buff1 = 1;

        //se determina el acumulado del porcentaje
        for(int i = 0; i < tamPoblacion; i++)
        {
            if(bandB0Fin == false)
            {
                Fitness = generacionB0.get(i).Fitness;
                FitnessAcumBeta0 = FitnessAcumBeta0 + Fitness;
            }

            if(bandB1Fin == false)
            {
                Fitness = generacionB1.get(i).Fitness;
                FitnessAcumBeta1 = FitnessAcumBeta1 + Fitness;
            }
        }
        for(int i = 0; i < tamPoblacion; i++)
        {
            if(bandB0Fin == false)
            {
                AlgoritmoGeneticoAgent.Individuo beta0 = generacionB0.get(i);
                Fitness = generacionB0.get(i).Fitness;
                FitnessAcumIndBeta0 = Fitness / FitnessAcumBeta0;
                beta0.Probabilidad = FitnessAcumIndBeta0;
                buff0 = FitnessAcumIndBeta0 + buff0;
                beta0.ProbabilidadAcumulada = buff0;
                generacionB0.set(i, beta0);
            }

            if(bandB1Fin == false)
            {
                AlgoritmoGeneticoAgent.Individuo beta1 = generacionB1.get(i);
                Fitness = generacionB1.get(i).Fitness;
                FitnessAcumIndBeta1 = Fitness / FitnessAcumBeta1;
                beta1.Probabilidad = FitnessAcumIndBeta1;
                buff1 = FitnessAcumIndBeta1 + buff1;
                beta1.ProbabilidadAcumulada = buff1;
                generacionB1.set(i, beta1);
            }
        }

        Random r = new Random();
        float beta0RandRuletaNum = 0 + r.nextFloat() * (generacionB0.get(generacionB0.size()-1).ProbabilidadAcumulada - 0);
        float beta0RandRuletaNum2 = 0 + r.nextFloat() * (generacionB0.get(generacionB0.size()-1).ProbabilidadAcumulada - 0);
        float beta1RandRuletaNum = 0 + r.nextFloat() * (generacionB1.get(generacionB1.size()-1).ProbabilidadAcumulada - 0);
        float beta1RandRuletaNum2 = 0 + r.nextFloat() * (generacionB1.get(generacionB1.size()-1).ProbabilidadAcumulada - 0);

        float beta0BufferRuleta = 0, beta0ProbabilidadActual=0;
        float beta1BufferRuleta = 0, beta1ProbabilidadActual=0;
        String beta0Padre1 = "00000000";
        String beta0Padre2 = "00000000";
        String beta1Padre1 = "00000";
        String beta1Padre2 = "00000";

        long obtenerGenoma;

        boolean beta0Padre1Listo = false;
        boolean beta0Padre2Listo = false;
        boolean beta1Padre1Listo = false;
        boolean beta1Padre2Listo = false;
        for(int i = 0; i < tamPoblacion; i++)
        {
            AlgoritmoGeneticoAgent.Individuo b0 = generacionB0.get(i);
            AlgoritmoGeneticoAgent.Individuo b1 = generacionB1.get(i);

            if(bandB0Fin == false)
            {
                beta0ProbabilidadActual = generacionB0.get(i).ProbabilidadAcumulada;
                if ((beta0BufferRuleta < beta0RandRuletaNum && beta0RandRuletaNum <= beta0ProbabilidadActual) && beta0Padre1Listo == false)
                {
                    obtenerGenoma = generacionB0.get(i).Genoma;
                    beta0Padre1 = b0.DecimalToBinario(true, obtenerGenoma);
                    beta0Padre1Listo = true;
                } else
                {
                    if ((beta0BufferRuleta < beta0RandRuletaNum2 && beta0RandRuletaNum2 <= beta0ProbabilidadActual) && beta0Padre2Listo == false)
                    {
                        obtenerGenoma = generacionB0.get(i).Genoma;
                        beta0Padre2 = b0.DecimalToBinario(true, obtenerGenoma);
                        beta0Padre2Listo = true;
                    } else
                        beta0BufferRuleta = beta0ProbabilidadActual;
                }
            }

            if(bandB1Fin == false)
            {
                beta1ProbabilidadActual = generacionB1.get(i).ProbabilidadAcumulada;
                if ((beta1BufferRuleta < beta1RandRuletaNum && beta1RandRuletaNum <= beta1ProbabilidadActual) && beta1Padre1Listo == false)
                {
                    obtenerGenoma = generacionB1.get(i).Genoma;
                    beta1Padre1 = b1.DecimalToBinario(false, obtenerGenoma);
                    beta1Padre1Listo = true;
                }
                else
                {
                    if ((beta1BufferRuleta < beta1RandRuletaNum2 && beta1RandRuletaNum2 <= beta1ProbabilidadActual) && beta1Padre2Listo == false)
                    {
                        obtenerGenoma = generacionB1.get(i).Genoma;
                        beta1Padre2 = b1.DecimalToBinario(false, obtenerGenoma);
                        beta1Padre2Listo = true;
                    }
                    else
                        beta1BufferRuleta = beta1ProbabilidadActual;
                }
            }
        }
        int contador;
        StringBuilder beta0Hijo1 = new StringBuilder(beta0Padre1.length());
        StringBuilder beta0Hijo2 = new StringBuilder(beta0Padre1.length());
        if(bandB0Fin == false)
        {
            int beta0Mutacion = ObtenerRandomEntero(0, beta0Padre1.length());
            for (contador = 0; contador < beta0Mutacion; contador++)
            {
                beta0Hijo1.append(beta0Padre2.charAt(contador));
                beta0Hijo2.append(beta0Padre1.charAt(contador));
            }
            for (int cont2 = contador; cont2 < beta0Padre1.length(); cont2++)
            {
                beta0Hijo1.append(beta0Padre1.charAt(cont2));
                beta0Hijo2.append(beta0Padre2.charAt(cont2));
            }
        }

        StringBuilder beta1Hijo1 = new StringBuilder(beta1Padre1.length());
        StringBuilder beta1Hijo2 = new StringBuilder(beta1Padre1.length());
        if(bandB1Fin == false)
        {
            int beta1Mutacion = ObtenerRandomEntero(0, beta1Padre1.length());
            for (contador = 0; contador < beta1Mutacion; contador++)
            {
                beta1Hijo1.append(beta1Padre2.charAt(contador));
                beta1Hijo2.append(beta1Padre1.charAt(contador));
            }
            for (int cont2 = contador; cont2 < beta1Padre1.length(); cont2++)
            {
                beta1Hijo1.append(beta1Padre1.charAt(cont2));
                try {
                    beta1Hijo2.append(beta1Padre2.charAt(cont2));
                }
                catch(Exception e)
                {
                    System.out.println("B1 Hijo1: " + beta1Hijo1);
                    System.out.println("B1 Hijo1 TAM: " + beta1Hijo1.length());
                    System.out.println("B1 Hijo2: " + beta1Hijo2);
                    System.out.println("B1 Hijo2 TAM: " + beta1Hijo2.length());
                    System.out.println("B1 Padre1: " + beta1Padre1);
                    System.out.println("B1 Padre1 TAM: " + beta1Padre1.length());
                    System.out.println("B1 Padre2: " + beta1Padre1);
                    System.out.println("B1 Padre2 TAM: " + beta1Padre1.length());
                }
            }
        }

        if(bandB0Fin == false) {
            AlgoritmoGeneticoAgent.Individuo b0H1 = new AlgoritmoGeneticoAgent.Individuo();
            b0H1.Genoma = b0H1.BinarioToDecimal(String.valueOf(beta0Hijo1));
            hijosB0.add(b0H1);
            AlgoritmoGeneticoAgent.Individuo b0H2 = new AlgoritmoGeneticoAgent.Individuo();
            b0H2.Genoma = b0H2.BinarioToDecimal(String.valueOf(beta0Hijo2));
            hijosB0.add(b0H2);
        }

        if(bandB1Fin == false) {
            AlgoritmoGeneticoAgent.Individuo b1H1 = new AlgoritmoGeneticoAgent.Individuo();
            b1H1.Genoma = b1H1.BinarioToDecimal(String.valueOf(beta1Hijo1));
            hijosB1.add(b1H1);
            AlgoritmoGeneticoAgent.Individuo b1H2 = new AlgoritmoGeneticoAgent.Individuo();
            b1H2.Genoma = b1H2.BinarioToDecimal(String.valueOf(beta1Hijo2));
            hijosB1.add(b1H2);
        }
    }

    static void Mutacion(){
        long GenomaHijo;
        StringBuilder StringGenoma;
        float ratioMutacion = 0.1f;
        Random r = new Random();
        for(int i=0; i< tamPoblacion; i++)
        {
            float randRatioMutacion;
            if(bandB0Fin == false)
            {
                randRatioMutacion = 0 + r.nextFloat() * (1 - 0);
                if (randRatioMutacion < ratioMutacion)
                {
                    AlgoritmoGeneticoAgent.Individuo hb0 = hijosB0.get(i);
                    GenomaHijo = hijosB0.get(i).Genoma;
                    StringGenoma = new StringBuilder(hb0.DecimalToBinario(true, GenomaHijo));
                    int rand = ObtenerRandomEntero(0, StringGenoma.length());
                    char bitAMutar = StringGenoma.charAt(rand);
                    if (bitAMutar == '1')
                        StringGenoma.setCharAt(rand, '0');
                    else
                        StringGenoma.setCharAt(rand, '1');
                    hb0.Genoma = hb0.BinarioToDecimal(String.valueOf(StringGenoma)); //B0 255
                }
            }
            if(bandB1Fin == false) {
                randRatioMutacion = 0 + r.nextFloat() * (1 - 0);
                if (randRatioMutacion < ratioMutacion) {
                    AlgoritmoGeneticoAgent.Individuo hb1 = hijosB1.get(i);
                    GenomaHijo = hijosB1.get(i).Genoma;
                    StringGenoma = new StringBuilder(hb1.DecimalToBinario(false, GenomaHijo));
                    int rand = ObtenerRandomEntero(0, StringGenoma.length());
                    char bitAMutar = StringGenoma.charAt(rand);
                    if (bitAMutar == '1')
                        StringGenoma.setCharAt(rand, '0');
                    else
                        StringGenoma.setCharAt(rand, '1');
                    hb1.Genoma = hb1.BinarioToDecimal(String.valueOf(StringGenoma)); //B0 255
                }
            }
        }
    }

    static void ActualizarGeneracion()
    {
        if(bandB0Fin == false) {
            generacionB0 = new ArrayList<>();
            for (int i = 0; i < tamPoblacion; i++)
                generacionB0.add(hijosB0.get(i));
            hijosB0 = new ArrayList<>();
        }

        if(bandB1Fin == false) {
            generacionB1 = new ArrayList<>();
            for (int i = 0; i < tamPoblacion; i++)
                generacionB1.add(hijosB1.get(i));
            hijosB1 = new ArrayList<>();
        }
    }

    private class MyOneShotBehaviour extends OneShotBehaviour {

        public void action() {
            CrearPrimeraGeneracion();
            while(true)
            {
                DeterminarFitness();
                MetodoBurbuja();
                ImprimirValores();
                int j =0, contGenomaBeta0 = 0, contGenomaBeta1 = 0;
                while(j < generacionB0.size())
                {
                    if(generacionB0.get(j).Genoma == 168)
                    {
                        contGenomaBeta0++;
                    }
                    if(generacionB1.get(j).Genoma == 23)
                    {
                        contGenomaBeta1++;
                    }
                    j++;
                }
                if(contGenomaBeta0 == generacionB0.size())
                {
                    bandB0Fin = true;
                    if(contBoolB0 == 0)
                        generacionParaB0 = Generacion;
                    contBoolB0++;
                }
                if(contGenomaBeta1 == generacionB1.size())
                {
                    bandB1Fin = true;
                    if(contBoolB1 == 0)
                        generacionParaB1 = Generacion;
                    contBoolB1++;
                }
                if(bandB0Fin == true && bandB1Fin == true)
                {
                    break;
                }
                else
                {
                    if(bandB0Fin == false)
                    {
                        while (hijosB0.size() < generacionB0.size()) {
                            Combinacion();
                        }
                    }
                    else if(bandB1Fin == false)
                    {
                        while (hijosB1.size() < generacionB1.size()) {
                            Combinacion();
                        }
                    }
                    Mutacion();
                    ActualizarGeneracion();
                }
            }
            System.out.println("Beta0 y Beta 1 encontrados satisfactoriamente");
            System.out.println("- Beta0 encontrado en generacion no. " + (generacionParaB0-1));
            System.out.println("- Beta1 encontrado en generacion no. " + (generacionParaB1-1));
        }

        /*
        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }*/
    }    // END of inner class ...Behaviour
}
