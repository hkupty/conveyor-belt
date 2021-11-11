(ns conveyor-belt.git
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (org.eclipse.jgit.api Git)
           (org.eclipse.jgit.lib Constants)))

(def git (Git/open (.getAbsoluteFile (io/file "."))))

(defn head [git] (.resolve (.getRepository git) Constants/HEAD))

(defn commit-theme [message]
  (keyword (last (re-find (re-matcher #"^(\w+):"
              message)))))

(defn xf-factory [-fn]
 (fn [xf]
    (fn
      ([] (xf))
      ([result] (xf result))
      ([result commit]
       (-fn commit)
       (xf result commit)))))

(defn xf-count-themes [v-map]
  (xf-factory
    (fn [commit]
      (vswap! v-map update (commit-theme (.getShortMessage commit)) (fnil inc 0)))))

(defn xf-get-changelog [v-coll]
  (xf-factory
    (fn [commit]
      (let [msg (.getShortMessage commit)]
         (vswap! v-coll
                 update
                 (commit-theme msg)
                 (fnil conj [])
                 (last (str/split msg #": " 2)))))))

(defn process-commits! [xf]
  (into []
        xf
        (iterator-seq
          (.iterator (.call (.addRange (.log git)
                                       (.getObjectId (last (-> git
                                                               (.tagList)
                                                               (.call))))
                                       (head)))))))

(defn changelog []
  (let [chg (volatile! {})]
    (process-commits! (xf-get-changelog chg))
    (run! (fn [[k v]]
            (some-> k
                    (name)
                    (str ":")
                   (println))
            (run! println v))
          @chg)))
