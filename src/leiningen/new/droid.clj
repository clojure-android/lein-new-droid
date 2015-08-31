(ns leiningen.new.droid
  (:require [leiningen.core.main :as main]
            [leiningen.core.classpath :as cp]
            [leiningen.core.project :as project]))

(defn pull-lein-droid
  "Downloads latest lein-droid plugin from Clojars, adds it to the classpath,
  returns its version."
  []
  (->> (cp/get-dependencies :dependencies {:dependencies [['lein-droid "RELEASE"]]
                                           :repositories project/default-repositories}
                            :add-classpath? true)
       keys
       (some #(when (= (first %) 'lein-droid) %))
       second))

(defn droid
  "Creates a new Clojure-Android project using lein-droid. Usage: `lein new
  droid <project-name> <package> & options`. If called simply as `lein new
  droid :init`, it will initialize a library project in the current directory."
  [& args]
  (let [version (pull-lein-droid)]
    (main/info (str "Using lein-droid " version))
    (require 'leiningen.droid.utils)
    (require 'leiningen.droid.new)
    (if (= (first args) :init)
      ((resolve 'leiningen.droid.new/init) "." version)
      (let [new-fn (resolve 'leiningen.droid.new/new)]
        (if (< (count args) 2)
          (main/abort ((resolve 'leiningen.droid.utils/wrong-usage)
                       "lein droid new" new-fn))
          (apply new-fn :lein-droid-version version args))))))
