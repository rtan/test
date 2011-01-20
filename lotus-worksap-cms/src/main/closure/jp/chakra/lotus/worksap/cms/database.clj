(ns jp.chakra.lotus.worksap.cms.database
  (:use
    net.briancarper.postgres-pool
    clojure.contrib.logging
    clojure.contrib.sql
    jp.chakra.lotus.worksap.cms.config)
  )

;(def db (postgres-pool @set-db))
;(def db (postgres-pool {:host "db01.lotus.chakra.jp" :port 5432
;                      :username "trancecore" :password "connecty1216"
;                      :database "eccms"}))

(defn- insert-query [table column-names value]
  (with-connection (postgres-pool @set-db)
    (transaction
      (insert-values table column-names value))
    )
  )

(defn insert-workitem [status name message userid siteid publish_date tags_name revision apps_key]
  (let [timestamp (java.sql.Timestamp. (.getTime (java.util.Date.)))]
    (insert-query :cms_workitem
      [:status :created :updated :name :message :cms_user_id :cms_site_id :publish_date :tags_name :revision :apps_keys]
      [status timestamp timestamp name message userid siteid publish_date tags_name revision apps_key])
    )
  )

