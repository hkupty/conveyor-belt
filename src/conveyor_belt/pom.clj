(ns conveyor-belt.pom
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (org.apache.maven.model Model)
           (org.apache.maven.model.io.xpp3 MavenXpp3Reader MavenXpp3Writer)) )

(defn read-pom []
  (.read (MavenXpp3Reader.) (io/reader (io/file "pom.xml"))))

(defn write-pom! [file]
  (.write (MavenXpp3Writer.)
          (io/writer (io/file "pom.xml"))
          file))

(defn get-version [str-version]
  (into []
        (map #(Integer/parseInt %))
        (str/split str-version #"\.")))

(defn major [version] (-> version
                          (update 0 inc)
                          (assoc 1 0)
                          (assoc 2 0)))
(defn minor [version] (-> version
                          (update 1 inc)
                          (assoc 2 0)))
(defn bugfix [version] (update version 2 inc))

(defn bump-version [version]
  (str/join "." (let [version (get-version version)]
                  )))

(defn update-version! []
  (let [base (read-pom)]
    (.setVersion base (bump-version (.getVersion base)))
    (write-pom! base)))
