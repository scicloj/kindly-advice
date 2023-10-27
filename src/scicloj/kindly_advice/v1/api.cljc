(ns scicloj.kindly-advice.v1.api
  (:require [scicloj.kindly-advice.v1.advisors :as advisors]
            [scicloj.kindly-advice.v1.completion :as completion]))

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
  (swap! *advisors conj advisor))

(defn set-advisors! [advisors]
  (reset! *advisors advisors))

(defn derefing-advise
  "Kind priority is inside out: kinds on the value supersedes kinds on the ref."
  [context]
  (let [context (advise context)
        {:keys [value]} context]
    (if (instance? clojure.lang.IDeref value)
      (let [v @value
            meta-kind (completion/meta-kind v)]
        (advise (derefing-advise (cond-> (assoc context :value v)
                                         meta-kind (assoc context :meta-kind meta-kind)))))
      context)))

(defn top-level-advise
  "Vars are derefed only when a user annotated kind is present, otherwise they are left alone."
  [context]
  (let [{:keys [value]} context]
    (if (var? value)
      (let [v @value
            meta-kind (or (completion/meta-kind v)
                          (completion/meta-kind value))]
        (if meta-kind
          (advise (derefing-advise (assoc context :value v
                                                  :meta-kind meta-kind)))
          (advise context)))
      (derefing-advise context))))
