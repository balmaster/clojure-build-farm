(ns server.distributive
  (use [clojure.data.zip.xml] )
  (require [clojure.xml :as xml]
           [clojure.zip :as zip]
           [clojure.string :as string]))


(defstruct Deploy
  :name
  :path
  :server-map
  :component-map)

(defstruct Server
  :name
  :description
  :address
  :port
  :keyfile
  :jdk
  :tar
  :os
  :arch
  :domain
  :user
  :password
  :home
  :enabled
  :app-server-map)

(defstruct AppServer
  :name
  :description
  :type
  :home
  :port
  :user
  :password
  :protocol
  :domain-map)

(defstruct Domain
  :name
  :description
  :id
  :home
  :port
  :user
  :password
  :protocol
  :instance-map)
  
(defstruct Instance
  :name
  :description
  :id
  :home
  :port
  :user
  :password
  :protocol)

(defstruct Assembly
  :name
  :group-id
  :artifact-id
  :classifier
  :package-type
  :server
  :app-server
  :domain
  :instance)

(defstruct Component
  :name
  :description
  :group-id
  :artifact-id
  :basedir
  :checksum-include-list
  :checksum-exclude-list
  :delete-exclude-list
  :app-file
  :enabled
  :encoding
  :eol
  :server
  :app-server
  :domain
  :instance
  :dependent-list
  :assembly-map
  )
  
(defstruct Service
  :name
  :component-list)

(defn named-list-to-map
  [l]
  (zipmap (map (fn [x] (get x :name)) l) l))

(defn loc-to-instance
  [loc]
  (let [node (zip/node loc)]
    (struct-map
      Instance
      :name (:name (:attrs node))
      :description (:description (:attrs node))
      :id (:id (:attrs node))    
      :home (:home (:attrs node))
      :port (:port (:attrs node))
      :user (:user (:attrs node))
      :password (:password (:attrs node))
      :protocol (:protocol (:attrs node)))))

(defn loc-to-domain
  [loc]
  (let [node (zip/node loc)]
    (struct-map 
      Domain
      :name (:name (:attrs node))
      :description (:description (:attrs node))
      :id (:id (:attrs node))
      :home (:home (:attrs node))
      :port (:port (:attrs node))
      :user (:user (:attrs node))
      :password (:password (:attrs node))
      :protocol (:protocol (:attrs node))
      :instance-map (named-list-to-map
                      (map loc-to-instance (xml-> loc :instance))))))

(defn loc-to-app-server
  [loc]
  (let [node (zip/node loc)]
    (struct-map 
      AppServer
      :name (:name (:attrs node))
      :description (:description (:attrs node))
      :type (:type (:attrs node))
      :home (:home (:attrs node))
      :port (:port (:attrs node))
      :user (:user (:attrs node))
      :password (:password (:attrs node))
      :protocol (:protocol (:attrs node))
      :domain-map (named-list-to-map
                    (map loc-to-domain (xml-> loc :domain))))))

(defn loc-to-server
  [loc]
  (let [node (zip/node loc)]
    (struct-map
      Server
      :name (:name (:attrs node))
      :description (:description (:attrs node))
      :address (:address (:attrs node))
      :port (:port (:attrs node))
      :keyfile (:keyfile (:attrs node))
      :jdk (:jdk (:attrs node))
      :tar (:tar (:attrs node))
      :os (:os (:attrs node))
      :arch (:arch (:attrs node))
      :domain (:domain (:attrs node))
      :user (:user (:attrs node))
      :password (:password (:attrs node))
      :home (:home (:attrs node))
      :enabled (:enabled (:attrs node))
      :app-server-map (named-list-to-map
                        (map loc-to-app-server (xml-> loc :app_server))))))

(defn loc-to-assembly
  [loc]
  (let [node (zip/node loc)]
    (struct-map
      Assembly
      :name (:name (:attrs node))
      :group-id (:groupId (:attrs node))
      :artifact-id (:artifactId (:attrs node))
      :classifier (:classifier (:attrs node))
      :package-type (:package-type (:attrs node)) 
      :server (:server (:attrs node))
      :app-server (:app_server (:attrs node))
      :domain (:domain (:attrs node))
      :instance (:instance (:attrs node)))))

(defn split
  [str]
  (split str #":"))  

(defn split
  [str delim]
  (if str (string/split str delim)))  

(defn loc-to-component
  [loc]
  (let [node (zip/node loc)]
    (struct-map
      Component
      :name (:name (:attrs node))
      :description (:description (:attrs node))
      :group-id (:groupId (:attrs node))
      :artifact-id (:artifactId (:attrs node))
      :basedir (:basedir (:attrs node))
      :checksum-include-list (split (:checksum_includes (:attrs node)))
      :checksum-exclude-list (split (:checksum_excludes (:attrs node)))
      :delete-exclude-list (split (:delete_excludes (:attrs node)))
      :app-file (:app-file (:attrs node))
      :enabled (:enabled (:attrs node))
      :encoding (:encoding (:attrs node))
      :eol (:eol (:attrs node))
      (comment
      :server 
      :app-server 
      :domain
      :instance
      )
      :dependent-list (split (:dependents (:attrs node)) #",")
      :assembly-map (named-list-to-map
                      (map loc-to-assembly (xml-> loc :assembly))))))


   
(defn load-deploy-file
  "Load deployment description from distributive"
  [env file]
  (let [z 
        (zip/xml-zip 
          (xml/parse 
            (java.io.File. file)))]
    (struct-map Deploy
      :path file
      :server-map (named-list-to-map 
                    (map 
                      loc-to-server
                      (xml-> z :servers :server) 
                      )))))
      

(defn get-server-list
  [distributive]
  (vals 
    (get distributive :server-map)))
   
(defn get-server
  [distributive name]
  (get
    (get distributive :server-map)
    name))

(defn get-component-list
  [distributive]
  (vals 
    (get distributive :component-map)))

(defn get-component
  [distributive name]
  (get
    (get distributive :component-map)
    name))

