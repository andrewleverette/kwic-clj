(ns kwic-clj.mod1
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn input
  "Read input from stdin.
  Returns a tuple of [:ok lines] or [:err e] where lines is a vector of
  of each line split on whitespace and e is the exception."
  []
  (with-open [rdr (io/reader *in*)]
    (try
      (doall [:ok (vec (map #(str/split % #"\s+") (line-seq rdr)))])
      (catch Exception e [:err e]))))

(defn circular-shifts
  "Convert lines from input to a list of circular shifts.
  A cirular shift is a tuple of the line index and the index of the
  first word in the shifted line."
  [lines]
  (letfn [(index-words [line-idx words]
            (map (fn [idx] [line-idx idx]) (range (count words))))]
    (->> lines
         (map-indexed index-words)
         (apply concat))))

(defn shift->line [lines [line-idx idx]]
  (let [line (lines line-idx)]
    (str/join " " (concat (drop idx line) (take idx line)))))

(defn alphabetize
  "Sort cirular shifts in alpabetical order.
  Uses original lines to determine the sort order and
  returns the shifts in the corresponding order."
  [lines shifts]
  (->> shifts
       (map (fn [shift] [shift (shift->line lines shift)]))
       (sort-by (comp str/lower-case second))
       (map first)))

(defn output
  "Take a list of sorted shifts and prints each line stdout in the sorted order."
  [lines sorted-shifts]
  (let [sorted-lines (map #(shift->line lines %) sorted-shifts)]
    (doseq [line sorted-lines]
      (println line))))

(defn kwic []
  (let [[tag result] (input)]
    (case tag
      :ok (->> result
               circular-shifts
               (alphabetize result)
               (output result))
      :err (println "An error occurred reading input: " (.getMessage result)))))


