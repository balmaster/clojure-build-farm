(ns server.common
  (:require [clojure.java.io :as io]))

(defn get-basedir
  "Получить базовый каталог"
  []
  (.getAbsolutePath
    (io/file "")))