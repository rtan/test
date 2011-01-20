(ns jp.chakra.lotus.worksap.cms.svn.svnservice
  (:import
    (org.tmatesoft.svn.core SVNURL)
    (org.tmatesoft.svn.core.wc.admin SVNAdminClient)
    (org.tmatesoft.svn.core.wc SVNClientManager)
    (org.tmatesoft.svn.core.internal.io.dav DAVRepositoryFactory)
    (org.tmatesoft.svn.core.internal.io.fs FSRepositoryFactory)
    (org.tmatesoft.svn.core.internal.io.svn SVNRepositoryFactoryImpl)
    (java.io File)
    (jp.chakra.lotus.service.impl SVNLocatorImpl)
    (jp.chakra.lotus.service.impl SVNUserServiceImpl))
  )

(def locator (new SVNLocatorImpl))
(def service (new SVNUserServiceImpl locator))

(defn commit [siteid paths]
    (println (str "commit siteid=" siteid " paths=" paths))
    (. service update (str userId) (str siteId))
    (. service commit (str userId) (str siteId) (into-array paths) (str message) )
  )
(defn reverse [siteid revision]
    (println (str "reverse siteid=" siteid " revision=" revision))
  )
(defn review [siteid revision workpath]
    (println (str "review siteid=" siteid " revision=" revision " workpath=" workpath))
  )
(defn export [siteid revision path]
    (println (str "export siteid=" siteid " revision=" revision " path=" path))
  )
(defn export-all [siteid tag exportpath]
    (println (str "exportall siteid=" siteid " tag=" tag " exportpath=" exportpath))
  )
(defn create-tag [siteid revision]
    (println (str "createtag siteid=" siteid " revision=" revision))
  )

;
;Following is a test directly use SVNKit without Lotus-scm.
;
(defn setup []
  (. SVNRepositoryFactoryImpl setup)
  (. FSRepositoryFactory setup)
  (. DAVRepositoryFactory setup)
  )

(defn create-dir [path]
  (setup)
  (let [svnurls (make-array SVNURL 1)
        commitClient (. (. SVNClientManager newInstance) getCommitClient)
        svnurl (. SVNURL fromFile (new File (str path)))
        ]
    (aset svnurls 0 path)
    (. commitClient doMkDir svnurls "ok?")
    )
  )

(defn create-repository [path]
  (setup)
  (let [adminClient (. (. SVNClientManager newInstance) getAdminClient)
        svnurl (. SVNURL fromFile (new File path))
        file (new File (. svnurl getPath))
        ]
    (. adminClient doCreateRepository file nil true true)
    )
  )