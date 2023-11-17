(ns kwic-clj.mod1-test
  (:require [clojure.test :refer [deftest is]]
            [kwic-clj.mod1 :as mod1]
            [clojure.string :as str]))

(def sample-input
  "This is the first line.
The second line follows the first.
And the third line is the last")

(def sample-ouput
  "And the third line is the last
first line. This is the
first. The second line follows the
follows the first. The second line
is the first line. This
is the last And the third line
last And the third line is the
line follows the first. The second
line is the last And the third
line. This is the first
second line follows the first. The
the first line. This is
the first. The second line follows
the last And the third line is
The second line follows the first.
the third line is the last And
third line is the last And the
This is the first line.
")

(def parsed-sample-input
  (vec (map #(str/split % #"\s+") (str/split-lines sample-input))))

(def sample-shifts
  [[0 0] [0 1] [0 2] [0 3] [0 4] [1 0] [1 1] [1 2] [1 3] [1 4] [1 5] [2 0] [2 1] [2 2] [2 3] [2 4] [2 5] [2 6]])

(def sorted-shifts
  [[2 0] [0 3] [1 5] [1 3] [0 1] [2 4] [2 6] [1 2] [2 3] [0 4] [1 1] [0 2] [1 4] [2 5] [1 0] [2 1] [2 2] [0 0]])

(deftest sample-input-parsing
  (is (= [:ok parsed-sample-input]
         (with-in-str sample-input (mod1/input)))))

(deftest empty-input
  (is (empty? (mod1/circular-shifts []))))

(deftest one-line-input
  (is (= [[0 0] [0 1] [0 2]]
         (mod1/circular-shifts [["one" "two" "three"]]))))

(deftest sample-input-circular-shifts
  (is (= sample-shifts
         (mod1/circular-shifts parsed-sample-input))))

(deftest shifts-that-can-cause-exceptions
  (is (thrown? Exception (mod1/shift->line [] [0 0])))
  (is (thrown? Exception (mod1/shift->line parsed-sample-input [3 0]))))

(deftest convert-shift-to-line
  (is (= "This is the first line."
         (mod1/shift->line parsed-sample-input [0 0])))
  (is (= "is the first line. This"
         (mod1/shift->line parsed-sample-input [0 1])))
  (is (= "the first line. This is"
         (mod1/shift->line parsed-sample-input [0 2]))))

(deftest alphabetize-no-input
  (is (thrown? Exception (mod1/alphabetize [] sample-shifts))))

(deftest alphabetize-no-shifts
  (is (empty? (mod1/alphabetize parsed-sample-input []))))

(deftest alphabetize-one-line
  (is (= [[0 0] [0 2] [0 1]]
         (mod1/alphabetize [["one" "two" "three"]] [[0 0] [0 1] [0 2]]))))

(deftest alphabetize-sample-input
  (is (= sorted-shifts
         (mod1/alphabetize parsed-sample-input sample-shifts))))

(deftest output-no-lines
  (is (thrown? Exception (with-out-str (mod1/output [] sample-shifts)))))

(deftest output-no-sorted-shifts
  (is (= ""
         (with-out-str (mod1/output parsed-sample-input [])))))

(deftest output-one-line-of-input
  (is (= (str "one two three\nthree one two\ntwo three one\n")
         (with-out-str (mod1/output [["one" "two" "three"]] [[0 0] [0 2] [0 1]])))))

(deftest output-with-sample-input
  (is (= sample-ouput
         (with-out-str (mod1/output parsed-sample-input sorted-shifts)))))
