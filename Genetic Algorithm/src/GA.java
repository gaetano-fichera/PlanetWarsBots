import com.google.gson.Gson;

import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class GA {
    private static final int POPULATION = 1;

    private static final int REPETITION_FOR_MAP = 1;
    private static final int MAPS_SIZE = 100;

    private static Individual theBest = null;

    public static void main(String[] args) {
        Instant startInstant, startOneIndividual, endOneIndividual;
        long durationOneIndividual;
        long durationPreviousOneIndividual = 0;
        long remainingTime;
        long remainingTimeSecs;
        long averageOneIndividual;

        String[] opponents = {
                //"BullyBot.jar",
                //"DualBot.jar",
                "ExGenebot.jar",
                //"Genebot.jar",
                //"ProspectorBot.jar",
                //"RageBot.jar",
                //"RandomBot.jar",
                //"ZerlingRush.jar",
                //"SwarmBot.jar",
                //"ChaoticOrder.jar",
                //"EvaBot.jar"
        };

        String[] maps = new String[MAPS_SIZE * REPETITION_FOR_MAP];

        int repetitionCounter = 0;
        int mapNameCounter = 0;
        int i = 0;
        while (i < maps.length) {
            if (repetitionCounter < REPETITION_FOR_MAP){
                maps[i] = "map" + String.valueOf(mapNameCounter + 1) + ".txt";
                //maps[i] = "map100.txt";
                repetitionCounter++;
                i++;
            }else{
                repetitionCounter = 0;
                mapNameCounter++;
            }
        }

        int sizeParams = 24;

        int lB = 0;
        int uB = 1;

        startInstant = Instant.now();

        int remainingRuns = POPULATION;

        Log("GA", "LF the Best", "Remaining Time Estimated: " + getTimeByMilliSec((long) (POPULATION * MAPS_SIZE * REPETITION_FOR_MAP * opponents.length) * 1000));

        for (int j = 0; j < POPULATION; j++){
            startOneIndividual = Instant.now();

            Individual one = new Individual(opponents, maps, sizeParams, lB, uB);

            one.random();
            one.calculateFitness();

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("Result_Gener_1_Eva_" + String.valueOf(j + 1) + ".txt"), "utf-8"))) {
                writer.write(one.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateTheBest(one);

            endOneIndividual = Instant.now();

            durationOneIndividual = ChronoUnit.MILLIS.between(startOneIndividual,endOneIndividual);

            if (j == 0) durationPreviousOneIndividual = durationOneIndividual;

            averageOneIndividual = (durationPreviousOneIndividual + durationOneIndividual) / 2;

            remainingRuns--;

            remainingTime = averageOneIndividual * remainingRuns;

            Log("GA", "LF the Best", "Individual N." + (j+1) + "of " + POPULATION + " Duration This Individual: " + String.valueOf(durationOneIndividual / 1000) + " secs" + " - " + "Remaining Time: " + getTimeByMilliSec(remainingTime) + " secs");
            Log("GA", "LF the Best", "Migliore Individuo Momentaneo con Victory: " + theBest.fitness + "%");

            durationPreviousOneIndividual = averageOneIndividual;

            printOnFile("Remaining_Time.txt", String.valueOf(remainingTime));
        }
    }

    public static void updateTheBest(Individual individual){
        String theBestString = "";
        if (theBest != null)
        {
            if (individual.fitness > theBest.fitness) {
                theBest = individual;
                Gson gson = new Gson();
                theBestString = gson.toJson(theBest);
                printOnFile("Gene_1_The_Best_Eva", theBestString);
            }
        }else {
            theBest = individual;
            Gson gson = new Gson();
            theBestString = gson.toJson(theBest);
            printOnFile("Gene_1_The_Best_Eva", theBestString);
        }


    }

    private static void printOnFile(String fileName, String toPrint){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "utf-8"))) {
            writer.write(toPrint);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Log(String classCaller, String functionCaller, String message) {
        System.out.println("(" + classCaller + ")" + " b:y " + functionCaller + " : " + message);
    }

    private static String getTimeByMilliSec(long milliSeconds){
        long seconds = milliSeconds / 1000;
        long secondsLeft = seconds % 60;
        long minutes = seconds / 60;
        long minutesLeft = minutes % 60;
        long hours = minutes / 60;
        return hours + "h:" + minutesLeft + "m:" + secondsLeft + "s";
    }
}
