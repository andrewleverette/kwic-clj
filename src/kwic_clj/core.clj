(ns kwic-clj.core
  (:gen-class)
  (:require [kwic-clj.mod1 :as mod1]))

(defn -main
  []
  (mod1/kwic))
