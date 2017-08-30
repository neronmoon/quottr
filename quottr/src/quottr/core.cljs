(ns quottr.core
  (:require
   [rum.core :as rum]))

(enable-console-print!)

(defonce app-state (atom {:quotes      []
                          :modal-state false}))

(defn submit-quote [quot]
  (reset! (rum/cursor-in app-state [:quotes])
          (conj (:quotes @app-state) {:text (:text @quot) :author (:author @quot)})))

(def *is-modal-active (rum/cursor-in app-state [:modal-state]))

(rum/defcs modal < rum/reactive (rum/local {:text "" :author ""}) [state]
  (let [*q      (:rum/local state)
        *text   (rum/cursor-in *q [:text])
        *author (rum/cursor-in *q [:author])]
    [:.modal.active
     [:.modal-overlay {:on-click #(reset! *is-modal-active false)}]
     [:.modal-container
      [:.modal-header
       [:button.btn.btn-clear.float-right {:aria-label "Close" :on-click #(reset! *is-modal-active false)}]
       [:.modal-title.h5 "Quotes are gooooood"]]
      [:form {:on-submit (fn [e]
                           (submit-quote *q)
                           (reset! *q {:text "" :author ""})
                           (reset! *is-modal-active false)
                           (.preventDefault e))}
       [:.modal-body
        [:.content
         [:.form-group
          [:textarea.form-input {:rows 5
                                 :cols 50
                                 :placeholder "What he said?"
                                 :required true
                                 :value (rum/react *text)
                                 :on-change (fn [e] (reset! *text (.. e -currentTarget -value)))}]]
         [:.form-group
          [:input.form-input {:placeholder "Who said that?"
                              :required true
                              :value (rum/react *author)
                              :on-change (fn [e] (reset! *author (.. e -currentTarget -value)))}]]]]
       [:.modal-footer
        [:button.btn.btn-primary "Remember my words!"]]]]]))

(rum/defc navbar []
  [:.container.grid-lg
   [:header.navbar.pt-2
    [:section.navbar-section
     [:h1.logo "Quottr"]]
    [:section.navbar-section
     [:a.btn.btn-primary {:on-click #(reset! *is-modal-active true)}
      "Someone said smthing cool"]]]])

(rum/defc render-quote < {:key-fn #(rand-int 1000000)} [q]
  [:blockquote
   [:p (:text q)]
   [:cite "- " (:author q)]])

(rum/defc quotes-list < rum/reactive []
  [:.container.grid-sm
   (if-let [quotes (seq (rum/react (rum/cursor-in app-state [:quotes])))]
     (for [q quotes] (render-quote q))
     [:p.text-center.text-gray "No quotes for you!"])])

(rum/defc app < rum/reactive []
  [:div
   (navbar)
   (quotes-list)
   (if (rum/react *is-modal-active) (modal))])

(rum/mount (app)
           (. js/document (getElementById "app")))
