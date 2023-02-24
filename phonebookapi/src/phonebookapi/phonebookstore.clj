(ns phonebookapi.phonebookstore
  (:require [clojure.string :as str]
            [taoensso.timbre :as log]))

(def db (atom []))

(defn standardize
  "1.  cleanup whitespaces by truncating it.
  2. if input is nil, treat it as empty string."
  [s]
  (->
   (or s "")
   str
   str/trim))

(defn equals-ci? [x y]
  (or (= x y nil)
      (and (not= x nil) (not= y nil) (= (str/lower-case x) (str/lower-case y)))))

(defn contains-ci?
  "checks whether given string (sub) is present in another string (whole).
  this comparison is case insensitive(CI)"
  [whole sub]
  (let [whole (or whole "")
        sub (or sub "")]
    (str/includes?  (str/lower-case whole) (str/lower-case sub))))

(defn get-item
  "returns a record based on exact name"
  ([first-name last-name]
   (get-item {:first-name first-name
              :last-name last-name}))
  ([{:keys [first-name last-name]}]
   (let [first-name (standardize first-name)
         last-name (standardize last-name)
         existing (some (fn [{fn :first-name ln :last-name :as item}]
                          (when
                           (and (equals-ci? fn first-name) (equals-ci? ln last-name))
                            item)) @db)]
     existing)))

(defn add-item!
  "adding a new name & phone entry to the store"
  ([first-name last-name phone-num]
   (add-item! {:first-name first-name
               :last-name last-name
               :phone-num phone-num}))
  ([{:keys [first-name last-name phone-num]  :as item}]
   (when (and first-name last-name phone-num)
     (let [existing (get-item item)]
      (if existing
        existing
        (let [new-item {:first-name (standardize first-name)
                        :last-name (standardize last-name)
                        :phone-num phone-num
                        :id (java.util.UUID/randomUUID)
                        :new? true}]
          (swap! db conj (dissoc new-item :new?))
          new-item))))))

(defn remove-item!
  ([first-name last-name]
   (remove-item! {:first-name first-name
                  :last-name last-name}))
  ([item]
   (let [existing (get-item item)]
     (if existing
       (do (swap! db (fn [_] (filter #(not= existing %) @db))) true)
       false))))

(defn search-items
  "search based on partial match"
  [name]
  (let [name (standardize name)]
    (if (>= (count name) 3)
      (filter (fn [{fn :first-name ln :last-name :as item}]
                (when
                 (or (contains-ci? fn name) (contains-ci? ln name))
                  item)) @db)
      (log/info "Invalid search criteria. Should be more than 2 characters!"))))
