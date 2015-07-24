(ns leiningen.new.droid
  (:require [leiningen.core.main :as main]
            [cemerick.pomegranate :as deps]
            [ancient-clj.core :as ancient]))

(defn latest-lein-droid-version []
  (-> (ancient/versions! 'lein-droid {:snapshots? false})
      first :version-string))

(defn pull-lein-droid [version]
  (deps/add-dependencies :coordinates [['lein-droid version]]
                         :repositories {"clojars" "http://clojars.org/repo"}))

(defn droid
  "Creates a new Clojure-Android project using lein-droid. Usage: `lein new
  droid <project-name> <package> & options`. If called simply as `lein new
  droid :init`, it will initialize a library project in the current directory."
  [& args]
  (let [version (latest-lein-droid-version)]
    (pull-lein-droid version)
    (main/info (str "Using lein-droid " version))
    (if (= (first args) :init)
      ((resolve 'leiningen.droid.new/init))
      (apply (resolve 'leiningen.droid.new/new) args))))
