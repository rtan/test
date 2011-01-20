(ns jp.chakra.lotus.worksap.cms.controller
  (:require [clojure.contrib.logging :as log])
  (:gen-class)
  (:use
    clojure.contrib.logging
    clojure.contrib.command-line
    compojure.core
    compojure.route
    ring.adapter.jetty
    ring.middleware.file-info
    ring.middleware.params
    ring.middleware.keyword-params
    sandbar.stateful-session
    jp.chakra.lotus.worksap.cms.svn.svnservice
    jp.chakra.lotus.worksap.cms.config
    jp.chakra.lotus.worksap.cms.database)
  (:import
    (java.io File))
  )

;簡易認証
(defn authorization-filter [handler]
  (fn [req]
    (let [userid ((:query-params req) "userid")
          password ((:query-params req) "password")]
      (info (str "authorization-filter input  : userid=" userid " password=" password))
      (info (str "authorization-filter config : userid=" (:userid @config) " password=" (:password @config)))
      (if (= userid (:userid @config))
        (if (= password (:password @config))
          (handler req)
          {:status 403 :body "Incorrect username or password"})
        {:status 403 :body "Incorrect username or password"}
          ))))

(defroutes main-routes
  (GET "/commit" req
    (commit ((:query-params req) "siteid") ((:query-params req) "paths"))
    (insert-workitem 1 "" "" 102 102 (java.sql.Timestamp. (.getTime (java.util.Date.))) "" "1" "")
    )
  (GET "/reverse" req
    (reverse ((:query-params req) "siteid") ((:query-params req) "revision")))
  (GET "/review" req
    (review ((:query-params req) "siteid") ((:query-params req) "revision") ((:query-params req) "workpath")))
  (GET "/export" req
    (export ((:query-params req) "siteid") ((:query-params req) "revision") ((:query-params req) "path")))
  (GET "/exportall" req
    (export-all ((:query-params req) "siteid") ((:query-params req) "tag") ((:query-params req) "exportpath")))
  (GET "/createtag" req
    (create-tag ((:query-params req) "siteid") ((:query-params req) "revision")))
    )

(def all-handler
  (-> main-routes
      authorization-filter
      (wrap-params)))

(defn -main [& args]
  (with-command-line args "lotus ec integration"
    [[config-xml "Setting config" "config.xml"]]
    (setting-config config-xml)
    (info (str "succeeded in reading the configration file"))
    (info (str "config :" @config))
    (info (str "db :" @set-db))
    (run-jetty all-handler {:port (Integer/parseInt (:port @config))}))
  )
