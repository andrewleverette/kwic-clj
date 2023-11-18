(ns kwic-clj.core
  (:gen-class)
  (:require [kwic-clj.mod1 :as mod1]
            [kwic-clj.mod2 :as mod2]))

(defn -main
  []
  (mod2/kwic))
