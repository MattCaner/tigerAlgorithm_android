package com.example.proj2;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Clusters {



    ArrayList<Vec2> points;

    public Clusters(){

    }

    static class IndexAndDistance{
        int index;
        double distance;
        IndexAndDistance(int index, double distance){
            this.index = index;
            this.distance = distance;
        }
    }

    static class Cmp implements Comparator<IndexAndDistance>
    {
        public int compare(IndexAndDistance iad1, IndexAndDistance iad2)
        {
            return Double.compare(iad1.distance, iad2.distance);
        }
    }

    public void load(ArrayList<Vec2> points){
        this.points = points;
    }

    public ArrayList<Integer> performClustering (int clusterNumbers,double ts, double tc, double alpha, int cr, double M0, double S, double bias){

        int searchNumber = (int)(points.size() * ts);
        int corrNumber = (int)(points.size() * tc);

        ArrayList<Integer> result = new ArrayList<Integer>();
        for(int i = 0; i<this.points.size(); i++){
            result.add(-1);
        }

        Vec2 [] individualPositions = new Vec2[clusterNumbers];

        // randomize positions of individuals:
        for(int i = 0; i<clusterNumbers; i++){
            int randomInt = ThreadLocalRandom.current().nextInt(0, points.size() + 1);
            while(result.get(randomInt) != -1){
                randomInt = ThreadLocalRandom.current().nextInt(0, points.size() + 1);
            }
            result.set(randomInt,i);
            individualPositions[i] = points.get(i);
        }

        // perform main loop:

        int marked = 0;
        while(marked < points.size()-clusterNumbers) {
            for (int individual = 0; individual < clusterNumbers; individual++) {
                //calculate distances:
                ArrayList<IndexAndDistance> iads = new ArrayList<IndexAndDistance>();
                int unmarked = 0;
                for (int i = 0; i < points.size(); i++) {
                    if (result.get(i) == -1) {
                        iads.add(new IndexAndDistance(i, individualPositions[individual].distanceTo(points.get(i))));
                        unmarked++;
                    }
                }

                if (iads.size() == 0) break;

                double unmarkedRatio = (double) unmarked / (double) iads.size();
                double base = M0 + S * (unmarkedRatio);

                Collections.sort(iads, new Cmp());
                //leave only a percent:
                if (iads.size() > searchNumber)
                    iads.subList(searchNumber, iads.size() - 1).clear();

                double[] weights = new double[iads.size()];

                for (int i = 0; i < iads.size(); i++) {
                    weights[i] = Math.pow(base, 1. / (bias + iads.get(i).distance));
                }

                //perform a randomization:
                double sum = 0;
                for (double d : weights) sum += d;
                double randomNumber = sum * ThreadLocalRandom.current().nextDouble();
                double dCounter = 0.;
                int iCounter = 0;
                while (dCounter < randomNumber) {
                    dCounter += weights[iCounter];
                    iCounter++;
                }
                int pointToJumpToIndex = iCounter >= iads.size() ? iads.get(iads.size()-1).index : iads.get(iCounter).index;

                //mark:
                result.set(pointToJumpToIndex, individual);
                Log.println(Log.ASSERT, "lelz", "pTJTI = " + pointToJumpToIndex);
                individualPositions[individual] = points.get(pointToJumpToIndex);
                marked++;
                Log.println(Log.ASSERT, "Meh", "marked = " + marked);
            }

        }

        // corrections:
        for(int crId = 0; crId < cr; crId++){

            ArrayList<Integer> newResult = new ArrayList<>(result);

            for(int p = 0; p<points.size(); p++){
                ArrayList<IndexAndDistance> iads = new ArrayList<IndexAndDistance>();
                int unmarked = 0;
                for(int i = 0; i<points.size(); i++){
                    if(i!=p){
                        iads.add(new IndexAndDistance(i, points.get(p).distanceTo(points.get(i))));
                        unmarked++;
                    }
                }
                Collections.sort(iads,new Cmp());
                iads.subList(corrNumber,iads.size()-1).clear();

                int [] ofCluster = new int[clusterNumbers];
                double [] dClusters = new double[clusterNumbers];
                double [] distances = new double[clusterNumbers];

                for(int c = 0; c < clusterNumbers; c++){
                    for(int i = 0; i<iads.size(); i++){
                        if(result.get(iads.get(i).index) == c){
                            ofCluster[c] ++;
                            distances[c] += iads.get(i).distance;
                        }
                    }
                    distances[c] /= ofCluster[c];
                }

                int numberMax = 0;
                double distancesMax = 0.;
                for(int c = 0; c<clusterNumbers; c++){
                    if(ofCluster[c] > numberMax) numberMax = ofCluster[c];
                    if(distances[c] > distancesMax) distancesMax = distances[c];
                }

                for(int c = 0; c<clusterNumbers; c++){
                    dClusters[c] = (double)(ofCluster[c]) / numberMax;
                    distances[c] /= distancesMax;
                }

                double [] weights = new double [clusterNumbers];

                for(int c = 0; c<clusterNumbers; c++){
                    weights[c] = distances[c] / (1.2 - dClusters[c]);
                    if(c != result.get(p)) weights[c] *= alpha;
                }

                int maxInd = 0;
                for(int c = 0; c<clusterNumbers; c++){
                    if(weights[c] > weights[maxInd]) maxInd = c;
                }

                newResult.set(p,maxInd);

            }

            for(int i = 0; i<result.size(); i++){
                result.set(i,newResult.get(i));
            }

        }


        return result;
    }

}
