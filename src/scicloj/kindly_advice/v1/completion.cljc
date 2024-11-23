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

(defn hide-code? [{:as note :keys [code form value kind]} options]
  (let [m (merge options (meta form) (meta value))
        hide-code (select-keys m [:hide-code
                                  :kind/hide-code
                                  :kindly/hide-code
                                  :kindly/hide-code?])]
    (or (= kind :kind/hidden)
        (nil? code)
        (some val hide-code)
        (and kind
             (empty? hide-code)
             (some-> options :kinds-that-hide-code kind)))))

(def default-hide-value-syms
  '#{ns comment
     def defonce defn defmacro
     defrecord defprotocol deftype
     extend-protocol extend
     require})

(defn hide-value? [{:as note :keys [form value kind]}
                   {:as options :keys [hide-nils hide-vars hide-value-syms]}]
  (let [m (merge options (meta form) (meta value))
        hide-value (select-keys m [:hide-value
                                   :kind/hide-value
                                   :kindly/hide-value
                                   :kindly/hide-value?])]
    (or (= kind :kind/hidden)
        (some val hide-value)
        (and hide-nils (nil? value))
        (and hide-vars (var? value))
        (and (sequential? form)
             (some->> form first (get (or hide-value-syms default-hide-value-syms))))
        (and kind
             (empty? hide-value)
             (some-> options :kinds-that-hide-values kind)))))

(defn with-hide-options [context options]
  (cond-> options
          (hide-code? context options) (assoc :hide-code true)
          (hide-value? context options) (assoc :hide-value true)))

(defn meta-options [x]
  (or
   (when-let [m (meta x)]
     (:kindly/options m))
   (when (var? x) (meta-options @x))))

(defn complete-options [{:keys [form value]
                         :as   context}]
  (let [form-options (meta-options form)]
    ;; Kindly options found on ns form cause options to be mutated,
    ;; note that options on the value are already on the ns.
    (when (and (sequential? form)
               (-> form first (= 'ns))
               form-options)
      (kindly/merge-options! form-options))

    (update context :kindly/options
            (fn [context-options]
              ;; context options come from configuration
              (->> (kindly/deep-merge context-options
                                      ;; options from the ns
                                      (kindly/get-options)
                                      form-options
                                      (meta-options value))
                   (with-hide-options context))))))

(defn complete [context]
  (-> context
      complete-value
      complete-meta-kind
      complete-options))
