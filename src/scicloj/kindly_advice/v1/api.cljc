(ns scicloj.kindly-advice.v1.api
  (:require [scicloj.kindly-advice.v1.advisors :as advisors]
            [scicloj.kindly-advice.v1.completion :as completion]))

(def *advisors
  (atom advisors/default-advisors))

(defn advise
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
  (swap! *advisors conj advisor))

(defn set-advisors! [advisors]
  (reset! *advisors advisors))

(defn set-only-advisor! [advisor]
  (set-advisors! [advisor]))
