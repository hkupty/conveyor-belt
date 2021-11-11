(ns conveyor-belt.main
  (:gen-class))



(defn -main [& opts]
  (let [f #(try
             (let [form (read-string %)]
               (cond
                 (qualified-symbol? form) @(requiring-resolve form)
                 (symbol? form) @((ns-publics (symbol (namespace `-main))) form)
                 :else form))
             (catch Exception _ %))
        [f & args] (map f opts)]
    (some-> (apply f args) prn)))
