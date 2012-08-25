(ns server.env)

(defstruct Env
  :property-map)

(defn load-env
  [name]
  ())

(defn get-env-property
  [env key]
  (get 
    (get env :property-map)
    key))
  