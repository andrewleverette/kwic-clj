(ns kwic-clj.mod2
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defprotocol LineStorage
  (words [this idx] "Returns a list of words for a given line index")
  (line-count [this] "Returns the number of lines in the line storage"))

(defrecord LineStore [lines]
  LineStorage
  (words [this idx] (str/split (get-in this [:lines idx]) #"\s+"))
  (line-count [this] (count (get this :lines))))

(defprotocol CircularShifter
  (initialize [this line-store] "Converts lines from line store into circulr shifts"))

(defprotocol Alphabetizer
  (alphabetize [this] "Returns a list of lines sorted alphabetically"))

(defprotocol Output
  (output [this] "Prints lines to stdout"))

(defrecord CircularShifts [shifts]
  CircularShifter
  (initialize [this line-store]
    (loop [line-idx 0
           circular-shifts []]
      (if (= line-idx (line-count line-store))
        (assoc this :shifts circular-shifts)
        (let [words (words line-store line-idx)
              line-shifts (map #(str/join " " (concat (drop % words) (take % words))) (range (count words)))]
          (recur (inc line-idx) (concat circular-shifts line-shifts))))))
  Alphabetizer
  (alphabetize [this] (assoc this :shifts (sort-by str/lower-case shifts)))
  Output
  (output [this]
    (doseq [line (get this :shifts)]
      (println line))))

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
  (initialize (->CircularShifts []) line-store))

(defn kwic []
  (let [[tag result] (input)]
    (case tag
      :ok (->> result
               circular-shifts
               alphabetize
               output)
      :err (println "An error occurred reading input: " (.getMessage result)))))
