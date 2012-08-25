(ns test.server.distributive 
  (:use [server.distributive]
       [clojure.test]
       [clojure.data.zip.xml])
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]))

(defn parse-str [s]
  (zip/xml-zip (xml/parse (new org.xml.sax.InputSource
                               (new java.io.StringReader s)))))

(def data1 (parse-str "
<deploy xmlns='http://jet.msk.su/build_farm/deploy'>
    <servers>
        <server name='server1' address='${server1.address}' keyfile='${server1.keyfile}'
            user='${server1.user}' jdk='${server1.jdk}' tar='${server1.tar}' home='${server1.home}'>
            <app_server name='resin'>
                <domain name='default' home='/opt/resin' id=''>
                    <instance name='default' id='' />
                </domain>
            </app_server>
            <app_server name='default'>
                <domain name='default' id=''>
                    <instance name='default' id='' />
                </domain>
            </app_server>
        </server>
   </servers>
</deploy>"))

(deftest test-loc-to
  (testing "instance count"
           (is (= 
                 (count
                     (xml-> data1 :servers :server :app_server :domain :instance))
                 2)))
  (testing "instance read"
           (is (=
                 (loc-to-instance
                   (first
                     (xml-> data1 :servers :server :app_server :domain :instance)))
                 (struct-map
                   Instance
                   :name "default" 
                   :id ""))))
  (testing "domain read"
           (is (=
                 (loc-to-domain
                   (first
                     (xml-> data1 :servers :server :app_server :domain)))
                 (struct-map
                   Domain
                   :name "default"
                   :id ""
                   :home "/opt/resin"
                   :instance-map
                   {
                    "default"
                    (struct-map
                      Instance
                       :name "default"
                       :id "")
                    }))))
    (testing "app-server read"
           (is (=
                 (loc-to-app-server
                   (first
                     (xml-> data1 :servers :server :app_server)))
                 (struct-map
                   AppServer
                   :name "resin"
                   :domain-map
                   {
                    "default"
                    (struct-map
                      Domain
                      :name "default"
                      :id ""
                      :home "/opt/resin"
                      :instance-map
                      {
                       "default"
                       (struct-map
                         Instance
                         :name "default"
                         :id "")
                       })
                    }))))
    (testing "server read"
           (is (=
                 (loc-to-server
                   (first
                     (xml-> data1 :servers :server)))
                 (struct-map
                   Server
                   :name "server1"
                   :address "${server1.address}"
                   :keyfile "${server1.keyfile}"
                   :user "${server1.user}"
                   :jdk "${server1.jdk}"
                   :tar "${server1.tar}"
                   :home "${server1.home}"
                   :app-server-map
                   {
                    
                    "resin"
                    (struct-map
                      AppServer
                      :name "resin"
                      :domain-map
                      {
                       "default"
                       (struct-map
                         Domain
	                      :name "default"
	                      :id ""
	                      :home "/opt/resin"
	                      :instance-map
	                      {
	                       "default"
	                       (struct-map
	                         Instance
	                         :name "default"
	                         :id "")
	                       })
	                    })
                    
                    "default"
                    (struct-map
                      AppServer
                      :name "default"
                      :domain-map
                      {
                       "default"
                       (struct-map
                         Domain
	                      :name "default"
	                      :id ""
	                      :instance-map
	                      {
	                       "default"
	                       (struct-map
	                         Instance
	                         :name "default"
	                         :id "")
	                       })
	                    })
                    })))))
	                    
	
	                 
	                   
	                 
	                 
	
                 
                   
                  
