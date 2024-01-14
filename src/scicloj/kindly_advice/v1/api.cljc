(ns scicloj.kindly-advice.v1.api
  (:require [scicloj.kindly-advice.v1.advisors :as advisors]
            [scicloj.kindly-advice.v1.completion :as completion]))

(def default-advidors
  advisors/default-advisors)

(def *advisors
  (atom advisors/default-advisors))

(defn advise
  "Adds advice to a context such as `{:form [:div]}`.
  Advice recommends a kind `{:form [:div], :value [:div], :kind :kindly/hiccup}`.
  Works best with both form and value because metadata may appear on either.
  If no value is present, will evaluate `:form` and populate `:value` and `:kind`."
  ([context]
   (advise context @*advisors))
  ([context advisors]
   (-> context
       completion/complete
       (#(reduce advisors/update-context
                 %
                 advisors))
       (update :advice vec))))

(defn add-advisor! [advisor]
  (swap! *advisors (partial cons advisor)))

(defn set-advisors! [advisors]
  (reset! *advisors advisors))
