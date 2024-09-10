(ns scicloj.kindly-advice.v1.completion-test
  (:require [clojure.test :refer [deftest is testing]]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly-advice.v1.completion :as kac]))

(def table3 (kind/table {}))

(def ^{:kindly/kind :kind/table} table4 {})

(def ^:kind/table table5 {})


(deftest meta-kind-test
  (testing "valid ways to annotate a kind"
    (is (= :kind/table (kac/meta-kind (kind/table {}))))
    (is (= :kind/table (kac/meta-kind ^{:kindly/kind :kind/table} {})))
    (is (= :kind/table (kac/meta-kind ^{:kindly/kind kind/table} {})))
    (is (= :kind/table (kac/meta-kind ^{:kindly/kind 'kind/table} {})))
    (is (= :kind/table (kac/meta-kind ^{kind/table true} {})))
    (is (= :kind/table (kac/meta-kind ^{'kind/table true} {})))
    (is (= :kind/table (kac/meta-kind ^:kind/table {})))
    (is (= :kind/table (kac/meta-kind ^kind/table {}))))
  (testing "invalid kinds"
    (is (= nil (kac/kind :kindly/table)))
    (is (= nil (kac/meta-kind ^:kindly/table {})))
    (is (= nil (kac/kind 1)))
    (is (= nil (kac/meta-kind ^{:kindly/kind 1} {})))
    (is (= nil (kac/meta-kind ^{:kindly/kind :not-a-kind} {}))))
  (testing "tricky kinds"
    (is (= :kind/table (kac/meta-kind table3)))
    (is (= :kind/table (kac/meta-kind #'table3)))
    (is (= :kind/table (kac/meta-kind #'table4)))
    (is (= :kind/table (kac/meta-kind #'table5)))))

(deftest options-test
  (kac/complete-options {:form
                         (read-string
                          "^{:kindly/options {:foo \"bar\"}} (ns test.ns)")})
  (is (= {:foo "bar"} (kindly/get-options))))
