(ns main.phonebookui.component.phonebook
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [main.phonebookui.logic.phonebook :as logic]
            [main.phonebookui.component.icons :as icons]
            [uix.core.alpha :as uix]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn update-div [updating? force-update? {:keys [title description pos] :as item}]
  (let [title       (uix/state title)
        description (uix/state description)]
    [:div {:class-name "container mb-2 todo-item align-middle"}
     [:div {:class-name "row"}
      [:div {:class-name "col-5"}
       [:input {:value       @title
                :placeholder "Title"
                :class-name "form-control"
                :on-change   (uix/callback #(reset! title (.. % -target -value)) [title])}]]
      [:div {:class-name "col-5"}
       [:input {:value       @description
                :placeholder "Description"
                :class-name "form-control"
                :on-change   (uix/callback #(reset! description (.. % -target -value)) [description])}]]
      [:div {:class-name "col-1"}
       [:button {:class-name "btn btn-primary btn-lg btn-block float-end"
                 :on-click (uix/callback
                            (fn [e]
                              (go (<! (http/put (str "http://localhost:8080/todos/" pos)
                                               {:json-params (assoc item :title @title :description @description)}))
                                 (reset! updating? false)
                                 (swap! force-update? inc)))
                            [title description updating?])} icons/check]]
      [:div {:class-name "col-1"}
       [:button {:class-name "btn btn-primary btn-lg btn-block float-end"
                 :on-click (uix/callback
                            (fn [e] (reset! updating? false))
                            [updating?])} icons/cancel]]]]))


(defn item-div [{:keys [first-name last-name phone-num]}]
  [:div {:class-name "container mb-2 todo-item align-middle"}
   [:div {:class-name "row"}
    [:div {:class-name "col-2"}
     [:span first-name]]
    [:div {:class-name "col-2"}
     [:span  last-name]]
    [:div {:class-name "col-2"}
     [:span phone-num]]
    [:div {:class-name "col-1"}
     [:button {:class-name "btn btn-primary btn-lg btn-block float-end"
               :on-click   (uix/callback
                            (fn [e]
                              (go (<! (http/delete (str "http://localhost:8080/phonebook/" first-name "/" last-name)))
                                  ))
                            [])} icons/trash]]]])

(defn search-exact-div []
  (let [f-name (uix/state "")
        l-name (uix/state "")
        esearch-list (uix/state {})]
    [:div {:class-name "container py-3 phonebook mb-2"}
     [:div {:class-name "row"}
      [:div {:class-name "col-2"}
       [:input {:value       @f-name
                :placeholder "First name"
                :class-name  "form-control"
                :style {:line-height 2}
                :on-change   (uix/callback #(reset! f-name (.. % -target -value))
                                           [f-name])}]]
      [:div {:class-name "col-2"}
       [:input {:value       @l-name
                :placeholder "last name"
                :class-name  "form-control"
                :style {:line-height 2}
                :on-change   (uix/callback #(reset! l-name (.. % -target -value))
                                           [l-name])}]]
      [:div {:class-name "col-3"}
       [:button {:class-name "btn btn-primary btn-lg btn-block float-end"
                 :on-click (uix/callback
                            (fn [e]
                              (go (reset! esearch-list
                                          (:body
                                           (<! (http/get
                                                "http://localhost:8080/phonebook/"
                                                {:query-params {"first-name" @f-name
                                                                "last-name" @l-name}}))))
                                  (reset! f-name "")
                                  (reset! l-name "")
                                  ))
                            [f-name
                             l-name])} "Search Exact"]]]
     (when (:first-name @esearch-list)
       [:div {:class-name "row"}
        [item-div @esearch-list]])]))



(defn search-div []
  (let [name (uix/state "")
        search-list (uix/state [])]
    [:div {:class-name "container py-3 phonebook mb-2"}
     [:div {:class-name "row"}
      [:div {:class-name "col-3"}
       [:input {:value       @name
                :placeholder "First name"
                :class-name  "form-control"
                :style {:line-height 2}
                :on-change   (uix/callback #(reset! name (.. % -target -value)) [name])}]]
      [:div {:class-name "col-2"}
       [:button {:class-name "btn btn-primary btn-lg btn-block float-end"
                 :on-click (uix/callback
                            (fn [e]
                              (go (reset! search-list (:body
                                                       (<! (http/get
                                                            "http://localhost:8080/phonebook/search"
                                                            {:query-params {"name" @name}}))))
                                  (reset! name "")
                                  ))
                            [name])} "Search"]]]
     [:div
      [:table {:class-name "table"}
       (map (fn [item]
              [item-div item]) @search-list)]]]))

(defn add-phone-div []
  (let [first-name (uix/state "")
        last-name (uix/state "")
        phone (uix/state "")]
    [:div {:class-name "container py-3 phonebook mb-2"}
     [:div {:class-name "row"}
      [:div {:class-name "col-4"}
       [:input {:value       @first-name
                :placeholder "First name"
                :class-name  "form-control"
                :style {:line-height 2}
                :on-change   (uix/callback #(reset! first-name (.. % -target -value)) [first-name])}]]
      [:div {:class-name "col-3"}
       [:input {:value       @last-name
                :placeholder "Last name"
                :class-name  "form-control"
                :style {:line-height 2}
                :on-change   (uix/callback #(reset! last-name (.. % -target -value)) [last-name])}]]
      [:div {:class-name "col-3"}
       [:input {:value       @phone
                :placeholder "Phone no."
                :class-name  "form-control"
                :style {:line-height 2}
                :on-change   (uix/callback #(reset! phone (.. % -target -value)) [phone])}]]
      [:div {:class-name "col-2"}
       [:button {:class-name "btn btn-primary btn-lg btn-block float-end"
                 :on-click (uix/callback
                            (fn [e]
                              (go (<! (http/post
                                       "http://localhost:8080/phonebook/"
                                       {:json-params (logic/new-item
                                                      @first-name
                                                      @last-name
                                                      @phone)}))
                                  (reset! first-name "")
                                  (reset! last-name "")
                                  (reset! phone "")))
                            [first-name last-name phone])} "Add Person"]]]]))
