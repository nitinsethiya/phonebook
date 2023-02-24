(ns phonebookapi.phonebookstoretest
  (:require [phonebookapi.phonebookstore :as sut]
            [clojure.test :refer [deftest testing is run-tests]]))

(deftest  standardize-test
  (testing "positive"
    (is (= (sut/standardize "abc") "abc"))
    (is (= (sut/standardize "  abc  ") "abc")))
  (testing "negative"
    (is (= (sut/standardize nil) ""))
    (is (= (sut/standardize 9) "9"))))


(deftest equals-ci?-test
  (testing "positive"
    (is (sut/equals-ci? "ABC" "ABC"))
    (is (sut/equals-ci? "892" "892"))
    (is (sut/equals-ci? "abC" "ABC")))
  (testing "negative"
    (is (not (sut/equals-ci? "ABC" nil)))
    (is (sut/equals-ci? nil nil))
    (is (sut/equals-ci? "abC" "ABC"))))

(deftest contains-ci?-test
  (testing "positive"
    (is (sut/contains-ci? "Potato" "ato")))
  (testing "negative"
    (is (sut/contains-ci? nil nil))
    (is (not (sut/contains-ci? nil "ato")))))


(deftest add-item!-test
  (testing "positive"
    (let [item {:first-name "Roman"
                :last-name "Ushakov"
                :phone-num 2342342423}]
      (is (not (nil? (sut/add-item! item))))))
  (testing "negative"
    (let [item {}]
      (is (nil? (sut/add-item! item))))))

(deftest search-items-test
  (testing "positive"
    (let [item {:first-name "Roman"
                :last-name "Ushakov"
                :phone-num 2342342423}]
      (is (not (nil? (sut/add-item! item))))
      (is (not (nil? (sut/search-items "rom"))))))
  (testing "negative"
    ;; testing with a non matching input - expects empty seq
    (is (nil? (seq (sut/search-items "roon"))))
    ;; testing with small string - expets nil
    (is (nil? (sut/search-items "ro")))))
