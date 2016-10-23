(ns leiningen.new.droid
  (:require [leiningen.core.main :as main]
            [leiningen.core.classpath :as cp]
            [leiningen.core.project :as project]))

(defn pull-lein-droid
  "Downloads latest lein-droid plugin from Clojars, adds it to the classpath,
  returns its version."
  [explicit-version]
  (let [deps-val {:dependencies [['lein-droid (or explicit-version "RELEASE")]]
                  :repositories project/default-repositories}
        resolved-dep (or (try (cp/get-dependencies :dependencies deps-val
                                                   :add-classpath? true)
                              (catch Exception _ nil))
                         (try (cp/get-dependencies :dependencies :foo deps-val
                                               :add-classpath? true)
                              (catch Exception _ nil)))]
    (when-not resolved-dep
      (main/abort "Something wrong with Leiningen's get-dependency! Please file an issue."))
   (->> resolved-dep
        keys
        (some #(when (= (first %) 'lein-droid) %))
        second)))

(defn droid
  "Creates a new Clojure-Android project using lein-droid. Usage: `lein new
  droid <project-name> <package> & options`. If called simply as `lein new
  droid :init`, it will initialize a library project in the current directory."
  [& args]
  (let [init? (= (first args) :init)
        options (when-not init?
                  (apply hash-map (drop 2 args)))
        version (pull-lein-droid (get options ":use-version"))]
    (main/info (str "Using lein-droid " version))
    (require 'leiningen.droid.utils)
    (require 'leiningen.droid.new)
    (if init?
      ((resolve 'leiningen.droid.new/init) ".")
      (let [new-fn (resolve 'leiningen.droid.new/new)]
        (if (< (count args) 2)
          (main/abort ((resolve 'leiningen.droid.utils/wrong-usage)
                       "lein droid new" new-fn))
          (apply new-fn args))))))
