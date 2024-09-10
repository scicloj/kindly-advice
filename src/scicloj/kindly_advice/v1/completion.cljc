(ns scicloj.kindly-advice.v1.completion
  (:require [clojure.string :as str]
            [scicloj.kindly.v4.api :as kindly]))

(defn eval-in-ns [ns form]
  (if ns
    (binding [*ns* ns]
      (eval form))
    (eval form)))

(defn complete-value [{:keys [ns form]
                       :as   context}]
  (if (contains? context :value)
    context
    (if (contains? context :form)
      (assoc context
        :value (eval-in-ns ns form))
      (throw (ex-info "context missing both form and value"
                      {:context context})))))

(defn kind [x]
  (cond (keyword? x) (when (= (namespace x) "kind")
                       x)
        (symbol? x) (when (= (namespace x) "kind")
                      (keyword x))
        (fn? x) (let [tag-str (str x)]
                  (some-> (re-find #".*\.(kind\$.*)@.*" tag-str)
                          (second)
                          (str/replace \$ \/)
                          (keyword)))))

(defn meta-kind [x]
  (or
    (when-let [m (meta x)]
      (or
        ;; ^{:kindly/kind :kind/table} x
        (kind (:kindly/kind m))

        ;; ^kind/table x
        (kind (:tag m))

        ;; ^:kind/table x
        (->> (keys m)
             (keep kind)
             (first))))
    (when (var? x) (meta-kind @x))))

(defn complete-meta-kind [{:keys [form value]
                           :as   context}]
  (assoc context
    :meta-kind (or (meta-kind form)
                   (meta-kind value))))

(defn meta-options [x]
  (or
   (when-let [m (meta x)]
     (:kindly/options m))
   (when (var? x) (meta-options @x))))

(defn complete-options [{:keys [form value]
                         :as   context}]
  (let [form-options (meta-options form)]
    ;; Kindly options found on ns form cause options to be mutated
    (when (and (sequential? form)
               (-> form first (= 'ns))
               form-options)
      (kindly/merge-options! form-options))

    (update context :kindly/options
            (fn [context-options]
              (kindly/deep-merge context-options
                                 (kindly/get-options)
                                 (meta-options value)
                                 form-options)))))

(defn complete [context]
  (-> context
      complete-value
      complete-meta-kind
      complete-options))
