(ns jp.chakra.lotus.worksap.cms.config
  (:gen-class)
  (:use clojure.xml)
  )

(def config (ref
  {:userid ""
   :password ""
   :port 0}
  ))

(def set-db (ref {
  :host ""
  :port 0
  :username ""
  :password ""
  :database ""}
  ))

; func
(defn get-value [param tag]
  (first
    (for [cnt (range (count param)) :when (= (:tag (get param cnt)) tag)]
      (let [value (:content (get param cnt))]
        (if (map? (get value 0))
          (doall value)
          (get value 0)
          )
        )
      )
    )
  )

(defn set-map [ref map]
  (first
    (for [cnt (range (count map))]
      (let [value (:content (get map cnt)) key (:tag (get map cnt))]
        (dosync (alter ref assoc key (get value 0)))
        )
      )
    )
  )

(defn setting-config
  ([] setting-config "config.xml")
  ([config-xml]
    (let [configxml (parse config-xml) params (:content configxml)]
      (set-map config (get-value params :config))
      (set-map set-db (get-value params :db)))
    )
  )
