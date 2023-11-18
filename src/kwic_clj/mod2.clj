(ns kwic-clj.mod2
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defprotocol LineStorage
  (words [this idx])
  (line-count [this]))

(defrecord LineStore [lines]
  LineStorage
  (words [this idx] (str/split (get-in this [:lines idx]) #"\s+"))
  (line-count [this] (count (get this :lines))))

(defn input
  "Read input from stdin.
  Returns a tuple of [:ok line-store] or [:err e]
  where line-store is an instance of LineStore and
  e is an exception"
  []
  (with-open [rdr (io/reader *in*)]
    (try
      (doall [:ok (->LineStore (vec (line-seq rdr)))])
      (catch Exception e [:err e]))))

(defn circular-shifts
  "Converts lines from line store into circulr shifts.
  A circulr shift is a string that has been shifted by 
  some number of words"
  [line-store]
  (loop [line-idx 0
         shifts []]
    (if (= line-idx (line-count line-store))
      shifts
      (let [words (words line-store line-idx)
            line-shifts (map #(str/join " " (concat (drop % words) (take % words))) (range (count words)))]
        (recur (inc line-idx) (concat shifts line-shifts))))))

(defn alphabetize
  "Sorts circular shifts in alphabetical order."
  [shifts]
  (sort-by str/lower-case shifts))

(defn output
  "Prints each line in the sorted order"
  [sorted-shifts]
  (doseq [line sorted-shifts]
    (println line)))

(defn kwic []
  (let [[tag result] (input)]
    (case tag
      :ok (->> result
               circular-shifts
               alphabetize
               output)
      :err (println "An error occurred reading input: " (.getMessage result)))))
