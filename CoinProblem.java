/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mytwocents.ads.currency;

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


/**
 *
 * @author Sangeetha Ramadurai
 */
public class CoinProblem {
  public static final String USAGE_MESSAGE = "Specify comma delimited command line arguments: " +
                                             "e.g. Quarter,4,Dime,10,penny,100" +
                                             "(name of each denomination, the count required of that denomination to reach our target sum)";
                                      
  Float[] counts;
  String[] names;
  Integer[] denomValues;
  Map<Integer, String> dvalueName;
  Map<Integer, Float> dvalueCount;
  int targetAmount;
  ArrayList<Map<Integer, Integer>> results;
  ArrayList<Map<Integer, Integer>> uniqueAnswers;

  public CoinProblem() {
    counts = null;
    names = null;
    denomValues = null;
    dvalueName = null;
    dvalueCount = null;
    targetAmount = 0;
    results = null;
    uniqueAnswers = null;
  }

  public CoinProblem(String[] input) {
    initialize(input);
  }

  private void initialize(String[] input) {
    //Initialize
    int denom = 0;
    String[] inputList = input[0].split(",");
    int len = inputList.length/2;
    targetAmount = 0;
    counts = new Float[len];
    names = new String[len];
    denomValues = new Integer[len];
    dvalueName = new HashMap<Integer, String>();
    dvalueCount = new HashMap<Integer, Float>();

    if(inputList.length%2 != 0) {
      System.out.println(USAGE_MESSAGE);
      System.exit(1);
    }

    //Save the inputs for processing, find the targetAmount to sum to
    int newMax = 0;
    for(int ci = 1, ix = 0; ci < inputList.length && ix < len; ci += 2, ix++) {
      names[ix] = inputList[ci-1];
      counts[ix] = Float.parseFloat(inputList[ci]);
      if(counts[ix] <= 0) {
        System.out.println(USAGE_MESSAGE);
        System.exit(1);
      }
      newMax = Math.round(counts[ix]);
      targetAmount = (newMax > targetAmount) ? newMax : targetAmount;
    }

    //Calculate denominations from the given input
    for(int i = 0; i < counts.length; i++) {
      denom = Math.round(targetAmount/counts[i]);
      denomValues[i] = denom;
      dvalueName.put(denom, names[i]);
      dvalueCount.put(denom, counts[i]);
    }
  }

   public void denominationSubsetSum() {
    HashMap<Integer, Integer> subsetMap = null;
    ArrayList<HashMap<Integer, Integer>> uniqueAnswers = new ArrayList<HashMap<Integer, Integer>>();
    denominationSubsetSum(targetAmount, subsetMap, targetAmount, uniqueAnswers);

    //Print the results
    printUniqueCombinations(uniqueAnswers);
  }

  private void denominationSubsetSum(int amountToSumTo, HashMap<Integer, Integer> sMap,
                                     int targetAmount, ArrayList<HashMap<Integer, Integer>> uniqueAnswers) {
    boolean duplicate = false;
    if(amountToSumTo == 0 && sMap != null) {
      uniqueResults(uniqueAnswers, sMap);
    }
    else if(amountToSumTo != 0) {
      //use each denomination
      for(int denomIndex = 0; denomIndex < denomValues.length; denomIndex++) {
        int denom = denomValues[denomIndex];
        int count = (int)Math.floor(dvalueCount.get(Integer.valueOf(denom)));
        //Combinations to consider: consider number of coins of the choosen deomination
        //from 0 upto the max count
        for(int i = 0; i <= count; i++) {
          //Initialize the new subset map for this combination
          HashMap<Integer, Integer> subsetMap = new HashMap<Integer, Integer>();
          for(Integer it : denomValues) {
            subsetMap.put(it, 0);
          }
          
          //use the new subset map for this new combination
          subsetMap.put(denom, i);
          amountToSumTo -= denom*i;
          if (amountToSumTo != 0) {
            amountToSumTo = makeCoinChange(amountToSumTo, subsetMap, denomIndex);
          }
          denominationSubsetSum(amountToSumTo, subsetMap, targetAmount, uniqueAnswers);
          
          //reset amountToSumTo for the next comination of denominations
          amountToSumTo = targetAmount;
        }
      }
    } //else
  }

  private int makeCoinChange(int amountToSumTo, HashMap<Integer, Integer> sMap, int currDenomIndex) {
    //In both of the cases below, go through the denomValues[] piucking up all the other denominations
    //other than the current denomination index. Also make sure you don't exceed the denomValues[] array length
    int index = currDenomIndex;
    int loop = denomValues.length -1 ;
    int len = denomValues.length;
    int denomUsed = 0;

    if(index+1 == len) {
      index = -1;
    }

    if (amountToSumTo != 0) {
      while(++index < len && denomUsed++ < loop && amountToSumTo > 0) {
        int r = amountToSumTo/denomValues[index];
        amountToSumTo %= denomValues[index];
        sMap.put(denomValues[index], r);
        if(index+1 == len) {
          index = -1;
        }
      }
      //If current combination does not yield exact change for targetAmount,
      //make the current denomination index contribute to make the exact change for targetAmount
      if(amountToSumTo > 0) {
        int r = amountToSumTo/denomValues[currDenomIndex];
        int val = sMap.get(denomValues[currDenomIndex]);
        sMap.put(denomValues[currDenomIndex], r+val);
        amountToSumTo %= denomValues[currDenomIndex];
      }
    }
    return amountToSumTo;
  }

  private boolean uniqueTest(ArrayList<HashMap<Integer, Integer>> subListOfMaps, HashMap<Integer, Integer> sMap) {
    boolean duplicate = false;
    int matchingKVPairs = 0;
    for(HashMap<Integer, Integer> m : subListOfMaps) { //Navigate thru each Map
      matchingKVPairs = 0;
      duplicate = false;
      int mapsz = m.size();
      for(Integer it : m.keySet()) { //Navigate thru each key, value pairs
        if(m.get(it).equals(sMap.get(it))) {
          duplicate = true;
          matchingKVPairs++;
        }
      }
      duplicate = (duplicate && matchingKVPairs == mapsz) ? true : false;
      if(duplicate)
        return duplicate;
    }
    return duplicate;
  }

  private void uniqueResults(ArrayList<HashMap<Integer, Integer>> uniqueAnswers, HashMap<Integer, Integer> sMap) {
    boolean duplicate = false;
    if( !(duplicate = uniqueTest(uniqueAnswers, sMap)) ) {
      uniqueAnswers.add(sMap);
    }
  }

  private void printUniqueCombinations(ArrayList<HashMap<Integer, Integer>> uniqueAnswers) {
    for(int i = 0; i < names.length; i++) {
      System.out.print(names[i] + "         ");
    }
    System.out.println();
    for(HashMap<Integer, Integer> m : uniqueAnswers) {
      for(int i = 0; i < denomValues.length; i++) {
        System.out.print(m.get(denomValues[i]) + "              ");
      }
      System.out.println();
    }
    System.out.println("Count: " + uniqueAnswers.size());
  }

  public static void main(String[] args) {
    if(args.length  < 1)  {
      System.out.println(USAGE_MESSAGE);
      return;
    }
    CoinProblem p = new CoinProblem(args);
    p.denominationSubsetSum();
  } //main

} //Class CoinProblem
