/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.example.sparkwordcount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.SparkConf;
import scala.Tuple2;

public class JavaWordCount {
  public static void main(String[] args) {
    JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("Spark Count"));
    
    // split each document into words
    JavaRDD<String> tokenized = sc.textFile(args[0]).flatMap(
      new FlatMapFunction<String, String>() {
        @Override
        public Iterable<String> call(String s) {
          return Arrays.asList(s.split(" "));
        }
      }
    );
    
    // count the occurrence of each word
    JavaPairRDD<String, Integer> counts = tokenized.mapToPair(
      new PairFunction<String, String, Integer>() {
        @Override
        public Tuple2<String, Integer> call(String s) {
          return new Tuple2<String, Integer>(s, 1);
        }
      }
    ).reduceByKey(
      new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer call(Integer i1, Integer i2) {
          return i1 + i2;
        }
      }, 1		//number of reducers = 1
    );

    counts.sortByKey(true).saveAsTextFile(args[1]); 
    System.exit(0);
  }
}