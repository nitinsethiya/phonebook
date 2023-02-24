(ns main.phonebookui.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [main.phonebookui.component.phonebook :refer
             [add-phone-div search-div search-exact-div]]
            [uix.dom.alpha :as uix.dom]
            [uix.core.alpha :as uix]))

(defn main-div []
  [:div {:class-name "container"}
   [:h1 {:class-name "py-5 text-center" :style {:color "white"}} "Phonebook"]
   [:div
    [:h3 {:style {:color "white"}} "Add phonebook entry"]
    [add-phone-div]
    [:h3 {:style {:color "white"}} "Search by Name"]
    [search-div]
    [:h3 {:style {:color "white"}} "Search by Exact Name"]
    [search-exact-div]]])

(uix.dom/render
 [main-div]
 (.getElementById js/document "root"))
