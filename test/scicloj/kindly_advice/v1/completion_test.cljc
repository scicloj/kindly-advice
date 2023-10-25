(ns scicloj.kindly-advice.v1.completion-test
  (:require [clojure.test :refer [deftest is testing]]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly-advice.v1.completion :as kac]))

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
    (is (= nil (kac/meta-kind ^{:kindly/kind :not-a-kind} {})))))
