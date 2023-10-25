(ns scicloj.kindly-advice.v1.completion
  (:require [clojure.string :as str]))

(defn eval-in-ns [ns form]
  (if ns
    (binding [*ns* ns]
      (eval form))
    (eval form)))

(defn complete-value [{:keys [ns form]
                       :as context}]
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
  (when-let [m (meta x)]
    (or
      ;; ^{:kindly/kind :kind/table} x
      (kind (:kindly/kind m))

      ;; ^kind/table x
      (kind (:tag m))

      ;; ^:kind/table x
      (->> (keys m)
           (keep kind)
           (first)))))

(defn complete-meta-kind [{:keys [form value]
                           :as context}]
  (assoc context
         :meta-kind (or (meta-kind form)
                        (meta-kind value))))

(defn complete [context]
  (-> context
      complete-value
      complete-meta-kind))
