(ns zip-movers-example.prod
  (:require [zip-movers-example.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
