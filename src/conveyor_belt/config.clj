(ns conveyor-belt.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def ^:private default-config
  {:bugfix-prefixes #{:fix :bug :bugfix :patch}
   :minor-prefixes #{:feature :new :add :feat}
   :major-prefixes #{:breaking :break}})

(def ^:private user-config (promise))

(comment
  (str/split-lines "adsf\nqewr")
  )

(defn conf [-key]
  (when-not (realized? user-config)
    (deliver default-config))
  (-> user-config
      (deref)
      (get -key)))


(defn read-config-from-file [fpath]
  (-> fpath
      slurp
      edn/read-string))
