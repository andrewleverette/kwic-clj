(ns kwic-clj.mod2-test
  (:require [clojure.test :refer [deftest is]]
            [kwic-clj.mod2 :as mod2]
            [clojure.string :as str]))

(def sample-input
  "This is the first line.
The second line follows the first.
And the third line is the last")

(def sample-output
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

(def one-line-input
  (mod2/->LineStore ["one two three"]))

(def parsed-sample-input
  (mod2/->LineStore (vec (str/split-lines sample-input))))

(def sample-shifts
  '("This is the first line."
    "is the first line. This"
    "the first line. This is"
    "first line. This is the"
    "line. This is the first"
    "The second line follows the first."
    "second line follows the first. The"
    "line follows the first. The second"
    "follows the first. The second line"
    "the first. The second line follows"
    "first. The second line follows the"
    "And the third line is the last"
    "the third line is the last And"
    "third line is the last And the"
    "line is the last And the third"
    "is the last And the third line"
    "the last And the third line is"
    "last And the third line is the"))

(deftest sample-input-parsing
  (is (= [:ok parsed-sample-input]
         (with-in-str sample-input (mod2/input)))))

(deftest empty-input
  (is (empty? (mod2/circular-shifts (mod2/->LineStore [])))))

(deftest one-line-circular-shifts
  (is (= ["one two three" "two three one" "three one two"]
         (mod2/circular-shifts one-line-input))))

(deftest sample-circular-shifts
  (is (= sample-shifts
         (mod2/circular-shifts parsed-sample-input))))

(deftest alphabetize-no-shifts
  (is (empty? (mod2/alphabetize []))))

(deftest alphabetize-one-line-shift
  (is (= ["one two three" "three one two" "two three one"]
         (mod2/alphabetize ["one two three" "two three one" "three one two"]))))

(deftest alphabetize-sample-shifts
  (is (= (str/split-lines sample-output)
         (mod2/alphabetize sample-shifts))))

(deftest output-no-lines
  (is (empty? (with-out-str (mod2/output [])))))

(deftest output-one-line
  (is (= "one two three\nthree one two\ntwo three one\n"
         (with-out-str (mod2/output ["one two three" "three one two" "two three one"])))))

(deftest output-sample-output
  (is (= sample-output
         (with-out-str (mod2/output (str/split-lines sample-output))))))
