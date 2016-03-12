(ns zip-movers-example.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as r]
            [clojure.zip :as zip]
            [zip-movers.core :as zm]))

(def initial-tree
  [1 [[2 [[3]
          [4]
          [5]]]
      [6]
      [7 [[8]]]]])

(def root-loc (zip/zipper
               vector?
               (comp seq second)
               (fn [[id _] new-children] [id (vec new-children)])
               initial-tree))

(defn find-loc [pred root-loc]
  (->> (iterate zip/next root-loc)
   (take-while (complement zip/end?))
   (drop-while (comp (complement pred) zip/node))
   (first)))

(defonce selected-node-loc (r/atom (find-loc
                                    #(= 4 (first %))
                                    root-loc)))

(def root (reaction (zip/root @selected-node-loc)))

(defn selected? [node]
  (= node (zip/node @selected-node-loc)))


(defn move-deeper
  [loc]
  (when (zip/left loc)
    (let [node (zip/node loc)]
      (-> loc
        (zm/remove-and-left)
        (zip/append-child node)
        (zip/down)
        (zip/rightmost)))))

(defn move-higher
  [loc]
  (when (zip/up (zip/up loc))
    (let [node (zip/node loc)]
      (-> loc
        (zm/remove-and-up)
        (zip/insert-right node)
        (zip/right)))))

;; up   - 38
;; down - 40
(def key->selection-move-fn {38 zip/prev
                             40 zip/next})

;; s    - 83
;; w    - 87
;; a    - 65
;; d    - 68
(def key->node-move-fn {83 zm/move-right
                        87 zm/move-left
                        65 move-higher
                        68 move-deeper})

(defn handle-keys! [event]
  (let [key (.-keyCode event)]
    (when-let [selection-move-fn (key->selection-move-fn key)]
      (.preventDefault event)
      (let [new-loc (selection-move-fn @selected-node-loc)]
        (if-not (or (nil? new-loc) (zip/end? new-loc))
          (reset! selected-node-loc new-loc))))
    (when-let [node-move-fn (key->node-move-fn key)]
      (.preventDefault event)
      (when-let [new-loc (node-move-fn @selected-node-loc)]
        (reset! selected-node-loc new-loc)))))

;; -------------------------
;; Views


(defn node
  [[id children :as n]]
  [:li {:key id}
    [:div.item {:class (if (selected? n) "selected")}
      "item " id]
    [:ul
      (map node children)]])

(defn app []
  [:div
    [:h1
      [:a {:href "https://github.com/evgenykochetkov/zip-movers"} "zip-movers"]
      " usage example"]
    [:p.help
      [:a {:href "https://github.com/evgenykochetkov/zip-movers-example"} "Back to source"]]
    [:p.help
      "Move selection with up/down arrow keys"
      [:br]
      "Move selected node with WASD"]
    [:ul.root
      [node @root]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (.addEventListener js/window "keydown" handle-keys!)
  (mount-root))
