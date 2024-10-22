(ns scicloj.kindly-advice.v1.api-test
  (:require [clojure.test :refer [deftest is testing]]
            [scicloj.kindly-advice.v1.api :as api]
            [clojure.test :as t]
            [tablecloth.api :as tc]))

(defonce image
  (->  "https://upload.wikimedia.org/wikipedia/commons/2/2c/Clay-ss-2005.jpg"
       (java.net.URL.)
       (javax.imageio.ImageIO/read)))

(defn value->kind [value]
  (-> {:value value}
      api/advise
      :kind))

(deftest dummy-test)

(deftest default-predicates-test
  (is (= :kind/dataset (value->kind (tc/dataset {:x (range 3)}))))
  (is (= :kind/image (value->kind image)))
  (is (= :kind/emmy-viewers (value->kind
                             (with-meta [:div]
                               {:portal.viewer/reagent? true,
                                :portal.viewer/default :emmy.portal/reagent,}))))
  (is (= :kind/test (value->kind #'dummy-test)))
  (is (= :kind/var (value->kind #'image)))
  (is (= :kind/map (value->kind {:x 9})))
  (is (= :kind/set (value->kind #{:x 9})))
  (is (= :kind/vector (value->kind [:x 9])))
  (is (= :kind/seq (value->kind '(:x 9)))))
