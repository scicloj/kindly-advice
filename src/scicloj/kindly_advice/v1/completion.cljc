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
    (when (var? x) (meta-kind @x))))

(defn deep-merge
  "Recursively merges maps together. If all the maps supplied have nested maps
  under the same keys, these nested maps are merged. Otherwise the value is
  overwritten, as in `clojure.core/merge`."
  {:arglists '([& maps])}
  ([])
  ([a] a)
  ([a b]
   (when (or a b)
     (letfn [(merge-entry [m e]
               (let [k (key e)
                     v' (val e)]
                 (if (contains? m k)
                   (assoc m k (let [v (get m k)]
                                (if (and (map? v) (map? v'))
                                  (deep-merge v v')
                                  v')))
                   (assoc m k v'))))]
       (reduce merge-entry (or a {}) (seq b)))))
  ([a b & more]
   (reduce deep-merge (or a {}) (cons b more))))

(defn complete-options [{:keys [form value]
                         :as   context}]
  (let [meta-options (or (meta-options form)
                         (meta-options value))]
    (update context :kindly/options
            (fn [options]
              (deep-merge options kindly/*options* meta-options)))))

(defn complete [context]
  (-> context
      complete-value
      complete-meta-kind
      complete-options))
