(ns main.phonebookui.logic.phonebook)

(defn remove-item [items position]
  (filter #(not (= position (% :pos))) items))

(defn update-item [parent-list item]
  (-> parent-list
      (remove-item (:pos item))
      (conj item)
      (#(sort-by :pos %))
      reverse))

(defn delete-item! [parent-list pos]
  (fn [e] (swap! parent-list remove-item pos)))

(defn new-item [first-name last-name phone-num]
  {:first-name first-name
   :last-name last-name
   :phone-num phone-num})
